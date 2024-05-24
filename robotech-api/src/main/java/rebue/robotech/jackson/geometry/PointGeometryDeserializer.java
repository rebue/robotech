package rebue.robotech.jackson.geometry;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import net.postgis.jdbc.geometry.Point;

public class PointGeometryDeserializer extends JsonDeserializer<Point> {

    @Override
    public Point deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
        // TODO parser.isExpectedStartArrayToken() 判断是否是数组
        List<Map<String, Double>> pointMapList = parser.readValueAs(new TypeReference<>() {
        });
        if (pointMapList == null || pointMapList.isEmpty())
            return null;
        return (Point) GeometryBuilder.buildPoint(pointMapList.get(0));
    }
}
