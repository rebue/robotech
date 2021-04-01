package rebue.robotech.svc;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;

import rebue.robotech.mo.Mo;
import rebue.robotech.to.PageTo;

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
 * @author zbz
 *
 * @param <ID>        ID的类型
 * @param <ADD_TO>    添加参数的类型
 * @param <MODIFY_TO> 修改参数的类型
 * @param <DEL_TO>    删除参数的类型
 * @param <ONE_TO>    单个参数的类型
 * @param <LIST_TO>   列表参数的类型
 * @param <PAGE_TO>   分页参数的类型
 * @param <MO>        Mybatis模型对象的类型
 * @param <JO>        JPA实体对象的类型
 *
 */
@Validated
public interface CacheBaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO extends PageTo, MO extends Mo<ID>, JO>
        extends BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, JO> {

    /**
     * 添加记录
     *
     * @param mo 添加的参数
     *
     * @return 如果成功，且仅添加一条记录，返回添加时自动生成的ID，否则会抛出运行时异常
     */
    @Override
    @CachePut(key = "#mo.id")
    MO addMo(@Valid MO mo);

    /**
     * 通过ID修改记录内容
     *
     * @param mo 修改的参数，必须包含ID
     *
     * @return 如果成功，且仅修改一条记录，正常返回，否则会抛出运行时异常
     */
    @Override
    @CachePut(key = "#mo.id")
    MO modifyMoById(@Valid MO mo);

    /**
     * 通过ID删除记录
     *
     * @param id 要删除记录的ID
     *
     * @return 如果成功，且删除一条记录，正常返回，否则会抛出运行时异常
     */
    @Override
    @CacheEvict
    void delById(@NotNull ID id);

    /**
     * 根据ID获取一条MyBatis Model对象的记录
     *
     * @param id 要获取对象的ID
     *
     * @return MyBatis Model对象
     */
    @Override
    @Cacheable
    MO getById(@NotNull ID id);

}