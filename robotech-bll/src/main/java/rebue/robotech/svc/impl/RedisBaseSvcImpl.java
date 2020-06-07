package rebue.robotech.svc.impl;

import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.RedisBaseSvc;

@Slf4j
public abstract class RedisBaseSvcImpl<T> implements RedisBaseSvc<T> {

    /**
     * Key的前缀
     */
    protected String                 REDIS_KEY_PREFIX;

    @Reference
    private RedisTemplate<String, T> _redisTemplate;

    @Override
    public Ro add(String key, final T value) {
        // key添加前缀
        key = REDIS_KEY_PREFIX + key;
        log.info("svc.add: key-{}, value-{}", key, value);

        // 判断key是否存在
        if (_redisTemplate.hasKey(key)) {
            final String msg = "添加失败，Key已存在";
            log.error("{}: key-{}", msg, key);
            return new Ro(ResultDic.FAIL, msg);
        }

        _redisTemplate.opsForValue().set(key, value);

        // 判断key是否存在
        if (_redisTemplate.hasKey(key)) {
            final String msg = "添加成功";
            log.info(msg);
            return new Ro(ResultDic.SUCCESS, msg);
        } else {
            final String msg = "添加失败";
            log.error("{}: key-{}, value-{}", msg, key, value);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    @Override
    public Ro modify(String key, final T value) {
        // key添加前缀
        key = REDIS_KEY_PREFIX + key;
        log.info("svc.modify: key-{}, value-{}", key, value);

        // 判断key是否存在
        if (!_redisTemplate.hasKey(key)) {
            final String msg = "修改失败，Key已存在";
            log.error("{}: key-{}", msg, key);
            return new Ro(ResultDic.FAIL, msg);
        }

        _redisTemplate.opsForValue().set(key, value);

        // 判断key是否存在
        if (_redisTemplate.hasKey(key) && _redisTemplate.opsForValue().get(key).equals(value)) {
            final String msg = "修改成功";
            log.info(msg);
            return new Ro(ResultDic.SUCCESS, msg);
        } else {
            final String msg = "修改失败";
            log.error("{}: key-{}, value={}", msg, key, value);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    @Override
    public Ro del(String key) {
        // key添加前缀
        key = REDIS_KEY_PREFIX + key;
        log.info("svc.del: key-{}", key);

        // 判断key是否存在
        if (_redisTemplate.hasKey(key)) {
            final String msg = "删除失败，Key并不存在";
            log.error("{}: key-{}", msg, key);
            return new Ro(ResultDic.FAIL, msg);
        }

        if (_redisTemplate.delete(key)) {
            final String msg = "删除成功";
            log.info("{}: key-{}", msg, key);
            return new Ro(ResultDic.SUCCESS, msg);
        } else {
            final String msg = "删除失败";
            log.error("{}: key-{}", msg, key);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    @Override
    public T get(String key) {
        // key添加前缀
        key = REDIS_KEY_PREFIX + key;
        log.info("svc.get: key-{}", key);

        final T result = _redisTemplate.opsForValue().get(key);
        log.info("svc.get return: value-{}", result);
        return result;
    }

}
