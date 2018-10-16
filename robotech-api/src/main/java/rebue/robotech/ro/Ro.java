package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rebue.robotech.dic.ResultDic;

/**
 * 基本的返回值的对象类
 * 
 * @author zbz
 *
 */
@JsonInclude(Include.NON_NULL)
@ToString
@Getter
@Setter
public class Ro {

    /**
     * 返回值 1:成功 -1:失败
     */
    private ResultDic result;

    /**
     * 返回的结果
     */
    private String    msg;

}