package rebue.robotech.clone;

import cn.zhxu.bs.SearchResult;
import com.github.pagehelper.PageInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import rebue.wheel.api.ra.PageRa;

@Mapper
public interface MapStructMapper {
    MapStructMapper INSTANCE = Mappers.getMapper(MapStructMapper.class);

    PageRa pageInfoMapPageRa(PageInfo pageInfo);

    @Mapping(source = "totalCount", target = "total", qualifiedByName = "NumberMapLong")
    @Mapping(source = "dataList", target = "list")
    PageRa searchResultMapPageRa(SearchResult pageInfo);

    @Named("NumberMapLong")
    default Long numberMapLong(Number value) {
        return value.longValue();
    }
}
