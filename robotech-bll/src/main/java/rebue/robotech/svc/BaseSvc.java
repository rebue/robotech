package rebue.robotech.svc;

import java.util.List;

import rebue.robotech.ro.PageRo;
import rebue.robotech.ro.Ro;

public interface BaseSvc<ID, MO, JO> {
    // TODO : Svc : 在Spring的Bean加载完以后要调用此方法，否则在RabbitMQ里的回调线程调实现类的方法会没有任何反应
//    void test();

    Ro add(MO mo);

    Ro modify(MO mo);

    Ro del(ID id);

    List<MO> listAll();

    List<JO> listJoAll();

    List<MO> list(MO mo);

    MO getById(ID id);

    JO getJoById(ID id);

    boolean existById(ID id);

    boolean existSelective(MO mo);

    MO getOne(MO mo);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, Integer limitPageSize);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, String orderBy);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}