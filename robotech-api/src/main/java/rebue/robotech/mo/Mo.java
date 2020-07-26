package rebue.robotech.mo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Mo<ID> {
    ID getId();

    void setId(ID id);

    @JsonIgnore
    String getIdType();
}
