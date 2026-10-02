package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.Users;
import com.lfy.kcat.user.service.UsersService;
import com.lfy.kcat.user.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【users(用户表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




