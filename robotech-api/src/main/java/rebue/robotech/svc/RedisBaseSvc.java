package rebue.robotech.svc;

import rebue.robotech.ro.Ro;

public interface RedisBaseSvc<VALUE> {
    /**
     * 添加
     */
    Ro add(String key, VALUE value);

    /**
     * 修改
     */
    Ro modify(String key, VALUE value);

    /**
     * 删除
     */
    Ro del(String key);

    /**
     * 获取
     */
    VALUE get(String key);

}