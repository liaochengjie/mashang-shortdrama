package com.lfy.kcat.content.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lfy.kcat.content.domain.Episodes;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 剧集管理视图对象 episodes
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Episodes.class)
public class EpisodesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 剧集ID
     */
    @ExcelProperty(value = "剧集ID")
    private Long episodeId;

    /**
     * 短剧ID
     */
    @ExcelProperty(value = "短剧ID")
    private Long dramaId;

    /**
     * 剧集标题
     */
    @ExcelProperty(value = "剧集标题")
    private String title;

    /**
     * 集数
     */
    @ExcelProperty(value = "集数")
    private Long episodeNumber;

    /**
     * 剧集封面URL
     */
    @ExcelProperty(value = "剧集封面URL")
    private String cover;

    /**
     * 时长(秒)
     */
    @ExcelProperty(value = "时长(秒)")
    private Long duration;

    /**
     * 剧集简介
     */
    @ExcelProperty(value = "剧集简介")
    private String description;

    /**
     * 视频URL
     */
    @ExcelProperty(value = "视频URL")
    private String videoUrl;

    /**
     * 高清视频URL
     */
    @ExcelProperty(value = "高清视频URL")
    private String videoUrlHd;

    /**
     * 标清视频URL
     */
    @ExcelProperty(value = "标清视频URL")
    private String videoUrlSd;

    /**
     * 字幕文件URL
     */
    @ExcelProperty(value = "字幕文件URL")
    private String subtitleUrl;

    /**
     * 是否免费：0收费，1免费
     */
    @ExcelProperty(value = "是否免费：0收费，1免费")
    private Long isFree;

    /**
     * 是否预告片：0否，1是
     */
    @ExcelProperty(value = "是否预告片：0否，1是")
    private Long isTrailer;

    /**
     * 金币价格
     */
    @ExcelProperty(value = "金币价格")
    private Long coinPrice;

    /**
     * 播放次数
     */
    @ExcelProperty(value = "播放次数")
    private Long playCount;

    /**
     * 点赞数
     */
    @ExcelProperty(value = "点赞数")
    private Long likeCount;

    /**
     * 评论数
     */
    @ExcelProperty(value = "评论数")
    private Long commentCount;

    /**
     * 分享数
     */
    @ExcelProperty(value = "分享数")
    private Long shareCount;

    /**
     * 弹幕数
     */
    @ExcelProperty(value = "弹幕数")
    private Long danmakuCount;

    /**
     * 发布时间
     */
    @ExcelProperty(value = "发布时间")
    private Date publishTime;

    /**
     * 状态：0下架，1上架，2审核中
     */
    @ExcelProperty(value = "状态：0下架，1上架，2审核中")
    private Long status;

    /**
     * 审核状态：0待审核，1通过，2拒绝
     */
    @ExcelProperty(value = "审核状态：0待审核，1通过，2拒绝")
    private Long auditStatus;

    /**
     * 审核拒绝原因
     */
    @ExcelProperty(value = "审核拒绝原因")
    private String auditReason;


}
