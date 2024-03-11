package rebue.robotech.api;

import rebue.wheel.api.ra.BooleanRa;
import rebue.wheel.api.ra.IdRa;
import rebue.wheel.api.ra.PageRa;
import rebue.wheel.api.ra.PojoRa;
import rebue.wheel.api.ro.Rt;
import rebue.robotech.to.PageTo;

public interface BaseApi<ID, ADD_TO, MODIFY_TO, ONE_TO, PAGE_TO extends PageTo, MO> {
    /**
     * 添加
     */
    Rt<IdRa<ID>> add(ADD_TO to);

    /**
     * 修改
     */
    Rt<?> modify(MODIFY_TO to);

    /**
     * 删除
     */
    Rt<?> del(ID id);

    /**
     * 根据条件获取一条记录
     *
     * @param qo 要获取记录需要符合的条件，如果查找不到则返回null
     */
    Rt<PojoRa<MO>> getOne(ONE_TO qo);

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     */
    Rt<PojoRa<MO>> getById(ID id);

    Rt<BooleanRa> existById(ID id);

    Rt<PageRa<MO>> page(PAGE_TO qo);

}
