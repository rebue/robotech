package rebue.robotech.svc.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.mo.Mo;
import rebue.robotech.mybatis.MapperRootInterface;
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
public abstract class BaseSvcImpl<ID, JO, DAO extends JpaRepository<JO, ID>, MO extends Mo<ID>, MAPPER extends MapperRootInterface<MO, ID>>
        implements BaseSvc<ID, MO, JO> {

    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected MAPPER    _mapper;
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected DAO       _dao;
    protected IdWorker3 _idWorker;
    @Value("${robotech.appid:0}")
    private int         _appid;

    @PostConstruct
    public void init() {
        _idWorker = new IdWorker3(_appid);
    }

    /**
     * 添加
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean add(final MO mo) {
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

        return _mapper.insertSelective(mo) == 1 ? true : false;
    }

    /**
     * 修改
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean modify(final MO mo) {
        return _mapper.updateByPrimaryKeySelective(mo) == 1 ? true : false;
    }

    /**
     * 删除
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean del(final ID id) {
        return _mapper.deleteByPrimaryKey(id) == 1 ? true : false;
    }

    @Override
    public MO getOne(final MO mo) {
        return _mapper.selectOne(mo).orElse(null);
    }

    @Override
    public MO getById(final ID id) {
        return _mapper.selectByPrimaryKey(id).orElse(null);
    }

    @Override
    public JO getJoById(final ID id) {
        return _dao.findById(id).orElse(null);
    }

    @Override
    public Boolean existById(final ID id) {
        return _mapper.existByPrimaryKey(id);
    }

    @Override
    public Boolean existSelective(final MO record) {
        return _mapper.existSelective(record);
    }

    @Override
    public Long countSelective(final MO record) {
        return _mapper.countSelective(record);
    }

    @Override
    public List<MO> listAll() {
        return _mapper.select(c -> c);
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
    public PageInfo<MO> list(final MO qo, Integer pageNum, Integer pageSize, final String orderBy, Integer limitPageSize) {
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

        final ISelect select = qo == null ? () -> _mapper.select(c -> c) : () -> _mapper.selectSelective(qo);

        if (orderBy == null) {
            return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(select);
        } else {
            return PageHelper.startPage(pageNum, pageSize, orderBy).doSelectPageInfo(select);
        }
    }

}
