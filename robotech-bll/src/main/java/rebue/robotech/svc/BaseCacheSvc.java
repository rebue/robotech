package rebue.robotech.svc;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface BaseCacheSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, MO, JO> extends BaseSvc<ID, ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, MO, JO> {

    @Override
    @CachePut(key = "#to.id")
    ID add(@Valid final ADD_TO to);

    @Override
    @CachePut(key = "#to.id")
    Boolean modifyById(@Valid final MODIFY_TO to);

    @Override
    @CacheEvict
    Boolean delById(@NotNull final ID id);

    @Override
    @CacheEvict(allEntries = true)
    Integer delSelective(@Valid final DEL_TO to);

    @Override
    @Cacheable
    MO getById(@NotNull final ID id);

    @Override
    @Cacheable
    JO getJoById(@NotNull final ID id);

    @Override
    @Cacheable
    Boolean existById(@NotNull final ID id);

    @Override
    @Cacheable
    List<JO> listJoAll();

}
