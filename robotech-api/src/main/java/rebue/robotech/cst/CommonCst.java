package rebue.robotech.cst;

/**
 * 常用常量
 */
public interface CommonCst {
    /**
     * 用户ID_系统(如果是系统的操作，比如定时器、采集器，请用此 ID 作为用户 ID)
     */
    Long USER_ID_SYS         = 0L;
    /**
     * 用户ID_三方(如果是第三方调用API，请用此 ID 作为用户 ID)
     */
    Long USER_ID_THIRD_PARTY = 1L;
}
