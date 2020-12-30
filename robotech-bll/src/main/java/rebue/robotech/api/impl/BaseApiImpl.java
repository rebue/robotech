package rebue.robotech.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.api.BaseApi;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;
import rebue.robotech.to.PageTo;

@Slf4j
public abstract class BaseApiImpl<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO, JO, SVC extends BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, JO>>
        implements BaseApi<ID, ADD_TO, MODIFY_TO, PAGE_TO, MO> {
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
    public Ro<IdRa<ID>> add(final ADD_TO to) {
        return new Ro<>(ResultDic.SUCCESS, "添加成功", new IdRa<>(_svc.add(to)));
    }

    /**
     * 修改
     */
    @Override
    public Ro<?> modify(final MODIFY_TO to) {
        _svc.modifyById(to);
        return new Ro<>(ResultDic.SUCCESS, "修改成功");
    }

    /**
     * 删除
     */
    @Override
    public Ro<?> del(final ID id) {
        _svc.delById(id);
        return new Ro<>(ResultDic.SUCCESS, "删除成功");

    }

    @Override
    public Ro<PojoRa<MO>> getById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", new PojoRa<>(_svc.getById(id)));
    }

    @Override
    public Ro<BooleanRa> existById(final ID id) {
        return new Ro<>(ResultDic.SUCCESS, "查询成功", new BooleanRa(_svc.existById(id)));
    }

    @Override
    public Ro<PageRa<MO>> page(final PAGE_TO qo) {
        if (qo.getPageSize() != null && qo.getPageSize() > _limitPageSize) {
            final String msg = "pageSize不能大于" + _limitPageSize;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return new Ro<>(ResultDic.SUCCESS, "分页查询成功", new PageRa<>(_svc.page(qo)));
    }

}
