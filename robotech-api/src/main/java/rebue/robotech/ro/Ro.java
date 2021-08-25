package rebue.robotech.ro;

import java.io.Serializable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rebue.robotech.dic.ResultDic;

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
     */
    @NonNull
    private ResultDic         result;

    /**
     * 返回结果的信息
     */
    @NonNull
    private String            msg;

    /**
     * 详情
     */
    private String            detail;

    /**
     * 返回结果的自定义编码
     * (如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    private String            code;

    /**
     * 附加的内容
     * (如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    private T                 extra;

    public Ro(final ResultDic result, final String msg, final String detail) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
    }

    public Ro(final ResultDic result, final String msg, final T extra) {
        this.result = result;
        this.msg    = msg;
        this.extra  = extra;
    }

    public boolean isSuccess()
    {
        return ResultDic.SUCCESS.equals(result);
    }

    public static <T> Ro<T> success(T extra)
    {
        return new Ro<>(ResultDic.SUCCESS, "", extra);
    }

    public static <T> Ro<T> fail(String msg)
    {
        return new Ro<>(ResultDic.FAIL, msg, null);
    }

}