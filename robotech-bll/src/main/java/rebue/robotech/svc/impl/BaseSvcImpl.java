package rebue.robotech.svc.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import rebue.robotech.mapper.MybatisBaseMapper;
import rebue.robotech.svc.BaseSvc;
import rebue.wheel.idworker.IdWorker3;

@Service
/**
 * <pre>
 * 在单独使用不带任何参数 的 @Transactional 注释时，
 * propagation(传播模式)=REQUIRED，readOnly=false，
 * isolation(事务隔离级别)=READ_COMMITTED，
 * 而且事务不会针对受控异常（checked exception）回滚。
 * 注意：
 * 一般是查询的数据库操作，默认设置readOnly=true, propagation=Propagation.SUPPORTS
 * 而涉及到增删改的数据库操作的方法，要设置 readOnly=false, propagation=Propagation.REQUIRED
 * </pre>
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class BaseSvcImpl<ID, JO, DAO extends JpaRepository<JO, ID>, MO, MAPPER extends MybatisBaseMapper<MO, ID>> implements BaseSvc<ID, MO, JO> {

    private final static Logger _log = LoggerFactory.getLogger(BaseSvcImpl.class);

    @Autowired
    protected MAPPER            _mapper;

    @Autowired
    protected DAO               _dao;

    @Value("${appid:0}")
    private int                 _appid;

    protected IdWorker3         _idWorker;

    @PostConstruct
    public void init() {
        _idWorker = new IdWorker3(_appid);
    }

    @Override
    public void test() {

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(final MO mo) {
        _log.info("add: {}", mo);
        return _mapper.insertSelective(mo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int modify(final MO mo) {
        _log.info("modify: {}", mo);
        return _mapper.updateByPrimaryKeySelective(mo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int del(final ID id) {
        _log.info("del: {}", id);
        return _mapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<MO> listAll() {
        _log.info("listAll");
        return _mapper.selectAll();
    }

    @Override
    public List<MO> list(final MO mo) {
        _log.info("list: {}", mo);
        return _mapper.selectSelective(mo);
    }

    @Override
    public MO getOne(final MO mo) {
        _log.info("getOne: {}", mo);
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
    public PageInfo<MO> list(final MO qo, final int pageNum, final int pageSize) {
        _log.info("list: qo-{}; pageNum-{}; pageSize-{}", qo, pageNum, pageSize);
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> _mapper.selectSelective(qo));
    }

    @Override
    public PageInfo<MO> list(final MO qo, final int pageNum, final int pageSize, final String orderBy) {
        _log.info("list: qo-{}; pageNum-{}; orderBy-{}; pageSize-{}", qo, pageNum, pageSize, orderBy);
        return PageHelper.startPage(pageNum, pageSize, orderBy).doSelectPageInfo(() -> _mapper.selectSelective(qo));
    }

    @Override
    public MO getById(final ID id) {
        _log.info("getById: {}", id);
        return _mapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean existByPrimaryKey(final ID id) {
        _log.info("existByPrimaryKey: {}", id);
        return _mapper.existByPrimaryKey(id);
    }

    @Override
    public boolean existSelective(final MO mo) {
        _log.info("existSelective: {}", mo);
        return _mapper.existSelective(mo);
    }

}
