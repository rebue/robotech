package rebue.robotech.clone;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import rebue.wheel.api.dic.Dic;
import rebue.wheel.api.dic.DicUtils;

/**
 * MapStruct的自定义枚举映射器
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class EnumConverter {
    /**
     * 字典项映射Short
     *
     * @param dicItem 字典项
     * @return Short
     */
    public <E extends Dic> Short dicItemMapShort(E dicItem) {
        if (dicItem == null)
            return null;
        return dicItem.getCode().shortValue();
    }

    /**
     * Short映射字典项
     *
     * @param code  字典项编码
     * @param clazz 字典类引用
     * @param <E>   字典类泛型
     * @return 字典项
     */
    @SuppressWarnings("unchecked")
    public <E extends Dic> E shortMapDicItem(Short code, @TargetType Class<E> clazz) {
        return (E) DicUtils.getItem(clazz, Integer.valueOf(code));
    }

}
