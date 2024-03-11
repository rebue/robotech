package rebue.robotech.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import rebue.robotech.api.BaseApi;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.mo.Mo;
import rebue.wheel.api.ra.BooleanRa;
import rebue.wheel.api.ra.IdRa;
import rebue.wheel.api.ra.PageRa;
import rebue.wheel.api.ra.PojoRa;
import rebue.wheel.api.ro.Rt;
import rebue.robotech.svc.BaseSvc;
import rebue.robotech.to.PageTo;

@Slf4j
public abstract class BaseApiImpl<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO extends Mo<ID>, JO, SVC extends BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, JO>>
        implements BaseApi<ID, ADD_TO, MODIFY_TO, ONE_TO, PAGE_TO, MO> {
    /**
     * 限制每页能查询的大小
     */
    protected int _limitPageSize = 100;

    @Autowired // 这里不能用@Resource，否则启动会报 `required a single bean, but xxx were found` 的错误
    protected SVC _svc;

    /**
     * 添加
     */
    @Override
    public Rt<IdRa<ID>> add(final ADD_TO to) {
        return Rt.success("添加成功", new IdRa<>(_svc.add(to).getId()));
    }

    /**
     * 修改
     */
    @Override
    public Rt<?> modify(final MODIFY_TO to) {
        _svc.modifyById(to);
        return Rt.success("修改成功");
    }

    /**
     * 删除
     */
    @Override
    public Rt<?> del(final ID id) {
        _svc.delById(id);
        return Rt.success("删除成功");

    }

    /**
     * 根据条件获取一条记录
     *
     * @param qo 要获取记录需要符合的条件，如果查找不到则返回null
     */
    @Override
    public Rt<PojoRa<MO>> getOne(final ONE_TO qo) {
        return Rt.success("查询成功", new PojoRa<>(_svc.getOne(qo)));
    }

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     */
    @Override
    public Rt<PojoRa<MO>> getById(final ID id) {
        return Rt.success("查询成功", new PojoRa<>(_svc.getById(id)));
    }

    @Override
    public Rt<BooleanRa> existById(final ID id) {
        return Rt.success("查询成功", new BooleanRa(_svc.existById(id)));
    }

    @Override
    public Rt<PageRa<MO>> page(final PAGE_TO qo) {
        if (qo.getPageSize() != null && qo.getPageSize() > _limitPageSize) {
            final String msg = "pageSize不能大于" + _limitPageSize;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return Rt.success("分页查询成功", new PageRa<>(_svc.page(qo)));
    }

}
