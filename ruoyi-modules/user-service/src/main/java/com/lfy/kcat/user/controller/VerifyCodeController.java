package com.lfy.kcat.user.controller;


import com.lfy.kcat.user.business.VerifyCodeBizService;
import com.lfy.kcat.user.constant.BizConst;
import com.lfy.kcat.user.vo.SendCodeReqVo;
import com.lfy.kcat.user.vo.SendCodeRespVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class VerifyCodeController {

    @Autowired
    VerifyCodeBizService verifyCodeBizService;

    /**
     * 发送验证码
     * @param sendCodeReqVo
     * @return
     */
    @PostMapping("/send-code")
    public R sendCode(@RequestBody SendCodeReqVo sendCodeReqVo){

        verifyCodeBizService.sendCode(sendCodeReqVo.getPhone());
        SendCodeRespVo sendCodeRespVo = new SendCodeRespVo();
        sendCodeRespVo.setPhone(sendCodeReqVo.getPhone());
        sendCodeRespVo.setExpires(BizConst.DEFAULT_CODE_EXPIRES);
        log.info("短信发送成功");
        return R.ok("短信发送成功",sendCodeRespVo);
    }
}


