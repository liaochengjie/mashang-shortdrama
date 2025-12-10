package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 短剧管理对象 dramas
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dramas")
public class Dramas extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 短剧ID
     */
    @TableId(value = "drama_id")
    private Long dramaId;

    /**
     * 短剧标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 封面图URL
     */
    private String cover;

    /**
     * 海报图URL
     */
    private String poster;

    /**
     * 预告片URL
     */
    private String trailerUrl;

    /**
     * 预告片信息流URL
     */
    private String trailerInfoflowUrl;

    /**
     * 简介
     */
    private String description;

    /**
     * 剧情介绍
     */
    private String storyLine;

    /**
     * 导演，多个用逗号分隔
     */
    private String director;

    /**
     * 编剧，多个用逗号分隔
     */
    private String screenwriter;

    /**
     * 制作公司
     */
    private String productionCompany;

    /**
     * 发布日期
     */
    private Date releaseDate;

    /**
     * 总集数
     */
    private Long totalEpisodes;

    /**
     * 当前集数
     */
    private Long currentEpisodes;

    /**
     * 平均每集时长(秒)
     */
    private Long episodeDuration;

    /**
     * 总时长(秒)
     */
    private Long totalDuration;

    /**
     * 语言
     */
    private String language;

    /**
     * 地区
     */
    private String region;

    /**
     * 年份
     */
    private Long year;

    /**
     * 是否完结：0连载中，1已完结
     */
    private Long isFinished;

    /**
     * 是否VIP专享：0否，1是
     */
    private Long isVip;

    /**
     * 是否新剧：0否，1是
     */
    private Long isNew;

    /**
     * 是否热播：0否，1是
     */
    private Long isHot;

    /**
     * 是否推荐：0否，1是
     */
    private Long isRecommended;

    /**
     * 画质：SD/HD/FHD/4K
     */
    private String quality;

    /**
     * 年龄分级：G/PG/PG-13/R
     */
    private String ageRating;

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
     * 收藏数
     */
    private Long collectionCount;

    /**
     * 追剧数
     */
    private Long followCount;

    /**
     * 评分
     */
    private Long ratingScore;

    /**
     * 评分人数
     */
    private Long ratingCount;

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

    /**
     * 排序顺序
     */
    private Long sortOrder;


}
