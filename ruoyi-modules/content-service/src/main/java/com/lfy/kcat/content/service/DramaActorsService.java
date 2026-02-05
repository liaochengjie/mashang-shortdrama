package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.ActorRoleInfoEntity;
import com.lfy.kcat.content.domain.DramaActors;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_actors(短剧演员关联表)】的数据库操作Service
* @createDate 2025-11-04 15:49:29
*/
public interface DramaActorsService extends IService<DramaActors> {

    List<ActorRoleInfoEntity> getDramaActorsInfo(Long dramaId);
}
