package com.lfy.kcat.user.business.impl;

import com.lfy.kcat.user.business.DramaBizService;
import com.lfy.kcat.user.feign.ContentServiceFeignClient;
import com.lfy.kcat.user.vo.PageReqVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.HomeDramaInfoDTO;
import org.dromara.common.core.dto.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.dromara.common.core.enums.FeignEnum;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.hutool.core.io.file.FileMode.r;

@Slf4j
@Service
public class DramaBizServiceImpl implements DramaBizService {

    @Autowired
    ContentServiceFeignClient contentServiceFeignClient;
    @Override
    public HomeFeaturedDTO getHomeFeature(PageReqDTO pageReqDTO) {
        log.info("正在远程调用获取首页精选视频数据:{}",pageReqDTO);
        R<HomeFeaturedDTO> r = contentServiceFeignClient.featured(pageReqDTO);
        //使用统一的拦截器响应，无需判断了
        return r.getData();
//        if(r.getCode()==200){
//            return r.getData();
//        }
//        log.info("远程调用短剧ID为{}的所有剧集数据返回异常",dramaId);
//        FeignEnum error = FeignEnum.SERVICE_DATA_ERROR;
//        throw new ServiceException(error.getMsg(), error.getCode());
    }

    @Override
    public HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId) {
        log.info("正在远程调用获取短剧ID为{}的所有剧集",dramaId);
        R<HomeDramaEpisodesDTO> r = contentServiceFeignClient.dramaEpisodes(dramaId);
        //使用统一的拦截器响应，无需判断了
        return r.getData();
//        if(r.getCode()==200){
//            return r.getData();
//        }
//        log.info("远程调用短剧ID为{}的所有剧集数据返回异常",dramaId);
//        FeignEnum error = FeignEnum.SERVICE_DATA_ERROR;
//        throw new ServiceException(error.getMsg(), error.getCode());
    }

    @Override
    public HomeDramaInfoDTO getDramaInfo(Long dramaId) {
        log.info("远程调用获取短剧ID为：{}的详情信息",dramaId);
        R<HomeDramaInfoDTO> r = contentServiceFeignClient.dramaInfo(dramaId);
        //使用统一的拦截器响应，无需判断了
        return r.getData();
//        if(r.getCode()==200){
//            return r.getData();
//        }
//        log.info("远程调用短剧ID为{}的所有剧集数据返回异常",dramaId);
//        FeignEnum error = FeignEnum.SERVICE_DATA_ERROR;
//        throw new ServiceException(error.getMsg(), error.getCode());
    }
}
