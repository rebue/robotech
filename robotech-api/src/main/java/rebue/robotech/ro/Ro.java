package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rebue.robotech.dic.ResultDic;

/**
 * 基本的返回值的对象类
 * 
 * @author zbz
 */
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "返回结果")
public class Ro {

    /**
     * 返回结果的类型
     * 1: 成功
     * 0: 参数错误
     * -1: 失败-->回滚事务
     * -2: 警告-->不用回滚事务
     */
    @Schema(description = "返回结果的类型")
    private ResultDic result;

    /**
     * 返回结果的信息
     */
    @Schema(description = "返回结果的信息")
    private String    msg;

}