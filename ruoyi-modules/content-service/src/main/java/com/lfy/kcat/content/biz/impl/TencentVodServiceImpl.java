package com.lfy.kcat.content.biz.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lfy.kcat.content.biz.TencentVodService;
import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.domain.Episodes;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.lfy.kcat.content.mapper.EpisodesMapper;
import com.lfy.kcat.content.vod.properties.VodProperties;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dromara.common.core.constant.VodConstant;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.sql.Wrapper;
import java.util.List;

@Service
@Slf4j
public class TencentVodServiceImpl implements TencentVodService {

    @Autowired
    DramasMapper dramasMapper;

    @Autowired
    VodClient vodClient;

    @Autowired
    VodUploadClient vodUploadClient;

    @Autowired
    VodProperties vodProperties;

    @Autowired
    EpisodesMapper episodesMapper;
    @Override
    public void uploadDrama(Long dramaId) {
        Dramas dramas = dramasMapper.selectById(dramaId);


        //TODO 处理预告片的信息流
        String trailerUrl = dramas.getTrailerUrl();
        //判断是否存在预告片
        if(StringUtils.isNotEmpty(trailerUrl)) {
            log.info("存在预告片，正在进行预告片的上传转流...");

            //进行上传并且转码预告片然后返回预告片的url,更新数据库
            uploadAndTranslateInfoFlowReturnResult(dramaId,trailerUrl);

        }else{
            log.info("不存在预告片，现在开始在剧集表中获取预告片...");
            //如果不存在预告片
            //先取来所有的剧集
            LambdaQueryWrapper<Episodes> eq = Wrappers.lambdaQuery(Episodes.class)
                .eq(Episodes::getDramaId, dramaId);
            List<Episodes> episodes = episodesMapper.selectList(eq);
            //难道预告片
            //如果有预告片，那么取第一个预告片即可
            //如果没有预告片，那么取第一集
            Episodes epis = episodes.stream().filter(ele -> ele.getIsTrailer().equals(1L))
                .findFirst()
                .orElse(episodes.get(0));
            String videoUrl = epis.getVideoUrl();
            //上传并解码拿到结果,然后更新数据库
            uploadAndTranslateInfoFlowReturnResult(dramaId,videoUrl);
        }


        //TODO视频画质流 处理每一集获取到所有转换结果并保存数据库；长业务
        //后台慢慢执行
    }
    private void uploadAndTranslateInfoFlowReturnResult(Long dramaId,String trailerUrl) {
        //1.上传预告片
        String fileId = uploadVodTranslateInfoFlow(trailerUrl);
        //2.进行转码并且获取转码后url
        String fileUrl = waitTranslateResult(fileId);
        //阻塞等待
        while(fileUrl == null) {
            //如果未获取到就线程睡3秒后重新获取
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            fileUrl=waitTranslateResult(fileId);
        }
        //走到这那么已经获取到信息流了
        //3.更新drama数据库
        LambdaUpdateWrapper<Dramas> eq = Wrappers.lambdaUpdate(Dramas.class)
            .set(Dramas::getTrailerUrl, fileUrl)
            .eq(Dramas::getDramaId, dramaId);
        dramasMapper.update(eq);
    }

    /**
     * 上传并且转换信息流
     * @param trailerUrl
     */
    //根据url来上传预告片
    private String uploadVodTranslateInfoFlow(String trailerUrl) {
        String result=null;
        try {
            //获取该剧的结尾名字
            String[] split = trailerUrl.split("_");
            String name=split[1];

            //根据url地址进行上传
            File trailerFile = uploadAndSaveTemp(trailerUrl,name);
            //获取一个上传请求
            VodUploadRequest vodUploadRequest = new VodUploadRequest();
            //获取其盘位的路径
            vodUploadRequest.setMediaFilePath(trailerFile.getAbsolutePath());
            //放入上传的appId
            vodUploadRequest.setSubAppId(vodProperties.getStuAppId());
            //设置转码方式
            vodUploadRequest.setProcedure(VodConstant.INFO_FLOW_PROCEDURE);
            //进行上传
            VodUploadResponse response = vodUploadClient.upload(vodProperties.getRegion(), vodUploadRequest);
            //获取上传腾讯云后的唯一ID
            result = response.getFileId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    /**
     * 等待转码结果
     * @param fileId
     * @return
     */
    private String waitTranslateResult(String fileId) {
        //拉取事件
        PullEventsRequest req = new PullEventsRequest();
        req.setSubAppId(vodProperties.getStuAppId());
        //创建结果对象
        String result=null;
        try {
            PullEventsResponse pullEventsResponse = vodClient.PullEvents(req);
            //双非空判断
            if(pullEventsResponse!=null && pullEventsResponse.getEventSet()!=null) {
                log.info("获取腾讯云事件...");
                for (EventContent eventContent : pullEventsResponse.getEventSet()) {
                    //选定出进行事件为转码的事件
                    if (eventContent.getEventType().equals(VodConstant.PROCEDURE_STATE_CHANGED)){
                        ProcedureTask event = eventContent.getProcedureStateChangeEvent();
                        //判断事件ID是否相同并且是否已经完成
                        if(event.getFileId().equals(fileId)&& event.getStatus().equals(VodConstant.STATUS_FINISH)) {
                            //确认事件已经收到，防止重复获取
                            ConfirmEventsRequest confirmEventsRequest = new ConfirmEventsRequest();
                            confirmEventsRequest.setEventHandles(new String[]{eventContent.getEventHandle()});
                            confirmEventsRequest.setSubAppId(vodProperties.getStuAppId());
                            vodClient.ConfirmEvents(confirmEventsRequest);
                            //获取转码后URL
                            result=event.getFileUrl();
                        }
                    }
                }
            }
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    /**
     * 下载喝保存临时数据
     * @param trailerUrl
     * @param name
     * @return
     */
    private File uploadAndSaveTemp(String trailerUrl,String name) {

        File trailerFile = null;
        URL url = null;
        try {
            url = URI.create(trailerUrl).toURL();
            try(InputStream inputStream = url.openStream();) {

                trailerFile = File.createTempFile("aaa", "_" + name);
                log.info("正在下载和保存文件为{}",trailerFile);
                FileUtil.copyFile(inputStream, trailerFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return trailerFile;
    }
}
