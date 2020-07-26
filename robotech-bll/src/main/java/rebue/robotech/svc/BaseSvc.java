package rebue.robotech.svc;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.github.pagehelper.PageInfo;

import rebue.robotech.valid.AddGroup;
import rebue.robotech.valid.ModifyGroup;

//在接口上方必须写上 @Validated 注解；
//有分组时，在方法上方必须写上 @Validated 注解及分组；
//参数是POJO类时用 @Valid 注解在参数类型的前面进行修饰；
//而如果是普通参数，则在方法的上方写上 @Validated 注解，具体约束的注解直接写在参数类型的前面
@Validated
public interface BaseSvc<ID, MO, JO> {
    // TODO : Svc : 在Spring的Bean加载完以后要调用此方法，否则在RabbitMQ里的回调线程调实现类的方法会没有任何反应
//    void test();

    /**
     * 添加
     */
    @Validated(AddGroup.class)
    Boolean add(@Valid MO mo);

    /**
     * 修改
     */
    @Validated(ModifyGroup.class)
    Boolean modify(@Valid MO mo);

    /**
     * 删除
     */
    Boolean del(@NotNull ID id);

    MO getOne(@Valid MO mo);

    MO getById(@NotNull ID id);

    JO getJoById(@NotNull ID id);

    Boolean existById(@NotNull ID id);

    Boolean existSelective(@Valid MO mo);

    Long countSelective(@Valid final MO record);

    List<MO> listAll();

    List<JO> listJoAll();

    List<MO> list(@Valid final MO mo);

    default PageInfo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize) {
        return list(qo, pageNum, pageSize, null, null);
    }

    default PageInfo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize, final Integer limitPageSize) {
        return list(qo, pageNum, pageSize, null, limitPageSize);
    }

    default PageInfo<MO> list(final MO qo, final Integer pageNum, final Integer pageSize, final String orderBy) {
        return list(qo, pageNum, pageSize, null, null);
    }

    PageInfo<MO> list(@Valid MO qo, Integer pageNum, Integer pageSize, String orderBy, Integer limitPageSize);

}