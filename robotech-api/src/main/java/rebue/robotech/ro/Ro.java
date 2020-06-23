package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import rebue.robotech.dic.ResultDic;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @param <T> 返回附加内容的类型
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Ro<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回结果的类型
     * 1: 成功
     * 0: 参数错误
     * -1: 失败 --》回滚事务
     * -2: 警告 --》不用回滚事务
     */
    @NonNull
    private ResultDic result;

    /**
     * 返回结果的信息
     */
    @NonNull
    private String msg;

    /**
     * 返回结果的自定义编码(如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    private String code;

    /**
     * 附加的内容(如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    private T extra;
}