package rebue.robotech.svc;

import java.util.List;

import com.github.pagehelper.PageInfo;

public interface BaseSvc<ID, MO, JO> {
    // TODO : Svc : 在Spring的Bean加载完以后要调用此方法，否则在RabbitMQ里的回调线程调实现类的方法会没有任何反应
    void test();

    int add(MO mo);

    int modify(MO mo);

    int del(ID id);

    List<MO> listAll();

    List<MO> list(MO mo);

    PageInfo<MO> list(MO qo, int pageNum, int pageSize);

    PageInfo<MO> list(MO qo, int pageNum, int pageSize, String orderBy);

    MO getById(ID id);

    boolean existByPrimaryKey(ID id);

    boolean existSelective(MO mo);

    MO getOne(MO mo);

}