package rebue.robotech.svc.impl;

import rebue.robotech.svc.RedisBaseSvc;

public abstract class RedisBaseSvcImpl<T> implements RedisBaseSvc<T> {

//    /**
//     * Key的前缀
//     */
//    protected String                 REDIS_KEY_PREFIX;
//
//    @Reference
//    private RedisTemplate<String, T> _redisTemplate;
//
//    @Override
//    public Ro add(String key, final T value) {
//        // key添加前缀
//        key = REDIS_KEY_PREFIX + key;
//        log.info("svc.add: key-{}, value-{}", key, value);
//
//        // 判断key是否存在
//        if (_redisTemplate.hasKey(key)) {
//            return new Ro(ResultDic.FAIL, "添加失败，Key已存在");
//        }
//
//        _redisTemplate.opsForValue().set(key, value);
//
//        // 判断key是否存在
//        if (_redisTemplate.hasKey(key)) {
//            return new Ro(ResultDic.SUCCESS, "添加成功");
//        } else {
//            return new Ro(ResultDic.FAIL, "添加失败");
//        }
//    }
//
//    @Override
//    public Ro modify(String key, final T value) {
//        // key添加前缀
//        key = REDIS_KEY_PREFIX + key;
//        log.info("svc.modify: key-{}, value-{}", key, value);
//
//        // 判断key是否存在
//        if (!_redisTemplate.hasKey(key)) {
//            return new Ro(ResultDic.FAIL, "修改失败，Key已存在");
//        }
//
//        _redisTemplate.opsForValue().set(key, value);
//
//        // 判断key是否存在
//        if (_redisTemplate.hasKey(key) && _redisTemplate.opsForValue().get(key).equals(value)) {
//            return new Ro(ResultDic.SUCCESS, "修改成功");
//        } else {
//            return new Ro(ResultDic.FAIL, "修改失败");
//        }
//    }
//
//    @Override
//    public Ro del(String key) {
//        // key添加前缀
//        key = REDIS_KEY_PREFIX + key;
//        log.info("svc.del: key-{}", key);
//
//        // 判断key是否存在
//        if (_redisTemplate.hasKey(key)) {
//            return new Ro(ResultDic.FAIL, "删除失败，Key并不存在");
//        }
//
//        if (_redisTemplate.delete(key)) {
//            return new Ro(ResultDic.SUCCESS, "删除成功");
//        } else {
//            return new Ro(ResultDic.FAIL, "删除失败");
//        }
//    }
//
//    @Override
//    public T get(String key) {
//        // key添加前缀
//        key = REDIS_KEY_PREFIX + key;
//        log.info("svc.get: key-{}", key);
//
//        final T result = _redisTemplate.opsForValue().get(key);
//        log.info("svc.get return: value-{}", result);
//        return result;
//    }

}
