package rebue.robotech.dic;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用认证类型
 * 认证方式(0:未认证;1:共用Cookie;2:OIDC;3:OAuth2,4:CAS)
 */
@AllArgsConstructor
@Getter
public enum AuthenticationDic implements Dic {
    /**
     * 0: 未认证
     */
    UN_AUTH((byte) 0, "未认证"),
    /**
     * 1: 共用Cookie
     */
    SHARE_COOKIE((byte) 1, "共用COOKIE"),
    /**
     * 2: OIDC
     */
    OIDC_OAUTH2((byte) 2, "OIDC/OAUTH2"),
    /**
     * 3: CSA
     */
    CSA((byte) 3, "CSA");

    private final byte   code;
    private final String desc;

    @Override
    public String getName() {
        return name();
    }

    /**
     * springdoc显示枚举说明将会调用此方法
     */
    @Override
    public String toString() {
        return getCode() + "(" + getDesc() + ")";
    }

    /**
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口Dic
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    @JsonCreator // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    public static SqlDic getItem(final byte pcode) {
        final SqlDic result = (SqlDic) DicUtils.getItem(SqlDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

}
