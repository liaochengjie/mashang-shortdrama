package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserFollows;
import com.lfy.kcat.user.service.UserFollowsService;
import com.lfy.kcat.user.mapper.UserFollowsMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_follows(用户关注表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserFollowsServiceImpl extends ServiceImpl<UserFollowsMapper, UserFollows>
    implements UserFollowsService{

}




