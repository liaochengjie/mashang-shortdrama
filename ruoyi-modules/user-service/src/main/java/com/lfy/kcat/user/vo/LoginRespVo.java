package com.lfy.kcat.user.vo;

import lombok.Data;

@Data
public class LoginRespVo {
    private String token;
    private String expires;
}
