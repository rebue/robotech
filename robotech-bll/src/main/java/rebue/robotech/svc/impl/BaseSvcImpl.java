package rebue.robotech.svc.impl;

import com.github.dozermapper.core.Mapper;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rebue.robotech.mo.Mo;
import rebue.robotech.mybatis.MapperRootInterface;
import rebue.robotech.svc.BaseSvc;
import rebue.robotech.to.ListTo;
import rebue.wheel.idworker.IdWorker3;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

/**
 * 服务实现层的父类
 *
 * <pre>
 * 封装了一些常用的增删改查的方法，并同时提供了MyBatis和JPA两种操作数据库的方式
 * 鱼和熊掌可以兼得矣，但是在有了MyBatis之后，JPA很鸡肋，JPA大概会有种“既生瑜何生亮”的感觉
 *
 * 注意：
 * 1. 查询数据库操作的方法，不用设置默认 @Transactional
 *    在类上方已经设置默认为 readOnly=true, propagation=Propagation.SUPPORTS
 *    而涉及到 增删改 数据库操作的方法时，要设置 readOnly=false, propagation=Propagation.REQUIRED
 * 2. 事务不会针对受控异常（checked exception）回滚
 *    要想回滚事务，须抛出运行时异常(RuntimeException)
 * 3. 如果类上方不带任何参数的 @Transactional 注解时，如同下面的设置
 *    propagation(传播模式)=REQUIRED，readOnly=false，isolation(事务隔离级别)=READ_COMMITTED
 * </pre>
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseSvcImpl<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO extends ListTo, MO extends Mo<ID>, JO, MAPPER extends MapperRootInterface<MO, ID>, DAO extends JpaRepository<JO, ID>>
        implements BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, MO, JO> {

    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected MAPPER _mapper;
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected DAO    _dao;
    @Autowired
    protected Mapper _dozerMapper;

    protected IdWorker3 _idWorker;
    @Value("${robotech.appid:0}")
    private   int       _appid;

    @PostConstruct
    public void init() {
        _idWorker = new IdWorker3(_appid);
    }

    protected abstract Class<MO> getMoClass();

    /**
     * 添加记录
     *
     * @param to 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加时自动生成的ID，否则返回null
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ID add(final ADD_TO to) {
        final MO mo = _dozerMapper.map(to, getMoClass());
        if (mo.getIdType().equals("String")) {
            if (StringUtils.isBlank((CharSequence) mo.getId())) {
                mo.setId((ID) UUID.randomUUID().toString().replaceAll("-", ""));
            }
        } else if (mo.getIdType().equals("Long")) {
            // 如果id为空那么自动生成分布式id
            if (mo.getId() == null || (Long) mo.getId() == 0) {
                mo.setId((ID) _idWorker.getId());
            }
        }

        return _mapper.insertSelective(mo) == 1 ? mo.getId() : null;
    }

    /**
     * 通过ID修改记录内容
     *
     * @param to 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，返回true，否则返回false
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean modifyById(final MODIFY_TO to) {
        final MO mo = _dozerMapper.map(to, getMoClass());
        return _mapper.updateByPrimaryKeySelective(mo) == 1;
    }

    /**
     * 通过ID删除记录
     *
     * @param id 要删除记录的ID
     * @return 如果成功，且删除一条记录，返回true，否则返回false
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean delById(final ID id) {
        return _mapper.deleteByPrimaryKey(id) == 1;
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
        final MO mo = _dozerMapper.map(to, getMoClass());
        return _mapper.deleteSelective(mo);
    }

    /**
     * 根据条件获取一条记录
     *
     * @param qo 要获取记录需要符合的条件
     */
    @Override
    public MO getOne(final ONE_TO qo) {
        final MO mo = _dozerMapper.map(qo, getMoClass());
        return _mapper.selectOne(mo).orElse(null);
    }

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     * @return MyBatis Model对象
     */
    @Override
    public MO getById(final ID id) {
        return _mapper.selectByPrimaryKey(id).orElse(null);
    }

    /**
     * 根据ID获取一条JPA对象的记录
     *
     * @param id 要获取对象的ID
     * @return JPA对象
     */
    @Override
    public JO getJoById(final ID id) {
        return _dao.findById(id).orElse(null);
    }

    /**
     * 判断指定ID的记录是否存在
     */
    @Override
    public Boolean existById(final ID id) {
        return _mapper.existByPrimaryKey(id);
    }

    /**
     * 判断符合条件的记录是否存在
     */
    @Override
    public Boolean existSelective(final ONE_TO qo) {
        final MO mo = _dozerMapper.map(qo, getMoClass());
        return _mapper.existSelective(mo);
    }

    /**
     * 统计符合条件的记录数
     */
    @Override
    public Long countSelective(final ONE_TO qo) {
        final MO mo = _dozerMapper.map(qo, getMoClass());
        return _mapper.countSelective(mo);
    }

    /**
     * 查询列表
     *
     * @param qo 查询条件
     * @return 查询列表
     */
    @Override
    public List<MO> listAll(final LIST_TO qo) {
        if (qo == null) {
            return _mapper.select(c -> c);
        } else {
            final MO mo = _dozerMapper.map(qo, getMoClass());
            return _mapper.selectSelective(mo);
        }
    }

    /**
     * 查询JPA对象列表
     *
     * @return 查询JPA对象列表
     */
    @Override
    public List<JO> listJoAll() {
        return _dao.findAll();
    }

    /**
     * 分页查询列表
     *
     * @param qo 查询条件
     * @return 查询到的分页信息
     */
    @Override
    public PageInfo<MO> list(final LIST_TO qo) {
        final MO      mo     = _dozerMapper.map(qo, getMoClass());
        final ISelect select = () -> _mapper.selectSelective(mo);
        if (qo.getOrderBy() == null) {
            return PageMethod.startPage(qo.getPageNum(), qo.getPageSize()).doSelectPageInfo(select);
        } else {
            return PageMethod.startPage(qo.getPageNum(), qo.getPageSize(), qo.getOrderBy()).doSelectPageInfo(select);
        }
    }

}
