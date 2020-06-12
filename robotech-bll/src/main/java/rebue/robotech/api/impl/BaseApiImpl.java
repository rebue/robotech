package rebue.robotech.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import rebue.robotech.api.BaseApi;
import rebue.robotech.ro.PageRo;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;

public abstract class BaseApiImpl<ID, MO, JO, SVC extends BaseSvc<ID, MO, JO>> implements BaseApi<ID, MO> {
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected SVC _svc;

    /**
     * 添加
     */
    @Override
    public Ro add(final MO mo) {
        return _svc.add(mo);
    }

    /**
     * 修改
     */
    @Override
    public Ro modify(final MO mo) {
        return _svc.modify(mo);
    }

    /**
     * 删除
     */
    @Override
    public Ro del(final ID id) {
        return _svc.del(id);
    }

    @Override
    public MO getById(final ID id) {
        return _svc.getById(id);
    }

    @Override
    public PageRo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy,
            final Integer limitPageSize) {
        return _svc.list(qo, pageNum, pageSize, orderBy, limitPageSize);
    }

}
