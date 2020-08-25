package rebue.robotech.svc;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.github.pagehelper.PageInfo;

//在接口上方必须写上 @Validated 注解；
//有分组时，在方法上方必须写上 @Validated 注解及分组；
//参数是POJO类时用 @Valid 注解在参数类型的前面进行修饰；
//而如果是普通参数，则在方法的上方写上 @Validated 注解，具体约束的注解直接写在参数类型的前面
@Validated
public interface BaseSvc<ID, ADD_TO, MODIFY_TO, QUERY_TO, MO, JO> {
    /**
     * 添加
     */
    @Validated
    ID add(@Valid ADD_TO to);

    /**
     * 修改
     */
    @Validated
    Boolean modify(@Valid MODIFY_TO to);

    /**
     * 删除
     */
    Boolean del(@NotNull ID id);

    MO getOne(@Valid QUERY_TO qo);

    MO getById(@NotNull ID id);

    JO getJoById(@NotNull ID id);

    Boolean existById(@NotNull ID id);

    Boolean existSelective(@Valid QUERY_TO qo);

    Long countSelective(@Valid final QUERY_TO qo);

    List<MO> listAll();

    List<JO> listJoAll();

    List<MO> list(@Valid final QUERY_TO qo);

    default PageInfo<MO> list(final QUERY_TO qo, final Integer pageNum, final Integer pageSize) {
        return list(qo, pageNum, pageSize, null, null);
    }

    default PageInfo<MO> list(final QUERY_TO qo, final Integer pageNum, final Integer pageSize, final Integer limitPageSize) {
        return list(qo, pageNum, pageSize, null, limitPageSize);
    }

    default PageInfo<MO> list(final QUERY_TO qo, final Integer pageNum, final Integer pageSize, final String orderBy) {
        return list(qo, pageNum, pageSize, null, null);
    }

    PageInfo<MO> list(@Valid QUERY_TO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}