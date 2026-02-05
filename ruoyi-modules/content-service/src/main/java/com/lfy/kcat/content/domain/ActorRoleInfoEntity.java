package com.lfy.kcat.content.domain;

import lombok.Data;

@Data
public class ActorRoleInfoEntity {
    //演员ID
    private String actorId;
    // 演员名称
    private String actorName;
    // 演员头像URL
    private String avatar;
    // 角色名称
    private String roleName;
    // 角色类型
    private String roleType;
}
