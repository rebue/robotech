package rebue.robotech.svc.impl;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
import rebue.robotech.clone.CloneMapper;
import rebue.robotech.clone.MapStructMapper;
import rebue.robotech.mo.Mo;
import rebue.robotech.mybatis.MapperRootInterface;
import rebue.robotech.svc.BaseSvc;
import rebue.robotech.to.PageTo;
import rebue.robotech.vo.Vo;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.api.ra.PageRa;
import rebue.wheel.core.idworker.IdWorker3;
import rebue.wheel.core.idworker.IdWorkerUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@RefreshScope
public abstract class BaseSvcImpl<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO extends Mo<ID>, VO extends Vo<ID>, MAPPER extends MapperRootInterface<MO, ID>, CLONE_MAPPER extends CloneMapper<ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO>>
        implements BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO> {

    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected CLONE_MAPPER     cloneMapper;
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected MAPPER           mybatisMapper;
    /**
     * 注入 Map 检索器，它检索出来的数据以 Map 对象呈现
     */
    @Autowired
    protected MapSearcher      mapSearcher;
    /**
     * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
     */
    @Autowired
    protected BeanSearcher     beanSearcher;
    @Autowired(required = false)
    private   CuratorFramework _zkClient;

    /**
     * 默认分页大小
     */
    @Value("${rebue.page.default-page-size:10}")
    private Integer defaultPageSize;
    /**
     * beanSearcher当前页的名称
     */
    @Value("${bean-searcher.params.pagination.page:page}")
    private String  pageNumName;
    /**
     * beanSearcher分页的大小
     */
    @Value("${bean-searcher.params.pagination.size:size}")
    private String  pageSizeName;
    /**
     * beanSearcher起始页
     */
    @Value("${bean-searcher.params.pagination.start:0}")
    private Integer pageStart;

    /**
     * 配置idworker参数
     * "auto": 由zookeeper自动分配nodeId(nodeIdBits默认为5)
     * "auto:xx": 由zookeeper自动分配nodeId("xx"为nodeIdBits的值)
     * "nodeId:xx": 指定nodeId(xx值为0~31)
     * 不设置: 不使用zookeeper来计算id(仅用于开发或单机模式中)
     */
    @Value("${rebue.idworker}")
    private String idworker;

    /**
     * ID生成器
     */
    private IdWorker3 _idWorker;

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
     * 添加记录
     *
     * @param to 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加时自动生成的ID，否则会抛出运行时异常
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VO add(final ADD_TO to) {
        final MO mo = cloneMapper.addToMapMo(to);
        return getThisSvc().addMo(mo);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
        return getThisSvc().modifyMoById(mo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VO modifyMoById(final MO mo) {
        final Long now = System.currentTimeMillis();
        mo.setUpdateTimestamp(now);
        final int rowCount = mybatisMapper.updateByPrimaryKeySelective(mo);
        if (rowCount == 0) {
            throw new RuntimeExceptionX("修改记录异常，记录已不存在或有变动");
        }
        if (rowCount != 1) {
            throw new RuntimeExceptionX("修改记录异常，影响行数为" + rowCount);
        }
        // XXX 注意这里是this，而不是getThisSvc()，这是避免使用到了缓存
        return this.getById(mo.getId());
    }

    /**
     * 通过ID删除记录
     *
     * @param id 要删除记录的ID
     * @return 如果成功，且删除一条记录，正常返回，否则会抛出运行时异常
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
    public PageRa<VO> page(final ISelect select, final Integer pageNum, final Integer pageSize, final String orderBy) {
        // 如果传过来的分页大小大于最大分页大小，抛出异常
        if (pageSize != null && pageSize > this.getMaxPageSize()) {
            throw new IllegalArgumentException(pageSizeName + "不能大于" + this.getMaxPageSize());
        }
        PageInfo<Object> pageInfo;
        if (StringUtils.isBlank(orderBy)) {
            pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(select);
        } else {
            // 将orderBy由小驼峰格式转化为数据库规范的大写下划线格式
            final String newOrderBy = Stream.of(orderBy.split(",")).map(item -> {
                final String[] split = item.trim().split(" ");
                final String   field = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, split[0]);
                return field + (split.length > 1 ? " " + split[1] : "");
            }).collect(Collectors.joining(","));
            pageInfo = PageHelper.startPage(pageNum, pageSize, newOrderBy).doSelectPageInfo(select);
        }
        return MapStructMapper.INSTANCE.pageInfoMapPageRa(pageInfo);
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
     * 适合需要分页的查询
     *
     * @param paraMap 检索参数
     * @return { 总条数，数据列表 }
     */
    @Override
    public PageRa<VO> beanSearch(Map<String, Object> paraMap) {
        long       total  = beanSearcher.searchCount(getVoClass(), paraMap).longValue();
        PageRa<VO> pageRa = (PageRa<VO>) correctPageParam(total, paraMap);
        pageRa.setList(beanSearcher.searchList(getVoClass(), paraMap));
        return pageRa;
    }

    /**
     * 适合需要分页的查询
     *
     * @param paraMap 检索参数
     * @return { 总条数，数据列表 }
     */
    @Override
    public PageRa<?> mapSearch(Map<String, Object> paraMap) {
        long                        total  = mapSearcher.searchCount(getVoClass(), paraMap).longValue();
        PageRa<Map<String, Object>> pageRa = (PageRa<Map<String, Object>>) correctPageParam(total, paraMap);
        pageRa.setList(mapSearcher.searchList(getVoClass(), paraMap));
        return pageRa;
    }

    /**
     * bean searcher校正分页参数
     *
     * @param total   总条数
     * @param paraMap 请求的参数
     * @return 校正后的分页信息
     */
    private PageRa<?> correctPageParam(long total, Map<String, Object> paraMap) {
        Integer pageNum  = (Integer) paraMap.get(pageNumName);   // 当前页
        Integer pageSize = (Integer) paraMap.get(pageSizeName); // 分页大小

        // 如果当前页的参数为空，那么设置为起始页
        if (pageNum == null) pageNum = pageStart;
        // 如果分页大小的参数为空，那么设置为默认分页大小
        if (pageSize == null) pageSize = defaultPageSize;
        // 如果传过来的分页大小大于最大分页大小，抛出异常
        if (pageSize != null && pageSize > this.getMaxPageSize()) {
            throw new IllegalArgumentException(pageSizeName + "不能大于" + this.getMaxPageSize());
        }
        // 如果当前页小于起始页，设置为起始页
        if (pageNum < pageStart) {
            pageNum = pageStart;
            paraMap.put(pageNumName, pageNum);
        }
        // 计算总页数
        int pageCount = (int) Math.ceil((double) total / pageSize); // 总页数
        // 如果当前页数大于总页数，设置当前页数为最后一页(即总页数-起始页)
        if (pageNum > pageCount) {
            pageNum = pageCount - pageStart;
            paraMap.put(pageNumName, pageNum);
        }
        return PageRa.builder()
                .total(total)
                .pageNum(pageNum)
                .build();
    }

}
