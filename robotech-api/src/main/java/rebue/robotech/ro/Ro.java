package rebue.robotech.ro;

import java.io.Serializable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rebue.robotech.dic.ResultDic;

/**
 * 基本的返回值的对象类
 * 
 * @param <RA>
 *            返回附加内容的类型
 */
@Schema(description = "返回结果")
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Ro<RA> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回结果的类型
     * 1: 成功
     * 0: 参数错误
     * -1: 失败-->回滚事务
     * -2: 警告-->不用回滚事务
     */
    @Schema(description = "返回结果的类型")
    @NonNull
    private ResultDic         result;

    /**
     * 返回结果的信息
     */
    @Schema(description = "返回结果的信息")
    @NonNull
    private String            msg;

    /**
     * 返回结果的自定义编码(如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    @Schema(description = "返回结果的自定义编码(如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)")
    private String            code;

    /**
     * 附加的内容(如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    @Schema(description = "附加的内容(如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)")
    private RA                addition;
}