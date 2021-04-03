package rebue.robotech.mybatis;

import rebue.robotech.dic.Dic;

public class EnumUtils {
    public static <E extends Enum<?> & Dic> E codeOf(final Class<E> enumClass, final int code) {
        final E[] enumConstants = enumClass.getEnumConstants();
        for (final E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
