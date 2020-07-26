package rebue.robotech.api;

import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;

public interface BaseApi<ID, MO> {
    /**
     * 添加
     */
    Ro<IdRa<ID>> add(MO mo);

    /**
     * 修改
     */
    Ro<?> modify(MO mo);

    /**
     * 删除
     */
    Ro<?> del(ID id);

    Ro<PojoRa<MO>> getById(ID id);

    Ro<BooleanRa> existById(ID id);

    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}
