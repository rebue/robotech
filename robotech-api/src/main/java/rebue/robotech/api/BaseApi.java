package rebue.robotech.api;

import rebue.robotech.to.PageTo;
import rebue.wheel.api.ra.BooleanRa;
import rebue.wheel.api.ra.IdRa;
import rebue.wheel.api.ra.PageRa;
import rebue.wheel.api.ro.Rt;

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
     * @param qc 要获取记录需要符合的条件，如果查找不到则返回null
     * @return 查询结果
     */
    Rt<MO> getOne(ONE_TO qc);

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     */
    Rt<MO> getById(ID id);

    /**
     * 判断指定ID的记录是否存在
     *
     * @param id 要查询对象的ID
     * @return 是否存在
     */
    Rt<BooleanRa> existById(ID id);

    /**
     * 分页查询列表
     *
     * @param qc 查询条件
     * @return 查询到的分页信息
     */
    Rt<PageRa<MO>> page(PAGE_TO qc);

}
