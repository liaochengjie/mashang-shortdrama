package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserBlacklist;
import com.lfy.kcat.user.service.UserBlacklistService;
import com.lfy.kcat.user.mapper.UserBlacklistMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_blacklist(用户黑名单表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserBlacklistServiceImpl extends ServiceImpl<UserBlacklistMapper, UserBlacklist>
    implements UserBlacklistService{

}




