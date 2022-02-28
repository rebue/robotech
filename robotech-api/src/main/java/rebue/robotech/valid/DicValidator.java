package rebue.robotech.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import rebue.robotech.dic.Dic;
import rebue.robotech.dic.DicUtils;

public class DicValidator implements ConstraintValidator<DicValid, Object> {
    // 枚举校验注解
    private DicValid annotation;

    @Override
    public void initialize(final DicValid constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        // 空值不判断返回真
        if (value == null) {
            return true;
        }

        final String valueStr = value.toString();
        // 空值不判断返回真
        if (StringUtils.isBlank(valueStr)) {
            return true;
        }

        // 获取目标枚举类型
        final Class<?> targetClass = annotation.target();

        if (!targetClass.isEnum()) {
            throw new IllegalArgumentException("@DicValid的target必须是枚举类型");
        }

        if (!Dic.class.isAssignableFrom(targetClass)) {
            throw new IllegalArgumentException("@DicValid的target必须实现Dic接口");
        }

        // 判断是否包含该值
        return DicUtils.isValid(targetClass, Integer.valueOf(valueStr));
    }

}
