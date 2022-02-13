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
     * 设置创建时间戳
     */
    default void setCreateTimestamp(final Long createTimestamp) {
    }

    /**
     * 设置修改时间戳
     */
    default void setUpdateTimestamp(final Long updateTimestamp) {
    }

    @JsonIgnore
    String getIdType();
}
