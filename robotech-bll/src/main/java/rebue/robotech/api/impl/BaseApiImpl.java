package rebue.robotech.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import rebue.robotech.api.BaseApi;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.mo.Mo;
import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;

public abstract class BaseApiImpl<ID, MO extends Mo<ID>, JO, SVC extends BaseSvc<ID, MO, JO>> implements BaseApi<ID, MO> {
    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected SVC svc;

    /**
     * 添加
     */
    @Override
    public Ro<IdRa<ID>> add(final MO mo) {
        if (svc.add(mo) != null) {
            return new Ro<>(ResultDic.SUCCESS, "添加成功", null, new IdRa<>(mo.getId()));
        } else {
            return new Ro<>(ResultDic.FAIL, "添加失败");
        }
    }

    /**
     * 修改
     */
    @Override
    public Ro<?> modify(final MO mo) {
        if (svc.modify(mo)) {
            return new Ro<>(ResultDic.SUCCESS, "修改成功");
        } else {
            return new Ro<>(ResultDic.FAIL, "修改失败");
        }
    }

    /**
     * 删除
     */
    @Override
    public Ro<?> del(final ID id) {
        if (svc.del(id)) {
            return new Ro<>(ResultDic.SUCCESS, "删除成功");
        } else {
            return new Ro<>(ResultDic.FAIL, "删除失败，找不到该记录");
        }

    }

    @Override
    public Ro<PojoRa<MO>> getById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new PojoRa<>(svc.getById(id)));
    }

    @Override
    public Ro<BooleanRa> existById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", null, new BooleanRa(svc.existById(id)));
    }

    @Override
    public Ro<PageRa<MO>> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy, final Integer limitPageSize) {
        return new Ro<>(ResultDic.SUCCESS, "分页查询成功", null, new PageRa<>(svc.list(qo, pageNum, pageSize, orderBy, limitPageSize)));
    }

}
