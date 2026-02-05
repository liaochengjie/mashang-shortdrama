package com.lfy.kcat.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.content.domain.ActorRoleInfoEntity;
import com.lfy.kcat.content.domain.DramaActors;
import com.lfy.kcat.content.service.DramaActorsService;
import com.lfy.kcat.content.mapper.DramaActorsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_actors(短剧演员关联表)】的数据库操作Service实现
* @createDate 2025-11-04 15:49:29
*/
@Service
public class DramaActorsServiceImpl extends ServiceImpl<DramaActorsMapper, DramaActors>
    implements DramaActorsService{

    @Autowired
    private DramaActorsMapper dramaActorsMapper;
    @Override
    public List<ActorRoleInfoEntity> getDramaActorsInfo(Long dramaId) {
        return dramaActorsMapper.getDramaActorsInfo(dramaId);
    }
}




