package rebue.robotech.mapper;

import java.util.List;

public interface MybatisBaseMapper<MO, ID> {
    int deleteByPrimaryKey(ID id);

    int insert(MO mo);

    int insertSelective(MO mo);

    MO selectByPrimaryKey(ID id);

    int updateByPrimaryKeySelective(MO mo);

    int updateByPrimaryKey(MO mo);

    List<MO> selectAll();

    List<MO> selectSelective(MO mo);

    boolean existByPrimaryKey(ID id);

    boolean existSelective(MO mo);

}