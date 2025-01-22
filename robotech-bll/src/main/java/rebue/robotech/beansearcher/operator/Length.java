package rebue.robotech.beansearcher.operator;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

import java.util.ArrayList;
import java.util.List;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.ObjectUtils;

public class Length extends DialectWrapper implements FieldOp {
    public Length(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String name() {
        return "Length";
    }

    @Override
    public boolean isNamed(String name) {
        return "len".equals(name) || "Length".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        String dialectName = getDialect().getClass().getSimpleName();
        if (dialectName.equals("SqlServerDialect")) {
            sqlBuilder.append("LEN(");
        } else {
            sqlBuilder.append("LENGTH(");
        }
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[]           values   = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
        }
        sqlBuilder.append(") = ?");
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values));
        return params;
    }
}
