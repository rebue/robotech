package rebue.robotech.svc;

public interface EsBaseSvc<SO> {
    String getIndexName();

    /**
     * 添加
     * 
     * @return document的id
     */
    SO add(SO so);

    /**
     * 修改
     */
    void modify(SO so);

    /**
     * 删除
     */
    void del(String id);

    SO getById(String id);

//    List<SO> listAll();
//
//    Page<SO> list(Pageable pageable);
//
//    Page<SO> list(Integer pageNum, Integer pageSize);
//
//    boolean existById(String id);
}