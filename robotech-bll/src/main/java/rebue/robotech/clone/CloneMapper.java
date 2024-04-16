package rebue.robotech.clone;

import com.github.pagehelper.PageInfo;
import rebue.wheel.api.ra.PageRa;

import java.util.List;

public interface CloneMapper<ADD_TO, MODIFY_TO, DEL_TO, ONE_TO, LIST_TO, PAGE_TO, MO, VO> {
    PageRa pageInfoMapPageRa(PageInfo pageInfo);

    VO moMapVo(MO to);

    List<VO> moListMapVoList(List<MO> to);

    MO addToMapMo(ADD_TO to);

    MO modifyToMapMo(MODIFY_TO to);

    MO delToMapMo(DEL_TO to);

    MO oneToMapMo(ONE_TO to);

    MO listToMapMo(LIST_TO to);

    MO pageToMapMo(PAGE_TO to);

    MODIFY_TO addToMapModifyTo(ADD_TO to);
}
