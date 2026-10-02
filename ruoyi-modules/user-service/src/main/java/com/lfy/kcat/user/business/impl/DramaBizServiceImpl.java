package com.lfy.kcat.user.business.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lfy.kcat.user.business.DramaBizService;
import com.lfy.kcat.user.cache.CacheData;
import com.lfy.kcat.user.constant.RedisConst;
import com.lfy.kcat.user.feign.ContentServiceFeignClient;
import com.lfy.kcat.user.template.BloomFilterTemplate;
import com.lfy.kcat.user.template.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liaochengjie
 */
@Slf4j
@Service
public class DramaBizServiceImpl implements DramaBizService {
    @org.springframework.beans.factory.annotation.Value("${rag.enabled:false}")
    private boolean ragEnabled;

    @Autowired
    ContentServiceFeignClient contentServiceFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RedisService redisService;
    @Override
    public HomeFeaturedDTO getHomeFeature(PageReqDTO pageReqDTO) {
        if (ragEnabled) return contentServiceFeignClient.featured(pageReqDTO).getData();
        String cacheKey= RedisConst.HOME_FEATURE_KEY
            +pageReqDTO.getPage()+":"
            +pageReqDTO.getPageSize();
        //抽取后的为
        HomeFeaturedDTO data = redisService.getData(cacheKey, HomeFeaturedDTO.class);
        if(data!=null){{
            log.info("缓存击中，直接返回数据：{}",data);
            return data;
        }}
        log.info("缓存{}的值判断是否为空值",cacheKey);
        boolean dataIsNullValue = redisService.dataIsNullValue(cacheKey);
        if(dataIsNullValue){
            log.info("缓存击中，但是数据为空");
            return new HomeFeaturedDTO();
        }

        log.info("缓存{}判断为非空值,缓存未击中，需要进行远程查询,并且进行写入缓存",cacheKey);
        R<HomeFeaturedDTO> featured = contentServiceFeignClient.featured(pageReqDTO);
        redisService.saveData(cacheKey,featured.getData(), RedisConst.DEFAULT_TIMEOUT);
        return featured.getData();


        //未抽取的这是
        //return getHomefeaturedDTOByOldRedisWay(pageReqDTO, cacheKey);


        //使用统一的拦截器响应，无需判断了

    }

    @Nullable
    private HomeFeaturedDTO getHomfeaturedDTOByOldRedisWay(PageReqDTO pageReqDTO, String cacheKey) {
        String json = redisTemplate.opsForValue().get(cacheKey);

        if(StringUtils.isEmpty(json)){
            log.info("缓存未命中，需要进行远程查询,并且进行写入缓存");
            //缓存未命中
            //进行查询并且写入缓存
            log.info("正在远程调用获取首页精选视频数据:{}", pageReqDTO);
            R<HomeFeaturedDTO> r = contentServiceFeignClient.featured(pageReqDTO);
            HomeFeaturedDTO data = r.getData();
            String jsonString = JsonUtils.toJsonString(data);
            redisTemplate.opsForValue().set(cacheKey,jsonString,3, TimeUnit.DAYS);
            return r.getData();
        }

        HomeFeaturedDTO homeFeaturedDTO = JsonUtils.parseObject(json, HomeFeaturedDTO.class);
        log.info("缓存命中，直接返回数据：{}",homeFeaturedDTO);
        return homeFeaturedDTO;
    }

    @CacheData(cacheKey = RedisConst.HOME_DRAMA_EPISODES_KEY,
    bloomFilterName =RedisConst.DRAMA_BF)
    public HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId) {
        R<HomeDramaEpisodesDTO> homeDramaEpisodesDTOR = contentServiceFeignClient.dramaEpisodes(dramaId);
        HomeDramaEpisodesDTO homeDramaEpisodesDTO = homeDramaEpisodesDTOR.getData();
        return homeDramaEpisodesDTO;
    }

    @CacheData(cacheKey=RedisConst.HOME_DRAMA_INFO,
    bloomFilterName = RedisConst.DRAMA_BF)
    public HomeDramaInfoDTO getDramaInfo(Long dramaId) {
        log.info("远程调用获取短剧ID为：{}的详情信息",dramaId);
        R<HomeDramaInfoDTO> r = contentServiceFeignClient.dramaInfo(dramaId);
        //使用统一的拦截器响应，无需判断了
        return r.getData();
    }

    /**
     * 判断用户是否点赞了该集数
     * @param episode
     *
     * @return
     */
    @Override
    public Boolean getUserIsLike(String episode) {
        boolean login = StpUtil.isLogin();
        if(!login){
            return false;
        }
        long userId = StpUtil.getLoginIdAsLong();
        Boolean isLiked = redisTemplate.opsForSet().isMember("likes"+episode,userId+"");
        return isLiked;
    }
}
