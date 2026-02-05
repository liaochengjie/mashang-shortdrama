package com.lfy.kcat.content.mapper;

import com.lfy.kcat.content.domain.Categories;
import com.lfy.kcat.content.domain.DramaCategories;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_categories(短剧分类关联表)】的数据库操作Mapper
* @createDate 2025-11-04 15:49:29
* @Entity com.lfy.kcat.content.domain.DramaCategories
*/
public interface DramaCategoriesMapper extends BaseMapper<DramaCategories> {

    List<Categories> getDramaCategoriesInfo(@Param("dramaId") Long dramaId);
}




