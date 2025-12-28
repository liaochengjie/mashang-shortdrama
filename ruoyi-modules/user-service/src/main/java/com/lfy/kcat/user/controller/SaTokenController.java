package com.lfy.kcat.user.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class SaTokenController {

    @GetMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password){
        if("lcj".equals(username)&&"123456".equals(password)){

            SaLoginParameter saLoginParameter = new SaLoginParameter();
            saLoginParameter.setExtra("username",username);
            saLoginParameter.setExtra("age",18);
            StpUtil.login("123456",saLoginParameter);
            return "登录成功,id为123456";
        }
        return "登录失败，不存在此人";
    }

    @GetMapping("/Logout")
    public String doLogout(){
        StpUtil.logout();

        return "用户已推出";
    }

    @GetMapping("/isLogin")
    public String isLogin(){
        return "当前会话是否登录"+StpUtil.isLogin();
    }

    @GetMapping("/getInfo")
    public String getInfo(){
        String tokenName = StpUtil.getTokenName();
        String tokenValue = StpUtil.getTokenValue();
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return "tokenName:"+tokenName+"\n tokenValue:"+tokenValue;
    }
}
