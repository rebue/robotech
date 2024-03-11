package rebue.robotech.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import rebue.robotech.dic.Dic;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumTypeHandler<E extends Enum<?> & Dic> extends BaseTypeHandler<Dic> {
    private final Class<E> type;

    public EnumTypeHandler(final Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    /**
     * 用于定义设置参数时，该如何把Java类型的参数转换为对应的数据库类型
     */
    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final Dic parameter,
                                    final JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    /**
     * 用于定义通过字段名称获取字段数据时，如何把数据库类型转换为对应的Java类型
     */
    @Override
    public Dic getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final int code = rs.getInt(columnName);
        return rs.wasNull() ? null : codeOf(code);
    }

    /**
     * 用于定义通过字段索引获取字段数据时，如何把数据库类型转换为对应的Java类型
     */
    @Override
    public Dic getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : codeOf(code);
    }

    /**
     * 用于定义调用存储过程后，如何把数据库类型转换为对应的Java类型
     */
    @Override
    public Dic getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : codeOf(code);
    }

    private E codeOf(final int code) {
        try {
            return EnumUtils.codeOf(type, code);
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", e);
        }
    }

}