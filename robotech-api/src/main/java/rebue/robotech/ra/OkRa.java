package rebue.robotech.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * 带有是否OK结果的附加内容
 * 主要给需要判断是否的系列查询方法使用
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class OkRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回是否OK
     */
    @NonNull
    private Boolean ok;

}