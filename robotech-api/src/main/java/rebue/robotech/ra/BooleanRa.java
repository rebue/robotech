package rebue.robotech.ra;

import java.io.Serializable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 带有Boolean结果的附加内容
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class BooleanRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回Boolean的值
     */
    @NonNull
    private Boolean value;

}