package rebue.robotech.clone;

import com.github.pagehelper.PageInfo;
import rebue.wheel.api.ra.PageRa;

import java.util.List;

public interface CloneMapper<ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO> {
    PageRa<VO> pageInfoMapPageRa(@SuppressWarnings("rawtypes") PageInfo pageInfo);

    MO moMapMo(MO mo);

    VO moMapVo(MO mo);

    VO voMapVo(VO vo);

    MO voMapMo(VO vo);

    ADD_TO voMapAddTo(VO vo);

    MODIFY_TO voMapModifyTo(VO vo);

    ADD_TO addToMapAddTo(ADD_TO to);

    MO addToMapMo(ADD_TO to);

    MODIFY_TO modifyToMapModifyTo(MODIFY_TO to);

    MO modifyToMapMo(MODIFY_TO to);

    MODIFY_TO addToMapModifyTo(ADD_TO to);

    DEL_TO delToMapDelTo(DEL_TO to);

    MO delToMapMo(DEL_TO to);

    ONE_TO oneToMapOneTo(ONE_TO to);

    MO oneToMapMo(ONE_TO to);

    MO listToMapMo(LIST_TO to);

    MO pageToMapMo(PAGE_TO to);

    List<VO> moListMapVoList(List<MO> mo);
}
