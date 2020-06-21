package rebue.robotech.svc.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.mapper.MybatisBaseMapper;
import rebue.robotech.ra.CountRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.ListRa;
import rebue.robotech.ra.OkRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;
import rebue.wheel.idworker.IdWorker3;

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
@Slf4j
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseSvcImpl<ID, JO, DAO extends JpaRepository<JO, ID>, MO, MAPPER extends MybatisBaseMapper<MO, ID>> implements BaseSvc<ID, MO, JO> {

    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected MAPPER    _mapper;
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected DAO       _dao;

    @Value("${robotech.appid:0}")
    private int         _appid;
    protected IdWorker3 _idWorker;

    @PostConstruct
    public void init() {
        _idWorker = new IdWorker3(_appid);
    }

    /**
     * 添加
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro<IdRa<ID>> add(final MO mo) {
        final int result = _mapper.insertSelective(mo);
        if (result == 1) {
            return new Ro<>(ResultDic.SUCCESS, "添加成功");
        } else {
            return new Ro<>(ResultDic.FAIL, "添加失败");
        }
    }

    /**
     * 修改
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro<?> modify(final MO mo) {
        if (_mapper.updateByPrimaryKeySelective(mo) == 1) {
            return new Ro<>(ResultDic.SUCCESS, "修改成功");
        } else {
            return new Ro<>(ResultDic.FAIL, "修改失败");
        }
    }

    /**
     * 删除
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro<?> del(final ID id) {
        final int result = _mapper.deleteByPrimaryKey(id);
        if (result == 1) {
            return new Ro<>(ResultDic.SUCCESS, "删除成功");
        } else {
            return new Ro<>(ResultDic.FAIL, "删除失败，找不到该记录");
        }
    }

    @Override
    public Ro<PojoRa<MO>> getOne(final MO mo) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new PojoRa<>(_mapper.selectOne(mo).orElse(null)));
    }

    @Override
    public Ro<PojoRa<MO>> getById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new PojoRa<>(_mapper.selectByPrimaryKey(id).orElse(null)));
    }

    @Override
    public Ro<PojoRa<JO>> getJoById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new PojoRa<>(_dao.findById(id).orElse(null)));
    }

    @Override
    public Ro<OkRa> existById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new OkRa(_mapper.existByPrimaryKey(id)));
    }

    @Override
    public Ro<OkRa> existSelective(final MO record) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new OkRa(_mapper.existSelective(record)));
    }

    @Override
    public Ro<CountRa> countSelective(final MO record) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new CountRa(_mapper.countSelective(record)));
    }

    @Override
    public Ro<ListRa<MO>> listAll() {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new ListRa<>(_mapper.select(c -> c)));
    }

    @Override
    public Ro<ListRa<JO>> listJoAll() {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new ListRa<>(_dao.findAll()));
    }

    @Override
    public Ro<ListRa<MO>> list(final MO mo) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new ListRa<>(_mapper.selectSelective(mo)));
    }

    @Override
    public Ro<PageRa<MO>> list(final MO qo, Integer pageNum, Integer pageSize, final String orderBy, Integer limitPageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }

        if (limitPageSize == null) {
            limitPageSize = 50;
        }
        if (pageSize > limitPageSize) {
            final String msg = "pageSize不能大于" + limitPageSize;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        PageInfo<MO> pageInfo;
        if (orderBy == null) {
            pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> _mapper.selectSelective(qo));
        } else {
            pageInfo = PageHelper.startPage(pageNum, pageSize, orderBy).doSelectPageInfo(() -> _mapper.selectSelective(qo));
        }

        return new Ro<>(ResultDic.SUCCESS, "分页查询成功", null, new PageRa<>(pageInfo));
    }

}
