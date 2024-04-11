package rebue.robotech.jackson.geometry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.Point;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LineGeometryDeserializer extends JsonDeserializer<LineString> {


    @Override
    public LineString deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        // TODO parser.isExpectedStartArrayToken() 判断是否是数组
        List<Map<String, Double>> pointMapList = parser.readValueAs(new TypeReference<>() {
        });
        if (pointMapList == null || pointMapList.isEmpty()) return null;
        Point[] points = GeometryBuilder.buildPoints(pointMapList);
        if (points == null || points.length == 0) return null;
        return new LineString(points);
    }
}

