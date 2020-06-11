package rebue.robotech.api;

import rebue.robotech.ro.PageRo;
import rebue.robotech.ro.Ro;

public interface BaseApi<ID, MO> {
    /**
     * 添加
     */
    Ro add(MO mo);

    /**
     * 修改
     */
    Ro modify(MO mo);

    /**
     * 删除
     */
    Ro del(ID id);

    MO getById(ID id);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}
