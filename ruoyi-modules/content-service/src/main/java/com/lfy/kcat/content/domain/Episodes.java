package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 剧集管理对象 episodes
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("episodes")
public class Episodes extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 剧集ID
     */
    @TableId(value = "episode_id")
    private Long episodeId;

    /**
     * 短剧ID
     */
    private Long dramaId;

    /**
     * 剧集标题
     */
    private String title;

    /**
     * 集数
     */
    private Long episodeNumber;

    /**
     * 剧集封面URL
     */
    private String cover;

    /**
     * 时长(秒)
     */
    private Long duration;

    /**
     * 剧集简介
     */
    private String description;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 高清视频URL
     */
    private String videoUrlHd;

    /**
     * 标清视频URL
     */
    private String videoUrlSd;

    /**
     * 低清视频URL
     */
    private String videoUrlLow;

    /**
     * 字幕文件URL
     */
    private String subtitleUrl;

    /**
     * 是否免费：0收费，1免费
     */
    private Long isFree;

    /**
     * 是否预告片：0否，1是
     */
    private Long isTrailer;

    /**
     * 金币价格
     */
    private Long coinPrice;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 分享数
     */
    private Long shareCount;

    /**
     * 弹幕数
     */
    private Long danmakuCount;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 状态：0下架，1上架，2审核中
     */
    private Long status;

    /**
     * 审核状态：0待审核，1通过，2拒绝
     */
    private Long auditStatus;

    /**
     * 审核拒绝原因
     */
    private String auditReason;


}
