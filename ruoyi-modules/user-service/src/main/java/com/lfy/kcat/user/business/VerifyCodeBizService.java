package com.lfy.kcat.user.business;


public interface VerifyCodeBizService {


    /**
     * 发送验证码
     * @param phone
     */
    void sendCode(String phone);

    /**
     * 校验验证码
     *
     * @return
     */
    boolean codeAuth(String phone, String code);
}
