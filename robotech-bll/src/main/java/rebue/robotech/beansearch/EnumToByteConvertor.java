package rebue.robotech.beansearch;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;
import rebue.wheel.api.dic.Dic;
import rebue.wheel.api.dic.DicUtils;

public class EnumToByteConvertor implements FieldConvertor.BFieldConvertor, FieldConvertor.MFieldConvertor {
    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return Dic.class.isAssignableFrom(meta.getType()) && meta.getDbType() == DbType.INT;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        return DicUtils.getItem(meta.getType(), (Integer) value);
    }
}
