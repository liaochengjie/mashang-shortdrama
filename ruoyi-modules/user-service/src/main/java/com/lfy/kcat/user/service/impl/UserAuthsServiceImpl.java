package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserAuths;
import com.lfy.kcat.user.service.UserAuthsService;
import com.lfy.kcat.user.mapper.UserAuthsMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_auths(用户认证表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserAuthsServiceImpl extends ServiceImpl<UserAuthsMapper, UserAuths>
    implements UserAuthsService{

}




