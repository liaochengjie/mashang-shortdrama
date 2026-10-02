package com.lfy.kcat.interaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.interaction.domain.UserDislikes;
import com.lfy.kcat.interaction.service.UserDislikesService;
import com.lfy.kcat.interaction.mapper.UserDislikesMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_dislikes(用户点踩记录表)】的数据库操作Service实现
* @createDate 2025-12-13 16:38:28
*/
@Service
public class UserDislikesServiceImpl extends ServiceImpl<UserDislikesMapper, UserDislikes>
    implements UserDislikesService{

}




