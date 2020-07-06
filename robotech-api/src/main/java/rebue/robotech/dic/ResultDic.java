package rebue.robotech.dic;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回结果的字典类
 * 1: 成功
 * -1: 参数错误
 * -2: 失败 --》回滚事务
 * -3: 警告 --》不用回滚事务
 */
@AllArgsConstructor
@Getter
public enum ResultDic implements EnumBase {
    /**
     * 1: 成功
     */
    SUCCESS(1, "成功"),
    /**
     * -1: 参数错误
     */
    PARAM_ERROR(-1, "参数错误"),
    /**
     * -2: 失败 --》回滚事务
     */
    FAIL(-2, "失败 --》回滚事务"),
    /**
     * -3: 警告 --》不用回滚事务
     */
    WARN(-3, "警告 --》不用回滚事务");

    private final int    code;
    private final String desc;

    @Override
    public String getName() {
        return name();
    }

    /**
     * springdoc显示枚举说明将会调用此方法
     */
    @Override
    public String toString() {
        return getCode() + "(" + getDesc() + ")";
    }

    /**
     * 枚举的所有项，注意这个变量是静态单例的
     */
    private static final Map<Integer, EnumBase> valueMap = new HashMap<>();
    // 初始化map，保存枚举的所有项到map中以方便通过code查找
    static {
        for (final EnumBase item : values()) {
            valueMap.put(item.getCode(), item);
        }
    }

    /**
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口EnumBase
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    @JsonCreator // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    public static ResultDic getItem(final int code) {
        final ResultDic result = (ResultDic) valueMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("输入的code" + code + "不在枚举的取值范围内");
        }
        return result;
    }

}