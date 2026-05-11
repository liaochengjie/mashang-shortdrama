package com.lfy.kcat.content.biz.impl;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lfy.kcat.content.biz.AuthCheckService;
import com.lfy.kcat.content.biz.BloomFilterTemplate;
import com.lfy.kcat.content.domain.DramaAuth;
import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.feign.CamundaFeignClient;
import com.lfy.kcat.content.mapper.DramaAuthMapper;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.lfy.kcat.content.service.DramaAuthService;
import com.lfy.kcat.content.vo.ManualAuthTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.dromara.common.core.dto.DramaAuthManualTaskDTO;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AuthCheckServiceImpl implements AuthCheckService {

    @Autowired
    DramaAuthService dramaAuthService;
    @Autowired
    CamundaFeignClient camundaFeignClient;

    @Autowired
    DramasMapper dramasMapper;

    @Autowired
    DramaAuthMapper dramaAuthMapper;

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;

    /**
     * 用于得到AI审核状态的其中的方法（通过短剧ID获取到流程ID）
     * @param dramaId
     * @return
     */
    @Override
    public String getProcessIdByDramaId(String dramaId) {
        DramaAuth dramaAuth = dramaAuthService.getOne(Wrappers.lambdaQuery(DramaAuth.class).eq(DramaAuth::getDramaId, dramaId));
        return dramaAuth.getProcessId();
    }

    /**
     * 获取短剧AI审核状态
     * @param dramaId
     * @return
     */
    public Integer getAiCheckStatus(String dramaId){

        Integer aiCheckStatus = 0;
        //获取流程ID
        String processId = getProcessIdByDramaId(dramaId);
        //用流程id来获取实例
        R<Map<String, Object>> R = camundaFeignClient.getProcessVariables(processId);
        if (R.getCode() == 200) {
            Map<String, Object> data = R.getData();
            log.info("通过远程调用获取的数据为{}",data);
            if (data != null) {
                int authName = Integer.parseInt(data.get("AuthName").toString());
                int authDescription = Integer.parseInt(data.get("AuthDescription").toString());
                if(authName==1&&authDescription==1){
                    return 1;
                }else {
                    return 0;
                }

            }
        }
        return aiCheckStatus;
    }

    @Override
    public void saveManualAuthData(ManualAuthTaskVo manualAuthTaskVo, String authorization) {
        log.info("人工审核更新数据库中,然后将camunda从人工审核推进到下一步ManualAuthTaskVo:{}",manualAuthTaskVo);
        //通过令牌来获取审核人
        //3、当前登录到系统中的人是谁。
        String payLoad = authorization.split("\\.")[1];
        String decode = SaBase64Util.decode(payLoad);

        JSONObject jsonObject = JSON.parseObject(decode);
        String userName = jsonObject.get("userName").toString();
        String processId = getProcessIdByDramaId(String.valueOf(manualAuthTaskVo.getDramaId()));

        //推进camunda流程
        DramaAuthManualTaskDTO dramaAuthManualTaskDTO = new DramaAuthManualTaskDTO();
        dramaAuthManualTaskDTO.setDramaId(manualAuthTaskVo.getDramaId());
        dramaAuthManualTaskDTO.setUserName(userName);
        dramaAuthManualTaskDTO.setProcessId(processId);
        dramaAuthManualTaskDTO.setAuditReason(manualAuthTaskVo.getAuditReason());
        dramaAuthManualTaskDTO.setAuditStatus(manualAuthTaskVo.getAuditStatus());

        camundaFeignClient.claimManualAuthTaskAndComplete(dramaAuthManualTaskDTO);


        //将短剧的id存入到布隆过滤器中
        bloomFilterTemplate.addDramaIdBloomFilter(manualAuthTaskVo.getDramaId());

    }

    @Override
    public void completeAuthUpdateDb(DramaAuthCompleteDTO dramaAuthCompleteDTO) {
        log.info("短剧数据库信息修改:{}",dramaAuthCompleteDTO);
        //将数据保存进数据库
        LambdaUpdateWrapper<Dramas> eq = Wrappers.lambdaUpdate(Dramas.class)
            .set(Dramas::getAuditStatus, dramaAuthCompleteDTO.getAuditStatus())
            .set(Dramas::getAuditReason, dramaAuthCompleteDTO.getAuditReason())
            .eq(Dramas::getDramaId, dramaAuthCompleteDTO.getDramaId());
        dramasMapper.update(eq);

        //更新审核日志
        LambdaUpdateWrapper<DramaAuth> eq1 = Wrappers.lambdaUpdate(DramaAuth.class)
            .set(DramaAuth::getAuthStatus, dramaAuthCompleteDTO.getAuditStatus().equals("2") ? "-1" : "1")
            .eq(DramaAuth::getDramaId, dramaAuthCompleteDTO.getDramaId());
        dramaAuthMapper.update(eq1);

    }
}
