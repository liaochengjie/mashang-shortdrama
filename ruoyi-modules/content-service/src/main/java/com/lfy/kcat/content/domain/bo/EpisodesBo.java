package com.lfy.kcat.content.domain.bo;

import com.lfy.kcat.content.domain.Episodes;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 剧集管理业务对象 episodes
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Episodes.class, reverseConvertGenerate = false)
public class EpisodesBo extends BaseEntity {

    /**
     * 剧集ID
     */
    @NotNull(message = "剧集ID不能为空", groups = { EditGroup.class })
    private Long episodeId;

    /**
     * 短剧ID
     */
    @NotNull(message = "短剧ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dramaId;

    /**
     * 剧集标题
     */
    @NotBlank(message = "剧集标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 集数
     */
    @NotNull(message = "集数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long episodeNumber;

    /**
     * 剧集封面URL
     */
    private String cover;

    /**
     * 时长(秒)
     */
    @NotNull(message = "时长(秒)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long duration;

    /**
     * 剧集简介
     */
    private String description;

    /**
     * 视频URL
     */
    @NotBlank(message = "视频URL不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 字幕文件URL
     */
    private String subtitleUrl;

    /**
     * 是否免费：0收费，1免费
     */
    @NotNull(message = "是否免费：0收费，1免费不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isFree;

    /**
     * 是否预告片：0否，1是
     */
    @NotNull(message = "是否预告片：0否，1是不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isTrailer;

    /**
     * 金币价格
     */
    @NotNull(message = "金币价格不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long coinPrice;

    /**
     * 播放次数
     */
    @NotNull(message = "播放次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long playCount;

    /**
     * 点赞数
     */
    @NotNull(message = "点赞数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long likeCount;

    /**
     * 评论数
     */
    @NotNull(message = "评论数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long commentCount;

    /**
     * 分享数
     */
    @NotNull(message = "分享数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long shareCount;

    /**
     * 弹幕数
     */
    @NotNull(message = "弹幕数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long danmakuCount;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 状态：0下架，1上架，2审核中
     */
    @NotNull(message = "状态：0下架，1上架，2审核中不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 审核状态：0待审核，1通过，2拒绝
     */
    @NotNull(message = "审核状态：0待审核，1通过，2拒绝不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long auditStatus;

    /**
     * 审核拒绝原因
     */
    private String auditReason;


}
