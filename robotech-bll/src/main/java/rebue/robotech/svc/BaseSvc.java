package rebue.robotech.svc;

import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

//在接口上方必须写上 @Validated 注解；
//有分组时，在方法上方必须写上 @Validated 注解及分组；
//参数是POJO类时用 @Valid 注解在参数类型的前面进行修饰；
//而如果是普通参数，则在方法的上方写上 @Validated 注解，具体约束的注解直接写在参数类型的前面
@Validated
public interface BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, MO, JO> {
    /**
     * 添加记录
     *
     * @param to 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加时自动生成的ID，否则返回null
     */
    @Validated
    ID add(@Valid ADD_TO to);

    /**
     * 通过ID修改记录内容
     *
     * @param to 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，返回true，否则返回false
     */
    @Validated
    Boolean modifyById(@Valid MODIFY_TO to);

    /**
     * 通过ID删除记录
     *
     * @param id 要删除记录的ID
     * @return 如果成功，且删除一条记录，返回true，否则返回false
     */
    Boolean delById(@NotNull ID id);

    /**
     * 通过条件删除记录
     *
     * @param to 要删除记录需要符合的条件
     * @return 返回删除的记录数
     */
    Integer delSelective(@Valid DEL_TO to);

    /**
     * 根据条件获取一条记录
     *
     * @param qo 要获取记录需要符合的条件
     */
    MO getOne(@Valid ONE_TO qo);

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     * @return MyBatis Model对象
     */
    MO getById(@NotNull ID id);

    /**
     * 根据ID获取一条JPA对象的记录
     *
     * @param id 要获取对象的ID
     * @return JPA对象
     */
    JO getJoById(@NotNull ID id);

    /**
     * 判断指定ID的记录是否存在
     */
    Boolean existById(@NotNull ID id);

    /**
     * 判断符合条件的记录是否存在
     */
    Boolean existSelective(@Valid ONE_TO qo);

    /**
     * 统计符合条件的记录数
     */
    Long countSelective(@Valid final ONE_TO qo);

    /**
     * 查询列表
     *
     * @param qo 查询条件
     * @return 查询列表
     */
    List<MO> listAll(@Valid LIST_TO qo);

    /**
     * 查询JPA对象列表
     *
     * @return 查询JPA对象列表
     */
    List<JO> listJoAll();

    /**
     * 分页查询列表
     *
     * @param qo 查询条件
     * @return 查询到的分页信息
     */
    PageInfo<MO> list(@Valid LIST_TO qo);

}