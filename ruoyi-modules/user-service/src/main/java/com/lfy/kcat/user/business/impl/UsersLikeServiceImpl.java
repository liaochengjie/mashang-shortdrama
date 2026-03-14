package com.lfy.kcat.user.business.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.lfy.kcat.user.business.UsersLikeService;
import com.lfy.kcat.user.exception.UserServiceExceptionEnume;
import com.lfy.kcat.user.feign.InteractionServiceFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.KafkaConstant;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.dromara.common.core.dto.evert.LikeEvent;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsersLikeServiceImpl implements UsersLikeService {

    @Autowired
    InteractionServiceFeignClient interactionServiceFeignClient;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void likeEpisode(UsersLikeDTO usersLikeDTO) {
        // 1. 校验用户是否存在
        boolean login = StpUtil.isLogin();
        if (!login){
            UserServiceExceptionEnume userNotLogin = UserServiceExceptionEnume.USER_NOT_LOGIN;
            throw new ServiceException(userNotLogin.getMessage(), userNotLogin.getCode());
        }

//        // 2. 调用interaction-service点赞接口
//        interactionServiceFeignClient.likeEpisode(usersLikeDTO);

        // 3. 构建点赞事件
        LikeEvent likeEvent = new LikeEvent();
        likeEvent.setEpisodeId(usersLikeDTO.getEpisodeId());
        long userId = StpUtil.getLoginIdAsLong();
        likeEvent.setUserId(userId);
        likeEvent.setAction(usersLikeDTO.getLike());
        // 4. 发送点赞事件到Kafka
        String jsonStr = JSONUtil.toJsonStr(likeEvent);
        long currentTimeMillis = System.currentTimeMillis();
        String key=""+currentTimeMillis;
        kafkaTemplate.send(KafkaConstant.LIKE_EVENT_TOPIC, key , jsonStr);
        log.info("发送点赞事件到Kafka: {}",jsonStr);

    }
}
