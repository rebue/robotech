package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class Ro {

    /**
     * 返回值类型
     * 1: 成功
     * 0: 参数错误
     * -1: 失败(回滚事务)
     * -2: 警告(不用回滚事务)
     */
    private ResultDic result;

    /**
     * 返回的结果
     */
    private String    msg;

}