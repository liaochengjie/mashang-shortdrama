package com.lfy.kcat.user.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfoRespVo {
    //用户基本信息
    private UserBaseInfo baseInfo;
    //关注数量
    private Long followCount;
    //粉丝数量
    private Long fansCount;
    //在追数量
    private Long followingCount;
    //历史数量
    private Long historyCount;



    @Data
    public static class UserBaseInfo{
        //头像地址
        private String avatar;
        //昵称
        private String nickname;
        //电话
        private String phone;
        //出生日期
        private Date birthday;
        //个人签名
        private String signature;


    }
}
