package rebue.robotech.mybatis.handler.geometry;

import net.postgis.jdbc.PGgeometry;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({PGgeometry.class})
public class PgGeometryTypeHandler extends BaseTypeHandler<PGgeometry> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PGgeometry parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public PGgeometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return (PGgeometry) rs.getObject(columnName);
    }

    @Override
    public PGgeometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return (PGgeometry) rs.getObject(columnIndex);
    }

    @Override
    public PGgeometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return (PGgeometry) cs.getObject(columnIndex);
    }
}
