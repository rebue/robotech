package rebue.robotech.svc;

import rebue.robotech.ra.CountRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.ListRa;
import rebue.robotech.ra.BooleanRa;
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

    // TODO 1.4.0版本暂时不好实现
    Ro<PojoRa<MO>> getOne(MO mo);

    Ro<BooleanRa> existById(ID id);

    Ro<BooleanRa> existSelective(MO mo);

    Ro<CountRa> countSelective(final MO record);

    Ro<ListRa<MO>> listAll();

    Ro<ListRa<JO>> listJoAll();

    Ro<ListRa<MO>> list(final MO mo);

    default Ro<PageRa<MO>> list(final MO qo, final Integer pageNum, final Integer pageSize) {
        return list(qo, pageNum, pageSize, null, null);
    }

    default Ro<PageRa<MO>> list(final MO qo, final Integer pageNum, final Integer pageSize, final Integer limitPageSize) {
        return list(qo, pageNum, pageSize, null, limitPageSize);
    }

    default Ro<PageRa<MO>> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy) {
        return list(qo, pageNum, pageSize, null, null);
    }

    // TODO 1.4.0版本暂时不好实现
    Ro<PageRa<MO>> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}