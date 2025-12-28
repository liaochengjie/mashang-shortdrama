package com.lfy.kcat.demoxxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelloService {

    @XxlJob("HelloXxl")
    public void SayHello(){
        XxlJobHelper.log("正在执行任务...");
        int a =10/0;
        System.out.println("正在执行sayHello"+a);
    }

    @XxlJob("tencentTranslate")
    public void tencentTranslateVod(){
        XxlJobHelper.log("开始执行腾讯云转码...");
        String jobParam = XxlJobHelper.getJobParam();
        String[] split = jobParam.split(",");
        String dramaId=split[0];
        String dramaName=split[1];
        String dramaTime=split[2];
        System.out.println("dramaId:"+dramaId);
        System.out.println("dramaName:"+dramaName);
        System.out.println("dramaTima:"+dramaTime);
        if(Long.parseLong(dramaTime)>60){
            XxlJobHelper.log("视频时长超过60s，无法上传");
            System.out.println("视频上传失败...");
            XxlJobHelper.handleFail("视频时长超过60s，请更换为短于60s的");
        }else{
            XxlJobHelper.log("视频上传成功");
            System.out.println("视频上传成功...");
        }

    }
}
