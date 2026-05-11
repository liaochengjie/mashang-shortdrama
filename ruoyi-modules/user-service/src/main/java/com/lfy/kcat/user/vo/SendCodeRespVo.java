package com.lfy.kcat.user.vo;

import lombok.Data;

@Data
public class SendCodeRespVo {
    private String phone;
    // 验证码过期时间，单位秒
    private int expires;
}
