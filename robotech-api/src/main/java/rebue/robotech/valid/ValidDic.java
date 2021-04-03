package rebue.robotech.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 字典校验注解
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 限定本注解的处理类
@Constraint(validatedBy = { DicValidator.class
})
public @interface ValidDic {

    /**
     * 目标枚举类
     */
    Class<?> target();

    /**
     * 校验不正确提示的文本
     */
    String message();

    // 作用参考@Validated和@Valid的区别
    // 没有此行会报错: javax.validation.ConstraintDefinitionException: HV000074: rebue.robotech.valid.EnumValid contains Constraint annotation, but does not contain a groups parameter.
    Class<?>[] groups() default {};

    // 没有此行会报错: javax.validation.ConstraintDefinitionException: HV000074: rebue.robotech.valid.EnumValid contains Constraint annotation, but does not contain a payload parameter.
    Class<? extends Payload>[] payload() default {};
}
