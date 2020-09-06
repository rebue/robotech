package rebue.robotech.mybatis;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;

public interface MapperRootInterface<MO, ID> {
    int insert(InsertStatementProvider<MO> insertStatement);

    int insertMultiple(MultiRowInsertStatementProvider<MO> multipleInsertStatement);

    int insert(MO record);

    int insertMultiple(Collection<MO> records);

    int insertSelective(MO record);

    int update(UpdateStatementProvider updateStatement);

    int update(UpdateDSLCompleter completer);

    // 下面两个update的方法是静态方法，不能不写方法体
//    static UpdateDSL<UpdateModel> updateAllColumns(MO record, UpdateDSL<UpdateModel> dsl);
//
//    static UpdateDSL<UpdateModel> updateSelectiveColumns(MO record, UpdateDSL<UpdateModel> dsl);

    int updateByPrimaryKey(MO record);

    int updateByPrimaryKeySelective(MO record);

    int delete(DeleteStatementProvider deleteStatement);

    int delete(DeleteDSLCompleter completer);

    int deleteByPrimaryKey(ID id);

    int deleteSelective(MO record);

    long count(SelectStatementProvider selectStatement);

    long count(CountDSLCompleter completer);

    Optional<MO> selectByPrimaryKey(ID id);

    Optional<MO> selectOne(SelectStatementProvider selectStatement);

    List<MO> selectMany(SelectStatementProvider selectStatement);

    Optional<MO> selectOne(SelectDSLCompleter completer);

    List<MO> select(SelectDSLCompleter completer);

    List<MO> selectDistinct(SelectDSLCompleter completer);

    List<MO> selectSelective(MO mo);

    Optional<MO> selectOne(MO mo);

    long countSelective(MO mo);

    boolean existByPrimaryKey(ID id);

    boolean existSelective(MO mo);

}