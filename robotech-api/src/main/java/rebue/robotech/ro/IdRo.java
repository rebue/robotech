package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 带有ID的基本的返回值的对象类
 * 主要给添加方法返回生成后的ID
 */
@JsonInclude(Include.NON_NULL)
@ToString
@Getter
@Setter
public class IdRo extends Ro {

    private Long Id;

}