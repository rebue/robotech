package rebue.robotech.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.valid.AddGroup;
import rebue.robotech.valid.ModifyGroup;

// 在接口上方必须写上 @Validated 注解；
// 有分组时，在方法上方必须写上 @Validated 注解及分组；
// 参数是POJO类时用 @Valid 注解在参数类型的前面进行修饰；
// 而如果是普通参数，则在方法的上方写上 @Validated 注解，具体约束的注解直接写在参数类型的前面
@Validated
public interface BaseApi<ID, MO> {
    /**
     * 添加
     */
    @Validated(AddGroup.class)
    Ro<IdRa<ID>> add(@Valid MO mo);

    /**
     * 修改
     */
    @Validated(ModifyGroup.class)
    Ro<?> modify(@Valid MO mo);

    /**
     * 删除
     */
    Ro<?> del(@NotNull ID id);

    Ro<PojoRa<MO>> getById(@NotNull ID id);

    Ro<PageRa<MO>> list(@Valid MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}
