package com.lfy.kcat.user.business.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lfy.kcat.user.business.UserAuthBizService;
import com.lfy.kcat.user.business.VerifyCodeBizService;
import com.lfy.kcat.user.domain.UserBrowseHistory;
import com.lfy.kcat.user.domain.UserCollections;
import com.lfy.kcat.user.domain.UserFollows;
import com.lfy.kcat.user.domain.Users;
import com.lfy.kcat.user.exception.UserServiceExceptionEnume;
import com.lfy.kcat.user.service.UserBrowseHistoryService;
import com.lfy.kcat.user.service.UserCollectionsService;
import com.lfy.kcat.user.service.UserFollowsService;
import com.lfy.kcat.user.service.UsersService;
import com.lfy.kcat.user.vo.LoginRespVo;
import com.lfy.kcat.user.vo.UserInfoRespVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserAuthBizServiceImpl implements UserAuthBizService {


    @Autowired
    VerifyCodeBizService verifyCodeBizService;

    @Autowired
    UsersService usersService;

    @Autowired
    UserFollowsService userFollowsService;;

    @Autowired
    UserBrowseHistoryService userBrowseHistoryService;

    @Autowired
    UserCollectionsService userCollectionsService;
    @Override
    public UserInfoRespVo getUserInfo(long loginId) {
        //1：根据loginId查询用户信息
        LambdaQueryWrapper<Users> wrapper1 = Wrappers.lambdaQuery(Users.class)
            .eq(Users::getUserId, loginId);
        Users users = usersService.getOne(wrapper1);
        //如果user为空，不存在该用户
        if(users==null){
            log.info("用户不存在，loginId为:{}",loginId);
            return null;
        }
        //2：如果user不为空，存在该用户，将用户信息封装到UserInfoRespVo中
        String avatar = users.getAvatar();
        String nickname = users.getNickname();
        String phone = users.getPhone();
        Date birthday = users.getBirthday();
        String signature = users.getSignature();
        UserInfoRespVo.UserBaseInfo baseInfo = new UserInfoRespVo.UserBaseInfo();
        baseInfo.setAvatar(avatar);
        baseInfo.setNickname(nickname);
        baseInfo.setPhone(phone);
        baseInfo.setBirthday(birthday);
        baseInfo.setSignature(signature);
        //3：将baseInfo封装到UserInfoRespVo中
        UserInfoRespVo userInfoRespVo = new UserInfoRespVo();
        userInfoRespVo.setBaseInfo(baseInfo);

        //4：查询关注数量
        LambdaQueryWrapper<UserFollows> eq = Wrappers.lambdaQuery(UserFollows.class)
            .eq(UserFollows::getFollowerId, loginId);
        long followCount = userFollowsService.count(eq);
        //5：查询粉丝数量
        LambdaQueryWrapper<UserFollows> eq1 = Wrappers.lambdaQuery(UserFollows.class)
            .eq(UserFollows::getFolloweeId, loginId);
        long fansCount = userFollowsService.count(eq1);

        //6.将粉丝数量和关注数量封装
        userInfoRespVo.setFollowCount(followCount);
        userInfoRespVo.setFansCount(fansCount);

        LambdaQueryWrapper<UserBrowseHistory> eq2 = Wrappers.lambdaQuery(UserBrowseHistory.class)
            .eq(UserBrowseHistory::getUserId, loginId);
        long historyCount = userBrowseHistoryService.count(eq2);
        //7.将历史数量封装
        userInfoRespVo.setHistoryCount(historyCount);

        //8.将收藏数量封装
        LambdaQueryWrapper<UserCollections> eq3 = Wrappers.lambdaQuery(UserCollections.class)
            .eq(UserCollections::getUserId, loginId);
        long collectionCount = userCollectionsService.count(eq3);
        userInfoRespVo.setFollowingCount(collectionCount);

        return userInfoRespVo;
    }

    @Override
    public LoginRespVo login(String phone, String code) {
        //1：对比redis中该号码存的验证码
        boolean codeAuth = verifyCodeBizService.codeAuth(phone, code);

        //2：如果校验失败，抛出验证码错误异常
        if(!codeAuth){
            UserServiceExceptionEnume verifyCodeError = UserServiceExceptionEnume.VERIFY_CODE_ERROR;
            throw new ServiceException(verifyCodeError.getMessage(),verifyCodeError.getCode());
        }
        //2：如果校验成功，进行用户查询
        LambdaQueryWrapper<Users> wrapper = Wrappers.lambdaQuery(Users.class)
            .eq(Users::getPhone, phone);
        Users user = usersService.getOne(wrapper);
        //如果user为空，不存在该用户
        if(user==null){
            log.info("用户不存在，进行用户的创建,手机号为:{}",phone);
            //不存在则进行注册新用户
            user = register(phone);
        }
        //3：进行登录
        SaLoginParameter parameter = new SaLoginParameter();
        parameter.setExtra("phone",user.getPhone());
        parameter.setExtra("nickname",user.getNickname());
        StpUtil.login(user.getUserId(),parameter);

        //获取令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        String tokenName = tokenInfo.getTokenName();
        String tokenValue = tokenInfo.getTokenValue();
        long tokenTimeout = tokenInfo.getTokenTimeout();
        LoginRespVo loginRespVo = new LoginRespVo();
        loginRespVo.setToken(tokenValue);
        loginRespVo.setExpires(String.valueOf(tokenTimeout));
        return loginRespVo;
    }

    /**
     * 用户注册
     * @return
     */
    @Override
    public Users register(String phone){
        Users user = new Users();
        String nickName="游客"+ RandomUtil.randomNumbers(8);

        user.setPhone(phone);
        user.setNickname(nickName);
        usersService.save(user);
        return user;
    }
}
