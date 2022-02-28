package rebue.robotech.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rebue.robotech.dic.Dic;

/**
 * mybatis自动处理枚举类型的转换
 * 启用方法: 在spring boot使用
 * 1. 依赖mybatis-spring-boot-starter
 * 2. 配置文件中配置
 * mybatis.configuration.default-enum-type-handler=rebue.robotech.mybatis.AutoEnumTypeHandler
 */
public class AutoEnumTypeHandler<E extends Enum<E> & Dic> extends BaseTypeHandler<E> {
    private final static Logger _log        = LoggerFactory.getLogger(AutoEnumTypeHandler.class);

    private BaseTypeHandler<E>  typeHandler = null;

    @SuppressWarnings({ "unchecked", "rawtypes"
    })
    public AutoEnumTypeHandler(final Class<E> type) {
        _log.info("构造mybatis自动处理枚举类型的转换类");
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        // 如果实现了Dic则使用我们自定义的转换器
        if (Dic.class.isAssignableFrom(type)) {
            // TODO 去除泛型警告及优化基于Dic查找的代码
            typeHandler = new rebue.robotech.mybatis.EnumTypeHandler(type);
        }
        // 默认转换器也可换成EnumOrdinalTypeHandler
        else {
            typeHandler = new org.apache.ibatis.type.EnumTypeHandler<>(type);
        }
    }

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final E parameter, final JdbcType jdbcType)
            throws SQLException {
        typeHandler.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        return typeHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(cs, columnIndex);
    }
}