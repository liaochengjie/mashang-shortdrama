package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Categories;
import com.lfy.kcat.content.domain.DramaCategories;
import com.baomidou.mybatisplus.extension.service.IService;
import feign.Param;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_categories(短剧分类关联表)】的数据库操作Service
* @createDate 2025-11-04 15:49:29
*/
public interface DramaCategoriesService extends IService<DramaCategories> {

    List<Categories> getDramaCategoriesInfo(Long dramaId);
}
