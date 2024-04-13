package rebue.robotech.clone;

import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct的自定义PostGis映射器
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class PostGisConverter {
    public Point pgGeometryMapPoint(PGgeometry pgGeometry) {
        if (pgGeometry == null) return null;
        return (Point) pgGeometry.getGeometry();
    }

    public PGgeometry pointMapPgGeometry(Point point) {
        if (point == null) return null;
        return new PGgeometry(point);
    }

    public LineString pgGeometryMapLine(PGgeometry pgGeometry) {
        if (pgGeometry == null) return null;
        return (LineString) pgGeometry.getGeometry();
    }

    public PGgeometry lineMapPgGeometry(LineString lineString) {
        if (lineString == null) return null;
        return new PGgeometry(lineString);
    }

    public Polygon pgGeometryMapPolygon(PGgeometry pgGeometry) {
        if (pgGeometry == null) return null;
        return (Polygon) pgGeometry.getGeometry();
    }

    public PGgeometry polygonMapPgGeometry(Polygon polygon) {
        if (polygon == null) return null;
        return new PGgeometry(polygon);
    }
}
