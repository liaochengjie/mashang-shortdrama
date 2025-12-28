package com.lfy.kcat.content.job;

import com.lfy.kcat.content.biz.TencentVodService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VodTranslatorJob {
    @Autowired
    TencentVodService tencentVodService;

    @XxlJob(value = "vodInfoFlowTranslator")
    void vodInfoFlowTranslator(){
        //获取传入参数
        String jobParam = XxlJobHelper.getJobParam();
        log.info("自动化robot的xxlJob进行信息流转换中:{}",jobParam);
        if(jobParam!=null) {
            //参数传入的即为短剧ID
            long dramaId = Long.parseLong(jobParam);
            tencentVodService.vodInfoFlowTranslator(dramaId);
            log.info("成功信息流转换,短剧Id为:{}",dramaId);
        }
    }


    @XxlJob(value = "vodQualifyTranslator")
    void vodQualifyTranslator(){
        //获取传入参数
        String jobParam = XxlJobHelper.getJobParam();
        log.info("自动化robot的xxlJob进行画质流转换中:{}",jobParam);
        if(jobParam!=null) {
            //参数传入的即为短剧ID
            long dramaId = Long.parseLong(jobParam);
            tencentVodService.vodQualityTranslator(dramaId);
            log.info("成功画质流转换,短剧Id为:{}",dramaId);
        }
    }
}
