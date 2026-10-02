package com.lfy.kcat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.user.domain.UserBrowseHistory;
import com.lfy.kcat.user.service.UserBrowseHistoryService;
import com.lfy.kcat.user.mapper.UserBrowseHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_browse_history(用户短剧浏览历史表)】的数据库操作Service实现
* @createDate 2025-12-13 16:52:00
*/
@Service
public class UserBrowseHistoryServiceImpl extends ServiceImpl<UserBrowseHistoryMapper, UserBrowseHistory>
    implements UserBrowseHistoryService{

}




