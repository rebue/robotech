package rebue.robotech.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.pagehelper.PageInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rebue.robotech.dic.ResultDic;

/**
 * 带有分页的基本的返回值的对象类
 * 主要给分页查询返回生成后的分页信息
 */
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PageRo<T> extends Ro {

    @Schema(description = "返回系统生成的ID")
    private PageInfo<T> page;

    public PageRo(final ResultDic result, final String msg, final PageInfo<T> page) {
        setResult(result);
        setMsg(msg);
        setPage(page);
    }

}