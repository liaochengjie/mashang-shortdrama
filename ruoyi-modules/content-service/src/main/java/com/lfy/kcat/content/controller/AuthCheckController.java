package com.lfy.kcat.content.controller;

import com.lfy.kcat.content.biz.AuthCheckService;
import com.lfy.kcat.content.vo.ManualAuthTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
public class AuthCheckController {
    @Autowired
    AuthCheckService authCheckService;

    /**
     * 更新数据库状态
     * @param dramaAuthCompleteDTO
     * @return
     */

    @PutMapping("/dramas/updateAuthDb")
    public R updateDramaAuthStatus(@RequestBody DramaAuthCompleteDTO dramaAuthCompleteDTO) {
        authCheckService.completeAuthUpdateDb(dramaAuthCompleteDTO);
        return R.ok();
    }


    /**
     * 人工审核
     * @return
     */
    @PostMapping("/dramas/authcheck")
    public R manualCheckTask(@RequestBody ManualAuthTaskVo manualAuthTaskVo,
                             @RequestHeader("Authorization") String authorization) {
        //更新数据库表并且推进流程
        log.info("ManualAuthTaskVo:{}",manualAuthTaskVo);
        authCheckService.saveManualAuthData(manualAuthTaskVo,authorization);

        return R.ok();
    }

    /**
     * 获取短剧AI审核状态
     * @param dramaId
     * @return
     */
    @GetMapping("/dramas/authcheck/{dramaId}")
    public R getAiCheckStatus(@PathVariable("dramaId") String dramaId){

        //TODO 获取AI审核的状态
        Integer status = authCheckService.getAiCheckStatus(dramaId);
        return R.ok("success",status);
    }

}
