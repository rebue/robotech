package rebue.robotech.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import rebue.wheel.api.dic.Dic;
import rebue.wheel.api.dic.DicUtils;

/**
 * MapStruct的自定义映射器
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class RebueMapper {
    /**
     * 字典项映射Byte
     *
     * @param dicItem 字典项
     * @return Byte
     */
    public <E extends Dic> Byte dicItemMapByte(E dicItem) {
        return dicItem.getCode().byteValue();
    }

    /**
     * Byte映射字典项
     *
     * @param code  字典项编码
     * @param clazz 字典类引用
     * @param <E>   字典类泛型
     * @return 字典项
     */
    public <E extends Dic> E byteMapDicItem(Byte code, @TargetType Class<E> clazz) {
        return (E) DicUtils.getItem(clazz, Integer.valueOf(code));
    }
}
