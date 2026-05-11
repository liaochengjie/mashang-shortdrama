package com.lfy.kcat.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lfy.kcat.user.business.UserAuthBizService;
import com.lfy.kcat.user.vo.LoginReqVo;
import com.lfy.kcat.user.vo.LoginRespVo;
import com.lfy.kcat.user.vo.UserInfoRespVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserLoginController {

    @Autowired
    UserAuthBizService userAuthBizService;

    /**
     * 用户登录
     * @param loginReqVo
     * @return
     */
    @PostMapping("/login")
    public R userLogin(@RequestBody LoginReqVo loginReqVo){
        log.info("现在开始进行用户登录，手机号为:{}",loginReqVo.getPhone());
        String phone = loginReqVo.getPhone();
        String code = loginReqVo.getCode();
        LoginRespVo loginRespVo=userAuthBizService.login(phone,code);

        return R.ok("登录成功",loginRespVo);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/userinfo")
    public R userInfo(){
        //前端自己带令牌放到请求头中，SaToken 只需要从 Authorization 头字段中拿到令牌
        //删除 Bearer 前缀，得到原始令牌，Sa-Token 拿着原始令牌去redis中反查用户
        //配置令牌的名字 和 前缀和前端对应起来
        long loginId = StpUtil.getLoginIdAsLong();
        log.info("正在进行用户信息查询,用户id={}",loginId);
        UserInfoRespVo vo=userAuthBizService.getUserInfo(loginId);
        return R.ok("获取用户信息成功",vo);
    }
}
