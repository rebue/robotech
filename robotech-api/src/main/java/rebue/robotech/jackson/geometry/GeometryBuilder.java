package rebue.robotech.jackson.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.Point;

public class GeometryBuilder {
    public static Geometry buildPoint(Map<String, Double> map) {
        if (map == null)
            return null;
        Point point = new Point();
        point.x = map.get("x");
        point.y = map.get("y");
        Double z = map.get("z");
        if (z != null) {
            point.z         = z;
            point.dimension = 3;
        } else {
            point.dimension = 2;
        }
        return point;
    }

    public static Point[] buildPoints(List<Map<String, Double>> pointMapList) {
        if (pointMapList == null || pointMapList.isEmpty())
            return null;
        List<Point> pointList = new ArrayList<>();
        for (Map<String, Double> map : pointMapList) {
            pointList.add((Point) GeometryBuilder.buildPoint(map));
        }
        Point[] pointArray = new Point[pointList.size()];
        for (int i = 0; i < pointList.size(); i++) {
            pointArray[i] = pointList.get(i);
        }
        return pointArray;
    }
}
