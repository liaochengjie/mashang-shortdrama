package com.lfy.kcat.interaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.interaction.domain.UserLikes;
import com.lfy.kcat.interaction.service.UserLikesService;
import com.lfy.kcat.interaction.mapper.UserLikesMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_likes(用户点赞记录表)】的数据库操作Service实现
* @createDate 2025-12-13 16:38:28
*/
@Service
public class UserLikesServiceImpl extends ServiceImpl<UserLikesMapper, UserLikes>
    implements UserLikesService{

}




