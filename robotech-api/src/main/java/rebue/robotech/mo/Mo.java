package rebue.robotech.mo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Mo<ID> {
    /**
     * 获取ID
     *
     * @return ID
     */
    ID getId();

    /**
     * 设置ID
     *
     * @param id ID
     */
    void setId(ID id);

    /**
     * 获取是否启用
     */
    default void setIsEnabled(Boolean isEnabled) {
    }

    ;

    /**
     * 获取创建时间戳
     */
    default Long getCreateTimestamp() {
        return null;
    }

    /**
     * 设置创建时间戳
     */
    default void setCreateTimestamp(final Long createTimestamp) {
    }

    /**
     * 获取修改时间戳
     */
    default Long getUpdateTimestamp() {
        return null;
    }

    /**
     * 设置修改时间戳
     */
    default void setUpdateTimestamp(final Long updateTimestamp) {
    }

    /**
     * 获致ID数据类型(一般分为Long,String)
     *
     * @return ID数据类型
     */
    @JsonIgnore
    String getIdType();
}
