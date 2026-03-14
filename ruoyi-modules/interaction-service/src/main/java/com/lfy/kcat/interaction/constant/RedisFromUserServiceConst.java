package com.lfy.kcat.interaction.constant;

import lombok.Data;

@Data
public class RedisFromUserServiceConst {
    public static final String POHNE_CODE_KEY="phone:code:";

    public static final String HOME_FEATURE_KEY="home:feature:";
    //默认缓存时间3天
    public static final Long DEFAULT_TIMEOUT=60*60*24*3L;

    public static final String NULL_VALUE="x";

    public static final Long NULL_VALUE_TIMEOUT=60*30L;

    public static final String DRAMA_BF="drama:bf";

    public static final String HOME_DRAMA_EPISODES_KEY="home:drama:episodes:";

    public static final String Home_DRAMA_LOCK="lock:home:drama:";

    public static final String HOME_DRAMA_INFO="home:drama:info:";
}
