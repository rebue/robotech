package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rebue.robotech.dic.ResultDic;

/**
 * 带有ID的基本的返回值的对象类
 * 主要给添加方法返回生成后的ID
 */
@JsonInclude(Include.NON_NULL)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class IdRo<ID> extends Ro {

    private ID id;

    public IdRo(final ResultDic result, final String msg, final ID id) {
        setResult(result);
        setMsg(msg);
        setId(id);
    }

}