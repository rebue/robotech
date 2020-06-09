package rebue.robotech.svc.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.mapper.MybatisBaseMapper;
import rebue.robotech.ro.PageRo;
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
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Slf4j
public abstract class BaseSvcImpl<ID, JO, DAO extends JpaRepository<JO, ID>, MO, MAPPER extends MybatisBaseMapper<MO, ID>> implements BaseSvc<ID, MO, JO> {

    @Autowired
    protected MAPPER    _mapper;

    @Autowired
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
    public Ro add0(final MO mo) {
        try {
            final int result = _mapper.insertSelective(mo);
            if (result == 1) {
                return new Ro(ResultDic.SUCCESS, "添加成功");
            } else {
                return new Ro(ResultDic.FAIL, "添加失败");
            }
        } catch (final DuplicateKeyException e) {
            final String msg = "添加失败，唯一键重复：" + e.getCause().getMessage();
            log.error(msg + ": mo-" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        } catch (final RuntimeException e) {
            final String msg = "添加失败，出现运行时异常";
            log.error(msg + ": mo-" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    /**
     * 修改
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro modify(final MO mo) {
        try {
            if (_mapper.updateByPrimaryKeySelective(mo) == 1) {
                return new Ro(ResultDic.SUCCESS, "修改成功");
            } else {
                final String msg = "修改失败";
                log.error("{}: mo-{}", msg, mo);
                return new Ro(ResultDic.FAIL, msg);
            }
        } catch (final DuplicateKeyException e) {
            final String msg = "修改失败，唯一键重复：" + e.getCause().getMessage();
            log.error(msg + ": mo=" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        } catch (final RuntimeException e) {
            final String msg = "修改失败，出现运行时异常";
            log.error(msg + ": mo-" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    /**
     * 删除
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro del(final ID id) {
        final int result = _mapper.deleteByPrimaryKey(id);
        if (result == 1) {
            return new Ro(ResultDic.SUCCESS, "删除成功");
        } else {
            final String msg = "删除失败，找不到该记录";
            log.error("{}: id-{}", msg, id);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertSelective(final MO mo) {
        return _mapper.insertSelective(mo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateByPrimaryKeySelective(final MO mo) {
        return _mapper.updateByPrimaryKeySelective(mo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteByPrimaryKey(final ID id) {
        return _mapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<MO> listAll() {
        return _mapper.selectAll();
    }

    @Override
    public List<JO> listJoAll() {
        return _dao.findAll();
    }

    @Override
    public List<MO> list(final MO mo) {
        return _mapper.selectSelective(mo);
    }

    @Override
    public MO getOne(final MO mo) {
        final List<MO> list = _mapper.selectSelective(mo);
        if (list.size() <= 0) {
            return null;
        } else if (list.size() > 1) {
            throw new RuntimeException("query row count>1");
        } else {
            return list.get(0);
        }
    }

    @Override
    public MO getById(final ID id) {
        return _mapper.selectByPrimaryKey(id);
    }

    @Override
    public JO getJoById(final ID id) {
        return _dao.findById(id).orElse(null);
    }

    @Override
    public boolean existByPrimaryKey(final ID id) {
        return _mapper.existByPrimaryKey(id);
    }

    @Override
    public boolean existSelective(final MO mo) {
        return _mapper.existSelective(mo);
    }

    @Override
    public PageRo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize) {
        return list(qo, pageNum, pageSize, null, null);
    }

    @Override
    public PageRo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize, final Integer limitPageSize) {
        return list(qo, pageNum, pageSize, null, limitPageSize);
    }

    @Override
    public PageRo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy) {
        return list(qo, pageNum, pageSize, null, null);
    }

    @Override
    public PageRo<MO> list(final MO qo, Integer pageNum, Integer pageSize, final String orderBy, Integer limitPageSize) {
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

        return new PageRo<>(ResultDic.SUCCESS, "分页查询成功", pageInfo);
    }

}
