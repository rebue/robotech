package rebue.robotech.dozer;

import com.github.dozermapper.core.CustomConverter;

import rebue.wheel.core.idworker.IdWorker3Helper;

/**
 * ID字段自动生成唯一long序列号
 *
 * @author 张柏子
 *
 * @deprecated 不再需要自动生成
 */
@Deprecated
public class IdGenConverter implements CustomConverter {

    @Override
    public Object convert(final Object existingDestinationFieldValue, final Object sourceFieldValue, final Class<?> destinationClass, final Class<?> sourceClass) {
        return IdWorker3Helper.getId();
    }

}
