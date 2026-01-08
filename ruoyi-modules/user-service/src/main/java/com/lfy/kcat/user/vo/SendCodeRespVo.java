package com.lfy.kcat.user.vo;

import lombok.Data;

@Data
public class SendCodeRespVo {
    private String phone;
    private int expires;
}
