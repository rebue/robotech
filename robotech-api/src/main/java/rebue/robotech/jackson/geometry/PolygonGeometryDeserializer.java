package rebue.robotech.jackson.geometry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.postgis.jdbc.geometry.LinearRing;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PolygonGeometryDeserializer extends JsonDeserializer<Polygon> {


    @Override
    public Polygon deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        // TODO parser.isExpectedStartArrayToken() 判断是否是数组
        List<Map<String, Double>> pointMapList = parser.readValueAs(new TypeReference<>() {
        });
        if (pointMapList == null || pointMapList.isEmpty()) return null;
        Point[] points = GeometryBuilder.buildPoints(pointMapList);
        if (points == null || points.length == 0) return null;
        LinearRing[] linearRings = new LinearRing[]{
                new LinearRing(points)
        };
        return new Polygon(linearRings);
    }
}

