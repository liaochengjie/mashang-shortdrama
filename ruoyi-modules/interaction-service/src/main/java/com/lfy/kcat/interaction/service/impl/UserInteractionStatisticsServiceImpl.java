package com.lfy.kcat.interaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.kcat.interaction.domain.UserInteractionStatistics;
import com.lfy.kcat.interaction.service.UserInteractionStatisticsService;
import com.lfy.kcat.interaction.mapper.UserInteractionStatisticsMapper;
import org.springframework.stereotype.Service;

/**
* @author liaochengjie
* @description 针对表【user_interaction_statistics(用户互动统计表)】的数据库操作Service实现
* @createDate 2025-12-13 16:38:28
*/
@Service
public class UserInteractionStatisticsServiceImpl extends ServiceImpl<UserInteractionStatisticsMapper, UserInteractionStatistics>
    implements UserInteractionStatisticsService{

}




