package rebue.robotech.ra;

import java.io.Serializable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 带有ID的附加内容
 * 主要给添加方法返回生成后的ID
 *
 * @param <ID>
 *            POJO类唯一标识的属性值
 */
@Schema(description = "带有ID的附加内容")
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class IdRa<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回系统生成的ID
     */
    @Schema(description = "返回系统生成的ID")
    @NonNull
    private ID                id;

}