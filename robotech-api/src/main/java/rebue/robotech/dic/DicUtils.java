package rebue.robotech.dic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DicUtils {

    /**
     * 枚举缓存
     */
    private static Map<String, List<Dic>> caches = new ConcurrentHashMap<>();

    /**
     * 获取字典类的所有name
     */
    public static List<Dic> getItems(final Class<?> dicClass) {
        if (!dicClass.isEnum()) {
            throw new IllegalArgumentException("参数必须是枚举类型");
        }

        if (!Dic.class.isAssignableFrom(dicClass)) {
            throw new IllegalArgumentException("参数必须实现Dic接口");
        }

        // 先从缓存中查找
        List<Dic> items = caches.get(dicClass.getName());
        if (items != null) {
            return items;
        }

        // 如果缓存中没有，则利用反射获取
        items = Stream.of(dicClass.getEnumConstants()).map(item -> (Dic) item).collect(Collectors.toList());

        // 放入缓存
        caches.put(dicClass.getName(), items);

        return items;
    }

    /**
     * 判断code是否在字典中是有效的值
     * 
     * @param dicClass 字典类型
     * @param code     字典的编码值
     */
    public static boolean isValid(final Class<?> dicClass, final Integer code) {
        final List<Dic> items = getItems(dicClass);
        for (final Dic dic : items) {
            if (dic.getCode() == code) {
                return true;
            }
        }
        return false;
    }
}
