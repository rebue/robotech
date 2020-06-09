package rebue.robotech.svc;

import java.util.List;

import rebue.robotech.ro.PageRo;
import rebue.robotech.ro.Ro;

public interface BaseSvc<ID, MO, JO> {
    /**
     * 添加(不会自动生成ID，所以ID不能为空)
     */
    Ro add0(MO mo);

    /**
     * 添加(如果ID为空，自动生成ID)
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

    // TODO : Svc : 在Spring的Bean加载完以后要调用此方法，否则在RabbitMQ里的回调线程调实现类的方法会没有任何反应
//    void test();

    int insertSelective(MO mo);

    int updateByPrimaryKeySelective(MO mo);

    int deleteByPrimaryKey(ID id);

    List<MO> listAll();

    List<JO> listJoAll();

    List<MO> list(MO mo);

    MO getById(ID id);

    JO getJoById(ID id);

    boolean existByPrimaryKey(ID id);

    boolean existSelective(MO mo);

    MO getOne(MO mo);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, Integer limitPageSize);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, String orderBy);

    PageRo<MO> list(MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}