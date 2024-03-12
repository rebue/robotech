package rebue.robotech.svc;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import rebue.robotech.mo.Mo;
import rebue.robotech.to.PageTo;

import java.util.List;

/**
 * 基础服务层接口
 *
 * <pre>
 * 1. 在接口上方必须写上 @Validated 注解
 * 2. 参数是POJO类时用 @Valid 注解在参数类型的前面进行修饰
 *    参数是普通参数时，直接在参数类型的前面加上具体约束的注解
 * 3. (待验证)有分组时，在方法上方必须写上 @Validated 注解及分组
 * 4. 踩坑留痕：
 *    如果方法的返回值为void，在方法上方加上 @Valid 注解会出现异常，报HV000132错误
 * </pre>
 *
 * @param <ID>        ID的类型
 * @param <ADD_TO>    添加参数的类型
 * @param <MODIFY_TO> 修改参数的类型
 * @param <DEL_TO>    删除参数的类型
 * @param <ONE_TO>    单个参数的类型
 * @param <LIST_TO>   列表参数的类型
 * @param <PAGE_TO>   分页参数的类型
 * @param <MO>        Mybatis模型对象的类型
 * @author zbz
 */
@Validated
public interface BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO extends Mo<ID>> {
    /**
     * 添加记录
     *
     * @param to 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加后的实体，否则会抛出运行时异常
     */
    MO add(@Valid ADD_TO to);

    /**
     * 添加记录
     *
     * @param mo 添加的参数
     * @return 如果成功，且仅添加一条记录，返回添加后的实体，否则会抛出运行时异常
     */
    MO addMo(@Valid MO mo);

    /**
     * 通过ID修改记录内容
     *
     * @param to 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，正常返回修改后的实体，否则会抛出运行时异常
     */
    MO modifyById(@Valid MODIFY_TO to);

    /**
     * 通过ID修改记录内容
     *
     * @param mo 修改的参数，必须包含ID
     * @return 如果成功，且仅修改一条记录，正常返回修改后的实体，否则会抛出运行时异常
     */
    MO modifyMoById(@Valid MO mo);

    /**
     * 通过ID删除记录
     * 如果成功，且删除一条记录，正常返回，否则会抛出运行时异常
     *
     * @param id 要删除记录的ID
     */
    void delById(@NotNull ID id);

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
     * @param qo 要获取记录需要符合的条件，如果查找不到则返回null
     */
    MO getOne(@Valid ONE_TO qo);

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     * @return MyBatis Model对象，如果查找不到则返回null
     */
    MO getById(@NotNull ID id);

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
     * 条件查询
     *
     * @param qo 查询条件
     * @return 查询列表
     */
    List<MO> list(@Valid final LIST_TO qo);

    /**
     * 根据ID列表查询
     *
     * @param ids ID列表
     * @return 查询列表
     */
    List<MO> listIn(final List<ID> ids);

    /**
     * 查询所有
     *
     * @return 查询列表
     */
    List<MO> listAll();

    /**
     * 分页查询列表
     *
     * @param select   选择器
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param orderBy  排序字段
     * @return 查询到的分页信息
     */
    PageInfo<MO> page(@NotNull ISelect select, @NotNull Integer pageNum, @NotNull Integer pageSize, String orderBy);

    /**
     * 分页查询列表
     *
     * @param qo 查询条件
     * @return 查询到的分页信息
     */
    PageInfo<MO> page(@Valid PAGE_TO qo);

}