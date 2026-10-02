package com.lfy.kcat.content.biz.impl;

import com.lfy.kcat.content.biz.RagReleaseService;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lfy.kcat.content.Xxl.XxlHttp;
import com.lfy.kcat.content.biz.TencentVodService;
import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.domain.Episodes;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.lfy.kcat.content.mapper.EpisodesMapper;
import com.lfy.kcat.content.vo.EpisodeVodRelation;
import com.lfy.kcat.content.vod.properties.VodProperties;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.VodConstant;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liaochengjie
 */
@Service
@Slf4j
public class TencentVodServiceImpl implements TencentVodService {
    @Autowired
    private RagReleaseService ragRelease;

    private void rejectManagedLegacy(Long dramaId) {
        if (ragRelease.managed(dramaId)) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"LEGACY_PROCESS_REQUIRES_MANUAL_MAPPING");
    }

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

    @Autowired
    XxlHttp xxlHttp;
    @Override
    public void vodTranslator(Long dramaId) {
        rejectManagedLegacy(dramaId);
        //获取登录时候的cookie信息
        HttpCookie httpCookie = xxlHttp.mockLogin();
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("id","7");
        infoMap.put("executorParam",dramaId);
        infoMap.put("addressList","");
        //调用其向xxlJob发送执行信息流转换的命令
        xxlHttp.traggerJob(httpCookie,infoMap);

        HashMap<String, Object> qualityMap = new HashMap<>();
        qualityMap.put("id","8");
        qualityMap.put("executorParam",dramaId);
        qualityMap.put("addressList","");
        xxlHttp.traggerJob(httpCookie,qualityMap);
    }


    @Override
    public void vodInfoFlowTranslator(Long dramaId) {
        rejectManagedLegacy(dramaId);
        log.info("正在进行信息流的上传转码（即预告片）");
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
        //TODO后台慢慢执行；【长业务容忍稳定性变数】
        //TODO数据库和腾讯云数据库做到最终一执行【长作业后台慢慢执行，直到完成】
    }

    @Override
    public void vodQualityTranslator(Long dramaId) {
        rejectManagedLegacy(dramaId);
        log.info("正在进行画质流的上传转码(即正片)");
        //获取所有的剧集
        LambdaQueryWrapper<Episodes> eq = Wrappers
            .lambdaQuery(Episodes.class)
            .eq(Episodes::getDramaId, dramaId);

        //获取所有剧集在minio中的url
        List<Episodes> list = episodesMapper.selectList(eq);

        List<EpisodeVodRelation> episodeVodRelationList = list.stream().map(epi -> {
            EpisodeVodRelation episodeVodRelation = new EpisodeVodRelation();
            episodeVodRelation.setEpiId(String.valueOf(epi.getEpisodeId()));
            //TODO 上传并且转码

            String fileId = uploadVodAndTranslate(epi.getVideoUrl(), VodConstant.QUALITY_FLOW_PROCESS);
            //TODO 收集放入每一集在腾讯云的Id
            episodeVodRelation.setFileId(fileId);
            return episodeVodRelation;
        }).toList();

        //调用waitQualityTranslateResult来获取返回的EpicodeVodRelation
        for (EpisodeVodRelation episodeVodRelation : episodeVodRelationList) {
            EpisodeVodRelation relation = waitQualityTranslateResult(episodeVodRelation.getFileId());

            while(StringUtils.isEmpty(relation.getLowUrl())){
                log.info("画质流的EpisodeVodRelation还未返回，正在进行阻塞等待");
                //阻塞等待，直到云点播返回数据为止
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    log.error("等待转码失败",e);
                }
                relation=waitQualityTranslateResult(episodeVodRelation.getFileId());
            }
            log.info("阻塞等待结束,开始为episodeVodRelation赋值URL");
            episodeVodRelation.setHdUrl(relation.getHdUrl());
            episodeVodRelation.setSdUrl(relation.getSdUrl());
            episodeVodRelation.setLowUrl(relation.getLowUrl());
        }
        //进行保存剧集表的更新
        for (EpisodeVodRelation relation : episodeVodRelationList) {
            log.info("即将更新的数据:{}",relation);
            LambdaUpdateWrapper<Episodes> wrapper = Wrappers.lambdaUpdate(Episodes.class)
                .set(Episodes::getVideoUrlHd, relation.getHdUrl())
                .set(Episodes::getVideoUrlSd, relation.getSdUrl())
                .set(Episodes::getVideoUrlLow, relation.getLowUrl())
                .eq(Episodes::getEpisodeId, relation.getEpiId());
            episodesMapper.update(wrapper);
            log.info("剧集:{},清晰度更新完成",relation.getEpiId());
        }



    }



    /**
     * 1.全平台临时文件兼容性：JaVa提供任何平台准备临时目录，创建文件
     * 2.可靠性的事件消费：做完的事情，保存事件：主动拉取事件。判断是自已的事件。回复确认
     * 3.阻塞等待结果返回（无限次稍后重试）：判断事件是否FINISH，才能获取结果。
     * @param dramaId
     * @param trailerUrl
     */
    private void uploadAndTranslateInfoFlowReturnResult(Long dramaId,String trailerUrl) {
        //1.上传预告片
        String fileId = uploadVodAndTranslate(trailerUrl,VodConstant.INFO_FLOW_PROCEDURE);
        //2.进行转码并且获取转码后url
        String fileUrl = waitInfoFlowTranslateResult(fileId);
        //阻塞等待
        while(fileUrl == null) {
            //如果未获取到就线程睡4秒后重新获取
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            fileUrl= waitInfoFlowTranslateResult(fileId);
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
     * @param url
     * @param flowName
     * @return
     */
    //根据url来上传预告片
    private String uploadVodAndTranslate(String url, String flowName) {
        String result=null;
        try {


            //根据url地址进行上传
            //获取并创建一个临时文件位置
            File trailerFile = uploadAndSaveTemp(url);

            //获取一个上传请求
            VodUploadRequest vodUploadRequest = new VodUploadRequest();
            //获取其盘位的路径
            vodUploadRequest.setMediaFilePath(trailerFile.getAbsolutePath());
            //放入上传的appId
            vodUploadRequest.setSubAppId(vodProperties.getStuAppId());
            //设置转码方式
            vodUploadRequest.setProcedure(flowName);
            //进行上传
            VodUploadResponse response = vodUploadClient.upload(vodProperties.getRegion(), vodUploadRequest);
            //获取上传腾讯云后的唯一ID
            result = response.getFileId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("视频成功进行上传，准备进行转码处理，上传腾讯云的ID为:{}",result);
        return result;
    }

    /**
     * 1.适当调节等待时间，以防止性能浪费
     * 2.所有转码异常都不要抛，这样会打断流程，用log.error来打印错误日志
     * @param fileId
     * @return
     */
    private EpisodeVodRelation waitQualityTranslateResult(String fileId){
        log.info("正在进行fileId为{}的画质流转码",fileId);
        //拉取事件
        PullEventsRequest req = new PullEventsRequest();
        req.setSubAppId(vodProperties.getStuAppId());
        //创建结果对象
        EpisodeVodRelation episodeVodRelation = new EpisodeVodRelation();
        try {
            PullEventsResponse pullEventsResponse = vodClient.PullEvents(req);

            //双非空判断
            if(pullEventsResponse!=null && pullEventsResponse.getEventSet()!=null) {
                log.info("画质流获取到腾讯云事件...{}",pullEventsResponse);
                for (EventContent eventContent : pullEventsResponse.getEventSet()) {
                    //选定出进行事件为转码的事件
                    if (eventContent.getEventType().equals(VodConstant.PROCEDURE_STATE_CHANGED)){

                        ProcedureTask event = eventContent.getProcedureStateChangeEvent();
                        log.info("{}=={}",event.getFileId(),fileId);
                        //判断事件ID是否相同并且是否已经完成
                        if(event.getFileId().equals(fileId)&& event.getStatus().equals(VodConstant.STATUS_FINISH)) {

                            log.info("确认事件ID和事件已经完成");
                            //确认事件已经收到，防止重复获取
                            ConfirmEventsRequest confirmEventsRequest = new ConfirmEventsRequest();
                            confirmEventsRequest.setEventHandles(new String[]{eventContent.getEventHandle()});
                            confirmEventsRequest.setSubAppId(vodProperties.getStuAppId());

                            //TODO 获取转码后URL

                            MediaProcessTaskResult[] mediaProcessResultSet = event.getMediaProcessResultSet();

                            //遍历所有的返回结果
                            for (MediaProcessTaskResult mediaProcessTaskResult : mediaProcessResultSet) {
                                //获取到返回的输出结果
                                MediaTranscodeItem output = mediaProcessTaskResult.getTranscodeTask().getOutput();
                                //判断画质的区别然后赋值到EpicodeVodRelation来进行赋值返回
                                log.info("mediaProcessTaskResult中的output的画质为:{}",output.getWidth());
                                switch (output.getWidth() + "") {
                                    case "1080":
                                        episodeVodRelation.setHdUrl(output.getUrl());
                                        log.info("1080p转码成功");
                                        break;
                                    case "720":
                                        episodeVodRelation.setSdUrl(output.getUrl());
                                        log.info("720p转码成功");
                                        break;
                                    case "480":
                                        episodeVodRelation.setLowUrl(output.getUrl());
                                        log.info("480p转码成功");
                                        break;
                                }
                                log.info("画质解析数据完成:{}", output);

                            }
                            vodClient.ConfirmEvents(confirmEventsRequest);
                            log.info("成功确认事件{}", confirmEventsRequest);
                            return episodeVodRelation;


                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("画质流转码失败，请稍后重试",e);
            XxlJobHelper.log("等待画质流转码事件失败，请稍后重试:{}",e.getMessage());
        }
        return episodeVodRelation;
    }


    /**
     * 等待信息流转码结果
     * @param fileId
     * @return
     */
    private String waitInfoFlowTranslateResult(String fileId) {
        log.info("正在进行fileId为{}的信息流转码",fileId);
        //拉取事件
        PullEventsRequest req = new PullEventsRequest();
        req.setSubAppId(vodProperties.getStuAppId());
        //创建结果对象
        String result=null;
        try {
            PullEventsResponse pullEventsResponse = vodClient.PullEvents(req);
            //双非空判断
            if(pullEventsResponse!=null && pullEventsResponse.getEventSet()!=null) {
                log.info("信息流转换中获取腾讯云事件...");
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
                            //TODO 获取转码后URL
                            MediaProcessTaskResult[] mediaProcessResultSet = event.getMediaProcessResultSet();
                            if(mediaProcessResultSet.length!=0){
                                log.info("信息流转换中在等待腾讯云事件结果时候,mediaProcessResultSer为非空");
                                MediaProcessTaskTranscodeResult transcodeTask = mediaProcessResultSet[0].getTranscodeTask();
                                String url = transcodeTask.getOutput().getUrl();
                                result=url;
                            }else {
                                log.info("信息流转换中在获取腾讯云事件时候,mediaProcessResultSer为空");
                            }
                            return result;
                        }
                    }
                }
            }
        } catch (TencentCloudSDKException e) {
            log.error("信息流转码失败，请稍后试",e);
        }
        return result;
    }


    /**
     * 下载喝保存临时数据
     * @param url
     * @return
     */
    private File uploadAndSaveTemp(String url) {
        //获取该剧的结尾名字
        String[] split = url.split("_");
        String name=split[1];

        File fileNow = null;
        URL urlNow = null;
        try {
            urlNow = URI.create(url).toURL();
            try(InputStream inputStream = urlNow.openStream();) {

                fileNow = File.createTempFile("aaa", "_" + name);
                log.info("正在下载和保存文件为{}",fileNow);
                FileUtil.copyFile(inputStream, fileNow, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileNow;
    }
}
