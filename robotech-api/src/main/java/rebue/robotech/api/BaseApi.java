package rebue.robotech.api;

import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.to.PageTo;

public interface BaseApi<ID, ADD_TO, MODIFY_TO, PAGE_TO extends PageTo, MO> {
    /**
     * 添加
     */
    Ro<IdRa<ID>> add(ADD_TO to);

    /**
     * 修改
     */
    Ro<?> modify(MODIFY_TO to);

    /**
     * 删除
     */
    Ro<?> del(ID id);

    Ro<PojoRa<MO>> getById(ID id);

    Ro<BooleanRa> existById(ID id);

    Ro<PageRa<MO>> page(PAGE_TO qo);

}
