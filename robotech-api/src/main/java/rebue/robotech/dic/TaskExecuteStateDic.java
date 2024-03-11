package rebue.robotech.dic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务执行状态的字典
 * <p>
 * -1: 取消
 * 0: 未执行
 * 1: 已执行
 * 2: 暂停
 *
 * @Deprecated
 */
@AllArgsConstructor
@Getter
@Deprecated
public enum TaskExecuteStateDic implements Dic {
    /**
     * -1: 取消
     */
    CANCEL(-1, "取消"),
    /**
     * 0: 未执行
     */
    NONE(0, "未执行"),
    /**
     * 1: 已执行
     */
    DONE(1, "已执行"),
    /**
     * 2: 暂停
     */
    SUSPEND(2, "暂停");

    private final Integer code;
    private final String  desc;

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
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口Dic
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    @JsonCreator // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    public static TaskExecuteStateDic getItem(final Integer pcode) {
        final TaskExecuteStateDic result = (TaskExecuteStateDic) DicUtils.getItem(TaskExecuteStateDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

}
