package rebue.robotech.dic;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 枚举的基础接口<br>
 */
public interface Dic {

    /**
     * Jackson在序列化时，只序列化 @JsonValue 标注的值
     *
     * @return jackson序列化的值
     */
    @JsonValue
    Integer getCode();

    String getName();

    String getDesc();

}
