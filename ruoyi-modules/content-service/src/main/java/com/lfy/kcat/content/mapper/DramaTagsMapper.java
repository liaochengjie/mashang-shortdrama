package com.lfy.kcat.content.mapper;

import com.lfy.kcat.content.domain.DramaTags;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lfy.kcat.content.domain.Tags;
import feign.Param;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_tags(短剧标签关联表)】的数据库操作Mapper
* @createDate 2025-11-04 15:49:29
* @Entity com.lfy.kcat.content.domain.DramaTags
*/
public interface DramaTagsMapper extends BaseMapper<DramaTags> {

    List<Tags> getDramaTagsByDramaId(@Param("dramaId") Long dramaId);
}




