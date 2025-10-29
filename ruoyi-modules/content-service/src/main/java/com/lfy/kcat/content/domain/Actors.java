package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 演员管理对象 actors
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("actors")
public class Actors extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 演员ID
     */
    @TableId(value = "actor_id")
    private Long actorId;

    /**
     * 演员名字
     */
    private String actorName;

    /**
     * 头像图
     */
    private String actorImg;

    /**
     * 演员介绍
     */
    private String actorInfo;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 身高(cm)
     */
    private Long height;

    /**
     * 体重(kg)
     */
    private Long weight;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 参演剧数量
     */
    private Long dramaCount;

    /**
     * 粉丝数量
     */
    private Long fansCount;

    /**
     * 是否热门演员
     */
    private Long isHot;

    /**
     * 状态：0禁用，1正常
     */
    private Long status;


}
