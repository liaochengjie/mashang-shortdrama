package com.lfy.kcat.user.business;

import com.lfy.kcat.user.domain.Users;
import com.lfy.kcat.user.vo.LoginRespVo;
import com.lfy.kcat.user.vo.UserInfoRespVo;

public interface UserAuthBizService {
    UserInfoRespVo getUserInfo(long loginId);

    LoginRespVo login(String phone, String code);

    Users register(String phone);
}
