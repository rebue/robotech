package rebue.robotech.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import rebue.robotech.api.BaseApi;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;

public abstract class BaseApiImpl<ID, MO, JO, SVC extends BaseSvc<ID, MO, JO>> implements BaseApi<ID, MO> {
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected SVC svc;

    /**
     * 添加
     */
    @Override
    public Ro<IdRa<ID>> add(final MO mo) {
        return svc.add(mo);
    }

    /**
     * 修改
     */
    @Override
    public Ro<?> modify(final MO mo) {
        return svc.modify(mo);
    }

    /**
     * 删除
     */
    @Override
    public Ro<?> del(final ID id) {
        return svc.del(id);
    }

    @Override
    public Ro<PojoRa<MO>> getById(final ID id) {
        return svc.getById(id);
    }

    @Override
    public Ro<BooleanRa> existById(final ID id) {
        return svc.existById(id);
    }

    @Override
    public Ro<PageRa<MO>> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy, final Integer limitPageSize) {
        return svc.list(qo, pageNum, pageSize, orderBy, limitPageSize);
    }

}
