package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserCollections;
import com.lfy.kcat.user.service.UserCollectionsService;
import com.lfy.kcat.user.mapper.UserCollectionsMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_collections(用户收藏表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserCollectionsServiceImpl extends ServiceImpl<UserCollectionsMapper, UserCollections>
    implements UserCollectionsService{

}




