package rebue.robotech.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * 带有POJO的附加内容
 * 主要给获取单个POJO的系列查询方法使用
 *
 * @param <POJO> POJO类
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class PojoRa<POJO> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回单个POJO对象
     */
    @NonNull
    private POJO one;

}