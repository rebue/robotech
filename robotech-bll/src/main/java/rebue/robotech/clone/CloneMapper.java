package rebue.robotech.clone;

public interface CloneMapper<ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO> {

    MO addToMapMo(ADD_TO to);

    MO modifyToMapMo(MODIFY_TO to);

    MO delToMapMo(DEL_TO to);

    MO oneToMapMo(ONE_TO to);

    MO listToMapMo(LIST_TO to);

    MO pageToMapMo(PAGE_TO to);

    MODIFY_TO addToMapModifyTo(ADD_TO to);
}
