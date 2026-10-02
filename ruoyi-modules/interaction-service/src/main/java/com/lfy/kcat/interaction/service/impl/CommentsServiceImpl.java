package com.lfy.kcat.interaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.interaction.domain.Comments;
import com.lfy.kcat.interaction.service.CommentsService;
import com.lfy.kcat.interaction.mapper.CommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【comments(评论表)】的数据库操作Service实现
* @createDate 2025-12-13 16:38:28
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService{

}




