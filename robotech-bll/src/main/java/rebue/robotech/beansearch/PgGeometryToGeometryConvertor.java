package rebue.robotech.beansearch;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import net.postgis.jdbc.PGgeometry;

public class PgGeometryToGeometryConvertor implements FieldConvertor.BFieldConvertor, FieldConvertor.MFieldConvertor {
    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return PGgeometry.class == valueType;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        PGgeometry pgGeometry = (PGgeometry) value;
        return pgGeometry.getGeometry();
    }
}
