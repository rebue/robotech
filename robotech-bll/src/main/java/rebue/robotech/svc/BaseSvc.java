package rebue.robotech.svc;

import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.ListRa;
import rebue.robotech.ra.OkRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;

public interface BaseSvc<ID, MO, JO> {
    // TODO : Svc : 在Spring的Bean加载完以后要调用此方法，否则在RabbitMQ里的回调线程调实现类的方法会没有任何反应
//    void test();

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

    Ro<PojoRa<JO>> getJoById(ID id);

    Ro<PojoRa<MO>> getOne(MO mo);

    Ro<ListRa<MO>> listAll();

    Ro<ListRa<JO>> listJoAll();

    Ro<ListRa<MO>> list(MO mo);

    Ro<OkRa> existById(ID id);

    Ro<OkRa> existSelective(MO mo);

    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize);

    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize, Integer limitPageSize);

    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize, String orderBy);

    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}