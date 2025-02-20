package rebue.robotech.svc.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.mybatis.dynamic.sql.exception.NonRenderingWhereClauseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.operator.InList;
import cn.zhxu.bs.operator.OrLike;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.MapUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import rebue.robotech.beansearcher.cst.SysParamCst;
import rebue.robotech.clone.CloneMapper;
import rebue.robotech.mo.Mo;
import rebue.robotech.mybatis.MapperRootInterface;
import rebue.robotech.svc.BaseSvc;
import rebue.robotech.to.ModifyTo;
import rebue.robotech.to.PageTo;
import rebue.robotech.vo.Vo;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.api.ra.PageRa;
import rebue.wheel.core.idworker.IdWorker3;
import rebue.wheel.core.idworker.IdWorkerUtils;

/**
 * 服务实现层的父类
 *
 * <pre>
 * 封装了一些常用的增删改查的方法
 *
 * 注意：
 * 1. 查询数据库操作的方法，不用设置默认 @Transactional
 *    在类上方已经设置默认为 readOnly=true, propagation=Propagation.SUPPORTS
 *    而涉及到 增删改 数据库操作的方法时，要设置 readOnly=false, propagation=Propagation.REQUIRED
 * 2. 事务不会针对受控异常（checked exception）回滚
 *    要想回滚事务，须抛出运行时异常(RuntimeException)
 * 3. 如果类上方不带任何参数的 @Transactional 注解时，如同下面的设置
 *    propagation(传播模式)=REQUIRED，readOnly=false，isolation(事务隔离级别)=READ_COMMITTED
 * 4. 如果要调用自己的方法，应该使用getThisSvc()代替this来调用，这样该方法的事务才会起效
 * </pre>
 */
@Slf4j
@RefreshScope
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseSvcImpl<ID, ADD_TO, MODIFY_TO extends ModifyTo<ID>, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO extends Mo<ID>, VO extends Vo<ID>, MAPPER extends MapperRootInterface<MO, ID>, CLONE_MAPPER extends CloneMapper<ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO>>
        implements BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected CLONE_MAPPER   cloneMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected MAPPER         mybatisMapper;
    /**
     * 注入 Map 检索器，它检索出来的数据以 Map 对象呈现
     */
    @Autowired
    protected MapSearcher    mapSearcher;
    /**
     * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
     */
    @Autowired
    protected BeanSearcher   beanSearcher;
    @Autowired(required = false)
    private CuratorFramework _zkClient;

    /**
     * 默认分页大小
     */
    @Value("${rebue.page.default-page-size:10}")
    private Integer          defaultPageSize;
    /**
     * beanSearcher当前页的名称
     */
    @Value("${bean-searcher.params.pagination.page:page}")
    private String           pageNumName;
    /**
     * beanSearcher分页的大小
     */
    @Value("${bean-searcher.params.pagination.size:size}")
    private String           pageSizeName;
    /**
     * beanSearcher起始页
     */
    @Value("${bean-searcher.params.pagination.start:0}")
    private Integer          pageStart;

    /**
     * 配置idworker参数
     * "auto": 由zookeeper自动分配nodeId(nodeIdBits默认为5)
     * "auto:xx": 由zookeeper自动分配nodeId("xx"为nodeIdBits的值)
     * "nodeId:xx": 指定nodeId(xx值为0~31)
     * 不设置: 不使用zookeeper来计算id(仅用于开发或单机模式中)
     */
    @Value("${rebue.idworker}")
    private String           idworker;

    /**
     * ID生成器
     */
    protected IdWorker3      _idWorker;

    @PostConstruct
    public void init() throws Exception {
        createIdWorker();
    }

    @EventListener
    public void eventListener(EnvironmentChangeEvent event) {
        log.info("config change: {}", event.getKeys());
        createIdWorker();
    }

    private void createIdWorker() {
        _idWorker = IdWorkerUtils.create3(this, idworker, _zkClient);
    }

    /**
     * 从接口获取本服务的单例
     * XXX 如果要调用自己的方法，涉及到可能要回滚事务的，请使用getThisSvc()代替this来调用，这样该方法的事务才能回滚
     *
     * @return 本服务的单例
     */
    protected abstract BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO> getThisSvc();

    /**
     * 泛型VO的class(子类提供给基类调用-因为java中泛型擦除，JVM无法智能获取泛型的class)
     *
     * @return 泛型MO的class
     */
    protected abstract Class<VO> getVoClass();

    /**
     * 获取最大分页大小
     * 如果分页查询传过来的分页大小大于这个值，那么抛出异常
     *
     * @return 最大分页大小
     */
    protected abstract Integer getMaxPageSize();

    /**
     * 获取树分层大小(默认为3)
     *
     * @return 树分层大小
     */
    protected int getTreeLevelSize() {
        return 3;
    }

    /**
     * 获取树编码字段名(默认为treeCode)
     *
     * @return 树编码字段名
     */
    protected String getTreeCodeFieldName() {
        return "treeCode";
    }

    /**
     * 添加记录
     *
     * @param to 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加时自动生成的ID，否则会抛出运行时异常
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VO add(final ADD_TO to) {
        final MO mo = cloneMapper.addToMapMo(to);
        return this.addMo(mo);
    }

    /**
     * 添加记录
     *
     * @param mo 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加后的实体，否则会抛出运行时异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public VO addMo(final MO mo) {
        if (mo.getIdType().equals("String")) {
            if (StringUtils.isBlank((CharSequence) mo.getId())) {
                mo.setId((ID) UUID.randomUUID().toString().replace("-", ""));
            }
        } else if (mo.getIdType().equals("Long")) {
            // 如果id为空那么自动生成分布式id
            if (mo.getId() == null || (Long) mo.getId() == 0) {
                mo.setId((ID) _idWorker.getId());
            }
        }
        final Long now = System.currentTimeMillis();
        mo.setCreateTimestamp(now);
        mo.setUpdateTimestamp(now);
        final int rowCount = mybatisMapper.insertSelective(mo);
        if (rowCount != 1) {
            throw new RuntimeExceptionX("添加记录异常，影响行数为" + rowCount);
        }
        // XXX 通过调用getById，如果有缓存机制，可将新添加的记录存入缓存中
        return getThisSvc().getById(mo.getId());
    }

    /**
     * 通过ID修改记录内容
     *
     * @param to 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，正常返回，否则会抛出运行时异常
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VO modifyById(final MODIFY_TO to) {
        final MO mo = cloneMapper.modifyToMapMo(to);
        if (mo.getId() == null)
            throw new NoSuchElementException("修改记录异常，记录不存在或已被删除");
        return this.modifyMoById(mo);
    }

    /**
     * 通过ID修改记录内容
     *
     * @param mo 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，正常返回修改后的实体，否则会抛出运行时异常
     */
    @Override
    public VO modifyMoById(final MO mo) {
        final Long now = System.currentTimeMillis();
        mo.setUpdateTimestamp(now);
        final int rowCount = mybatisMapper.updateByPrimaryKeySelective(mo);
        if (rowCount == 0) {
            throw new NoSuchElementException("修改记录异常，记录不存在或已被删除");
        }
        if (rowCount != 1) {
            throw new RuntimeExceptionX("修改记录异常，影响行数为" + rowCount);
        }
        // XXX 注意这里是this，而不是getThisSvc()，这是避免使用到了缓存
        return this.getById(mo.getId());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VO save(final MO mo) {
        if (mo.getId() != null) {
            try {
                // 这里用this，修改如果没有此记录，就添加，而不是回滚
                return this.modifyMoById(mo);
            } catch (NoSuchElementException e) {
                // 找不到不用抛异常，后面进行添加的操作
            }
        }
        // 如果不存在，则添加
        return this.addMo(mo);
    }

    /**
     * 通过ID删除记录
     * 如果成功，且删除一条记录，正常返回，否则会抛出运行时异常
     *
     * @param id 要删除记录的ID
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void delById(final ID id) {
        final int rowCount = mybatisMapper.deleteByPrimaryKey(id);
        if (rowCount == 0) {
            throw new RuntimeExceptionX("删除记录异常，记录已不存在或有变动");
        }
        if (rowCount != 1) {
            throw new RuntimeExceptionX("删除记录异常，影响行数为" + rowCount);
        }
    }

    /**
     * 通过条件删除记录
     *
     * @param to 要删除记录需要符合的条件
     * @return 返回删除的记录数
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer delSelective(final DEL_TO to) {
        try {
            final MO mo = cloneMapper.delToMapMo(to);
            return mybatisMapper.deleteSelective(mo);
        } catch (NonRenderingWhereClauseException e) {
            throw new RuntimeExceptionX("不能执行不带条件的删除操作");
        }
    }

    /**
     * 根据条件获取一条记录
     *
     * @param qc 要获取记录需要符合的条件，如果查找不到则返回null
     */
    @Override
    public VO getOne(final ONE_TO qc) {
        final MO mo = cloneMapper.oneToMapMo(qc);
        return cloneMapper.moMapVo(mybatisMapper.selectOne(mo).orElse(null));
    }

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     * @return MyBatis Model对象，如果查找不到则返回null
     */
    @Override
    public VO getById(final ID id) {
        return cloneMapper.moMapVo(mybatisMapper.selectByPrimaryKey(id).orElse(null));
    }

    /**
     * 判断指定ID的记录是否存在
     *
     * @param id 要查询对象的ID
     * @return 是否存在
     */
    @Override
    public Boolean existById(final ID id) {
        return mybatisMapper.existByPrimaryKey(id);
    }

    /**
     * 判断符合条件的记录是否存在
     *
     * @param qc 查询条件
     * @return 是否存在
     */
    @Override
    public Boolean existSelective(final ONE_TO qc) {
        final MO mo = cloneMapper.oneToMapMo(qc);
        return mybatisMapper.existSelective(mo);
    }

    /**
     * 统计符合条件的记录数
     *
     * @param qc 查询条件
     * @return 符合条件的记录数
     */
    @Override
    public Long countSelective(final ONE_TO qc) {
        final MO mo = cloneMapper.oneToMapMo(qc);
        return mybatisMapper.countSelective(mo);
    }

    /**
     * 条件查询
     *
     * @param qc 查询条件
     * @return 查询列表
     */
    @Override
    public List<VO> list(final LIST_TO qc) {
        final MO mo = cloneMapper.listToMapMo(qc);
        return cloneMapper.moListMapVoList(mybatisMapper.selectSelective(mo));
    }

    /**
     * 根据ID列表查询
     *
     * @param ids ID列表
     * @return 查询列表
     */
    @Override
    public List<VO> listIn(final List<ID> ids) {
        return cloneMapper.moListMapVoList(mybatisMapper.selectIn(ids));
    }

    /**
     * 查询所有
     *
     * @return 查询列表
     */
    @Override
    public List<VO> listAll() {
        return cloneMapper.moListMapVoList(mybatisMapper.select(c -> c));
    }

    /**
     * 分页查询列表
     *
     * @param select   选择器
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param orderBy  排序字段
     * @return 查询到的分页信息
     */
    @Override
    public PageRa<VO> page(@NotNull final ISelect select, @NotNull final Integer pageNum, @NotNull final Integer pageSize, final String orderBy) {
        // 如果传过来的分页大小大于最大分页大小，抛出异常
        if (pageSize > this.getMaxPageSize()) {
            throw new IllegalArgumentException(pageSizeName + "不能大于" + this.getMaxPageSize());
        }
        PageInfo<Object> pageInfo;
        if (StringUtils.isBlank(orderBy)) {
            try (Page<Object> page = PageHelper.startPage(pageNum, pageSize)) {
                pageInfo = page.doSelectPageInfo(select);
            }
        } else {
            // 将orderBy由小驼峰格式转化为数据库规范的大写下划线格式
            final String newOrderBy = Stream.of(orderBy.split(",")).map(item -> {
                final String[] split = item.trim().split(" ");
                final String   field = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, split[0]);
                return field + (split.length > 1 ? " " + split[1] : "");
            }).collect(Collectors.joining(","));
            try (Page<Object> page = PageHelper.startPage(pageNum, pageSize, newOrderBy)) {
                pageInfo = page.doSelectPageInfo(select);
            }
        }
        return cloneMapper.pageInfoMapPageRa(pageInfo);
    }

    /**
     * 分页查询列表
     *
     * @param qc 查询条件
     * @return 查询到的分页信息
     */
    @Override
    public PageRa<VO> page(final PAGE_TO qc) {
        final MO      mo     = cloneMapper.pageToMapMo(qc);
        final ISelect select = () -> mybatisMapper.selectSelective(mo);
        return getThisSvc().page(select, qc.getPageNum(), qc.getPageSize(), qc.getOrderBy());
    }

    /**
     * 根据条件查询一条记录
     *
     * @param paraMap 检索参数
     * @return 一条记录，如果查找不到则返回null
     */
    @Override
    public VO beanSearchOne(Map<String, Object> paraMap) {
        return beanSearcher.searchFirst(getVoClass(), paraMap);
    }

    /**
     * 根据ID获取一条记录
     *
     * @param id 要获取对象的ID
     * @return 一条记录，如果查找不到则返回null
     */
    @Override
    public VO beanSearchById(final ID id) {
        return getThisSvc().beanSearchOne(Map.of("id", id));
    }

    /**
     * 根据条件查询一条记录
     *
     * @param paraMap 检索参数
     * @return 一条记录，如果查找不到则返回null
     */
    @Override
    public Map<String, Object> mapSearchOne(Map<String, Object> paraMap) {
        return mapSearcher.searchFirst(getVoClass(), paraMap);
    }

    /**
     * 根据ID获取一条记录
     *
     * @param id 要获取对象的ID
     * @return 一条记录，如果查找不到则返回null
     */
    @Override
    public Map<String, Object> mapSearchById(final ID id) {
        return getThisSvc().mapSearchOne(Map.of("id", id));
    }

    /**
     * 查询所有数据列表
     *
     * @param paraMap 检索参数
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     * @return 数据列表
     */
    @Override
    public List<VO> beanSearchList(Map<String, Object> paraMap) {
        return getThisSvc().beanSearchList0(getVoClass(), paraMap);
    }

    /**
     * 查询所有数据列表(用于自定义查询的VO类)
     *
     * @param voClazz 查询与数据库映射的VO类
     * @param paraMap 检索参数
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     * @return 数据列表
     */
    @Override
    public <T> List<T> beanSearchList0(Class<T> voClazz, Map<String, Object> paraMap) {
        // noinspection unchecked
        return (List<T>) this.searchList0(voClazz, paraMap, true);
    }

    /**
     * 查询所有数据列表(用于自定义查询的VO类)
     * 
     * @param paraMap 检索参数
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @return 数据列表
     */
    @Override
    public List<Map<String, Object>> mapSearchList(Map<String, Object> paraMap) {
        // noinspection unchecked
        return (List<Map<String, Object>>) this.searchList0(getVoClass(), paraMap, false);
    }

    /**
     * 查询所有数据列表(用于自定义查询的VO类)
     *
     * @param voClazz 查询与数据库映射的VO类
     * @param paraMap 检索参数
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @return 数据列表
     */
    @Override
    public <T> List<T> searchList(Class<T> voClazz, Map<String, Object> paraMap) {
        // noinspection unchecked
        return (List<T>) this.searchList0(voClazz, paraMap, true);
    }

    /**
     * 查询所有数据列表(统一了 mapSearcher 和 beanSearcher 的查询)
     *
     * @param voClazz        查询与数据库映射的VO类
     * @param paraMap        检索参数
     *                       SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                       SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                       SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @param isBeanSearcher 是否使用beanSearcher查询(true 使用beanSearcher查询，false 使用mapSearcher查询)
     * @return 数据列表
     */
    private List<?> searchList0(Class<?> voClazz, Map<String, Object> paraMap, boolean isBeanSearcher) {
        if (paraMap == null) {
            paraMap = new LinkedHashMap<>();
        }
        // 获取树形结构的层级
        Integer treeLevel = Optional.ofNullable(paraMap.remove(SysParamCst.TREE_LEVEL))
                .map(o -> Integer.parseInt(o.toString()))
                .orElse(null);
        // 树形结构查询
        if (treeLevel != null) {
            // 是否带自定义查询条件
            boolean customCondition    = false;
            Object  customConditionObj = paraMap.remove(SysParamCst.CUSTOM_CONDITION);
            if (customConditionObj != null) {
                customCondition = Boolean.parseBoolean(customConditionObj.toString());
            }
            // 带自定义查询条件
            if (customCondition) {
                // 获取符合条件的 treeCode 集合
                Set<String> treeCodes = listTreeCodes(paraMap, treeLevel);
                // 更换查询参数为 treeCode 的集合
                paraMap = MapUtils.builder()
                        .field(this.getTreeCodeFieldName(), treeCodes).op(InList.class)
                        .build();
                return getSearchAll(voClazz, paraMap, isBeanSearcher);
            }

            // else 不带自定义查询条件
            // 添加树形查询参数
            this.addTreeSearchParams(treeLevel, paraMap);
        }
        // 获取所有数据列表
        return getSearchAll(voClazz, paraMap, isBeanSearcher);
    }

    /**
     * 分页查询
     *
     * @param paraMap 检索参数
     *                page 为空则设置为起始页
     *                size 为空则使用默认分页大小
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     * @return { 总条数，数据列表 }
     */
    @Override
    public PageRa<VO> beanSearch(Map<String, Object> paraMap) {
        return getThisSvc().beanSearch0(getVoClass(), paraMap);
    }

    /**
     * 分页查询(用于自定义查询的VO类)
     *
     * @param voClazz 查询与数据库映射的VO类
     * @param paraMap 检索参数
     *                page 为空则设置为起始页
     *                size 为空则使用默认分页大小
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @return { 总条数，数据列表 }
     */
    @Override
    public <T> PageRa<T> beanSearch0(Class<T> voClazz, Map<String, Object> paraMap) {
        // noinspection unchecked
        return (PageRa<T>) this.search0(voClazz, paraMap, true);
    }

    /**
     * 分页查询
     *
     * @param paraMap 检索参数
     *                page 为空则设置为起始页
     *                size 为空则使用默认分页大小
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     * @return { 总条数，数据列表 }
     */
    @Override
    public PageRa<?> mapSearch(Map<String, Object> paraMap) {
        return this.search0(getVoClass(), paraMap, false);
    }

    /**
     * 分页查询(用于自定义查询的VO类)
     *
     * @param voClazz 查询与数据库映射的VO类
     * @param paraMap 检索参数
     *                page 为空则设置为起始页
     *                size 为空则使用默认分页大小
     *                SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @return { 总条数，数据列表 }
     */
    @Override
    public <T> PageRa<T> search(Class<T> voClazz, Map<String, Object> paraMap) {
        // noinspection unchecked
        return (PageRa<T>) this.search0(voClazz, paraMap, true);
    }

    /**
     * 分页查询(统一了 mapSearcher 和 beanSearcher 的查询)
     *
     * @param voClazz        查询与数据库映射的VO类
     * @param paraMap        检索参数
     *                       page 为空则设置为起始页
     *                       size 为空则使用默认分页大小
     *                       SYS_TREE_LEVEL 查询树形结构的层级(为空则不查询树形结构，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     *                       SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     *                       SYS_CUSTOM_CONDITION 是否自定义查询条件(为空或false 不自定义查询条件，为true 才自定义查询条件)
     * @param isBeanSearcher 是否使用beanSearcher查询(true 使用beanSearcher查询，false 使用mapSearcher查询)
     * @return { 总条数，数据列表 }
     */
    private PageRa<?> search0(Class voClazz, Map<String, Object> paraMap, boolean isBeanSearcher) {
        if (paraMap == null) {
            paraMap = new LinkedHashMap<>();
        }
        // 获取树形结构的层级
        Integer treeLevel = Optional.ofNullable(paraMap.remove(SysParamCst.TREE_LEVEL))
                .map(item -> Integer.parseInt(item.toString()))
                .orElse(null);
        // 总记录数
        long    total;
        // 非树形结构查询
        if (treeLevel == null) {
            // noinspection unchecked
            total = beanSearcher.searchCount(voClazz, paraMap).longValue();
            // 校正分页参数
            PageRa<?> pageRa = correctPageParam(total, paraMap);
            List      list;
            if (total == 0) {
                list = new LinkedList();
            } else {
                // 获取数据列表
                list = getSearchList(voClazz, paraMap, isBeanSearcher);
            }
            // noinspection unchecked
            pageRa.setList(list);
            return pageRa;
        }

        // else 树形结构查询
        // 是否带自定义查询条件
        boolean customCondition    = false;
        Object  customConditionObj = paraMap.remove(SysParamCst.CUSTOM_CONDITION);
        if (customConditionObj != null) {
            customCondition = Boolean.parseBoolean(customConditionObj.toString());
        }
        // 带自定义查询条件
        if (customCondition) {
            // 获取符合条件的 treeCode 集合
            Set<String>  treeCodes           = listTreeCodes(paraMap, treeLevel);
            // 获取符合条件的第一层的 treeCode 集合
            List<String> firstLevelTreeCodes = treeCodes.stream()
                    .filter(treeCode -> treeCode.length() == this.getTreeLevelSize())
                    .toList();
            total = firstLevelTreeCodes.size();
            // 校正分页参数
            PageRa<?> pageRa = correctPageParam(total, paraMap);
            List      list;
            if (total == 0) {
                list = new LinkedList();
            } else {
                // 第几页
                int pageNum    = pageRa.getPageNum() - pageStart;
                int pageSize   = pageRa.getPageSize();
                int beginIndex = pageNum * pageSize;
                int endIndex   = (pageNum + 1) * pageSize;
                if (endIndex > total) {
                    endIndex = (int) (total);
                }
                // 获取当前页记录的第一层 treeCode 集合
                List<String> firstLevelTreeCodesPage = firstLevelTreeCodes.subList(beginIndex, endIndex);
                // 过滤符合的记录
                List<String> treeCodesPage           = treeCodes.stream().filter(treeCode -> {
                                                         for (String firstLevelTreeCode : firstLevelTreeCodesPage) {
                                                             if (treeCode.startsWith(firstLevelTreeCode))
                                                                 return true;
                                                         }
                                                         return false;
                                                     }).toList();
                // 更换查询参数为 treeCode 的集合
                paraMap = MapUtils.builder()
                        .field(this.getTreeCodeFieldName(), treeCodesPage).op(InList.class)
                        .build();
                list    = getSearchAll(voClazz, paraMap, isBeanSearcher);
            }
            // noinspection unchecked
            pageRa.setList(list);
            return pageRa;
        }

        // else 不带自定义查询条件，查询第一层的记录数为总记录数
        // 添加树形查询参数
        this.addTreeSearchParams(1, paraMap);
        PageRa<?>    pageRa        = mapSearch(paraMap);
        List<String> treeCodesPage = pageRa.getList().stream()
                .map(item -> ((Map) item).get(this.getTreeCodeFieldName()).toString()).toList();
        // 更换查询参数为 treeCode 的集合
        MapBuilder   builder       = MapUtils.builder();
        builder.or(o -> {
            for (String treeCode : treeCodesPage) {
                o.field(this.getTreeCodeFieldName(), treeCode);
                o.field(this.getTreeCodeFieldName(), treeCode + "_".repeat(this.getTreeLevelSize())).op(OrLike.class);
            }
        });
        paraMap = builder.build();
        List list = getSearchAll(voClazz, paraMap, isBeanSearcher);
        // noinspection unchecked
        pageRa.setList(list);
        return pageRa;
    }

    /**
     * 获取所有数据列表
     * 统一了 mapSearcher 和 beanSearcher 的查询
     * 忽略分页参数
     *
     * @param voClazz        查询与数据库映射的VO类
     * @param paraMap        检索参数
     * @param isBeanSearcher 是否使用beanSearcher查询(true 使用beanSearcher查询，false 使用mapSearcher查询)
     * @return 数据列表
     */
    private List<?> getSearchAll(Class voClazz, Map<String, Object> paraMap, boolean isBeanSearcher) {
        // noinspection unchecked
        return isBeanSearcher ? beanSearcher.searchAll(voClazz, paraMap) : mapSearcher.searchAll(voClazz, paraMap);
    }

    /**
     * 获取分页数据列表(统一了 mapSearcher 和 beanSearcher 的查询)
     *
     * @param voClazz        查询与数据库映射的VO类
     * @param paraMap        检索参数
     * @param isBeanSearcher 是否使用beanSearcher查询(true 使用beanSearcher查询，false 使用mapSearcher查询)
     * @return 数据列表
     */
    private List<?> getSearchList(Class voClazz, Map<String, Object> paraMap, boolean isBeanSearcher) {
        // noinspection unchecked
        return isBeanSearcher ? beanSearcher.searchList(voClazz, paraMap) : mapSearcher.searchList(voClazz, paraMap);
    }

    /**
     * bean searcher校正分页参数
     *
     * @param total   总条数
     * @param paraMap 请求的参数
     * @return 校正后的分页信息
     */
    private PageRa<?> correctPageParam(long total, Map<String, Object> paraMap) {
        // 获取分页大小参数
        Object size     = paraMap.remove(pageSizeName);
        // 分页大小(如果参数为空，那么设置为默认分页大小)
        int    pageSize = size == null ? defaultPageSize : Integer.parseInt(size.toString());
        // 如果传过来的分页大小大于最大分页大小，抛出异常
        if (pageSize > this.getMaxPageSize()) {
            throw new IllegalArgumentException(pageSizeName + "不能大于" + this.getMaxPageSize());
        }

        // 计算总页数
        int pageCount = total == 0 ? 0 : (int) Math.ceil((double) total / pageSize);

        // 获取当前页号参数
        int pageNum;
        // 总数为0，当前页为1
        if (total == 0) {
            pageNum = 1;
        } else {
            Object page = paraMap.remove(pageNumName);
            // 当前页号(如果参数为空，那么设置为默认起始页号)
            pageNum = page == null ? pageStart : Integer.parseInt(page.toString());

            // 如果当前页号小于起始页号，设置为起始页号
            if (pageNum < pageStart) {
                pageNum = pageStart;
            }
            // 如果当前页号大于总页数，设置当前页数为最后一页(即总页数-起始页)
            else if (pageNum > pageCount) {
                pageNum = pageCount + 1 - pageStart;
            }
        }

        // 纠正后回填参数
        paraMap.put(pageSizeName, pageSize);
        paraMap.put(pageNumName, pageNum);
        return PageRa.builder()
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }

    /**
     * 添加树形结构查询参数
     * 
     * @param treeLevel 查询树形结构的层级(0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     * @param paraMap   检索参数
     *                  SYS_PARENT_ID 父节点ID(为空则从第一层节点开始查询，不为空则查询指定ID的子节点)
     */
    private void addTreeSearchParams(int treeLevel, Map<String, Object> paraMap) {
        // 父节点ID
        @SuppressWarnings("unchecked")
        ID           parentId = (ID) paraMap.remove(SysParamCst.PARENT_ID);
        List<String> likeTreeCode;
        if (parentId == null) {
            // 如何父ID为空且树形层级为0，意为查询所有，则不用添加查询条件
            if (treeLevel == 0) {
                return;
            }
            // 模糊查询的树编码
            likeTreeCode = addLikeTreeCode(treeLevel, null);
        } else {
            Map<String, Object> parentNode = getThisSvc().mapSearchById(parentId);
            String              treeCode   = parentNode.get(this.getTreeCodeFieldName()).toString();
            // 模糊查询的树编码
            likeTreeCode = (treeLevel == 0 ? List.of(treeCode + "%") : addLikeTreeCode(treeLevel, treeCode));
        }
        Map<String, Object> treeMap = MapUtils.builder()
                .field(this.getTreeCodeFieldName(), likeTreeCode).op(OrLike.class)
                .build();
        paraMap.putAll(treeMap);
    }

    /**
     * 添加树形编码模糊查询的参数
     *
     * @param treeLevel      查询树形结构的层级(不能为空，0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     * @param parentTreeCode 父节点的树形编码(为空则从第一层节点开始查询)
     * @return 查询字符串列表
     */
    private List<String> addLikeTreeCode(int treeLevel, String parentTreeCode) {
        List<String> list = new LinkedList<>();
        for (int i = 1; i <= treeLevel; i++) {
            list.add((parentTreeCode == null ? "" : parentTreeCode) + "_".repeat(this.getTreeLevelSize() * i));
        }
        return list;
    }

    /**
     * 获取符合条件的 treeCode 集合(包括符合条件节点的父节点)
     *
     * @param paraMap   检索参数
     * @param treeLevel 查询树形结构的层级(0-表示查询所有层，1-表示查询1层，2-表示查询2层，以此类推)
     * @return 符合条件的 treeCode 集合(包括符合条件节点的父节点)
     */
    private Set<String> listTreeCodes(Map<String, Object> paraMap, Integer treeLevel) {
        // 添加树形查询参数
        this.addTreeSearchParams(treeLevel, paraMap);
        // 查询符合条件的记录
        // List<Map<String, Object>> voMaps = getThisSvc().mapSearchList(paraMap);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> voMaps    = (List<Map<String, Object>>) getSearchList(getVoClass(), paraMap, false);
        // 遍历符合条件的记录，生成查询 treeCode 的集合
        Set<String>               treeCodes = new LinkedHashSet<>();
        for (Map<String, Object> voMap : voMaps) {
            final String curTreeCode       = voMap.get(this.getTreeCodeFieldName()).toString();
            int          curTreeCodeLength = curTreeCode.length();
            int          curTreeCodeLevel  = curTreeCodeLength / this.getTreeLevelSize();
            for (int i = 1; i <= curTreeCodeLevel; i++) {
                treeCodes.add(curTreeCode.substring(0, i * this.getTreeLevelSize()));
            }
        }
        return treeCodes;
    }
}
