package rebue.robotech.to;

import java.io.Serializable;

import javax.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class PageTo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 页码(默认为1)
     */
    @Positive(message = "页码必须大于0")
    private Integer           pageNum          = 1;
    /**
     * 每页大小(默认为10)
     */
    @Positive(message = "每页大小必须大于0")
    private Integer           pageSize         = 10;
    /**
     * 排序字段
     */
    private String            orderBy;
}
