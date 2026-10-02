package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserPreferences;
import com.lfy.kcat.user.service.UserPreferencesService;
import com.lfy.kcat.user.mapper.UserPreferencesMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_preferences(用户偏好表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserPreferencesServiceImpl extends ServiceImpl<UserPreferencesMapper, UserPreferences>
    implements UserPreferencesService{

}




