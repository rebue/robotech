package rebue.robotech.jackson.geometry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.Point;

import java.io.IOException;

public class GeometrySerializer extends JsonSerializer<Geometry> {
    @Override
    public void serialize(Geometry geometry, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartArray();
        for (int i = 0; i < geometry.numPoints(); i++) {
            generator.writeStartObject();
            Point point = geometry.getPoint(i);
            generator.writeFieldName("x");
            generator.writeNumber(point.x);
            generator.writeFieldName("y");
            generator.writeNumber(point.y);
            // 如果是3D的点，序列化z值
            if (point.dimension == 3) {
                generator.writeFieldName("z");
                generator.writeNumber(point.z);
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

}

