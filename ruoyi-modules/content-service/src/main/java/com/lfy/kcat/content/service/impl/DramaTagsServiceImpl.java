package com.lfy.kcat.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.content.domain.DramaTags;
import com.lfy.kcat.content.domain.Tags;
import com.lfy.kcat.content.service.DramaTagsService;
import com.lfy.kcat.content.mapper.DramaTagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 廖成杰
* @description 针对表【drama_tags(短剧标签关联表)】的数据库操作Service实现
* @createDate 2025-11-04 15:49:29
*/
@Service
public class DramaTagsServiceImpl extends ServiceImpl<DramaTagsMapper, DramaTags>
    implements DramaTagsService{
    @Autowired
    private DramaTagsMapper dramaTagsMapper;

    @Override
    public List<Tags> getDramaTagsByDramaId(Long dramaId) {
        return dramaTagsMapper.getDramaTagsByDramaId(dramaId);
    }
}




