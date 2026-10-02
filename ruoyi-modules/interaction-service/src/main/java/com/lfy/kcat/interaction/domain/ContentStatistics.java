package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 内容统计表
 * @TableName content_statistics
 */
@TableName(value ="content_statistics")
@Data
public class ContentStatistics {
    /**
     * 统计ID
     */
    @TableId(type = IdType.AUTO)
    private Long statId;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容类型：1短剧，2剧集
     */
    private Integer contentType;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 弹幕数
     */
    private Integer danmakuCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 点踩数
     */
    private Integer dislikeCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 举报数
     */
    private Integer reportCount;

    /**
     * 热度分数
     */
    private BigDecimal hotScore;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ContentStatistics other = (ContentStatistics) that;
        return (this.getStatId() == null ? other.getStatId() == null : this.getStatId().equals(other.getStatId()))
            && (this.getContentId() == null ? other.getContentId() == null : this.getContentId().equals(other.getContentId()))
            && (this.getContentType() == null ? other.getContentType() == null : this.getContentType().equals(other.getContentType()))
            && (this.getCommentCount() == null ? other.getCommentCount() == null : this.getCommentCount().equals(other.getCommentCount()))
            && (this.getDanmakuCount() == null ? other.getDanmakuCount() == null : this.getDanmakuCount().equals(other.getDanmakuCount()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getDislikeCount() == null ? other.getDislikeCount() == null : this.getDislikeCount().equals(other.getDislikeCount()))
            && (this.getShareCount() == null ? other.getShareCount() == null : this.getShareCount().equals(other.getShareCount()))
            && (this.getReportCount() == null ? other.getReportCount() == null : this.getReportCount().equals(other.getReportCount()))
            && (this.getHotScore() == null ? other.getHotScore() == null : this.getHotScore().equals(other.getHotScore()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStatId() == null) ? 0 : getStatId().hashCode());
        result = prime * result + ((getContentId() == null) ? 0 : getContentId().hashCode());
        result = prime * result + ((getContentType() == null) ? 0 : getContentType().hashCode());
        result = prime * result + ((getCommentCount() == null) ? 0 : getCommentCount().hashCode());
        result = prime * result + ((getDanmakuCount() == null) ? 0 : getDanmakuCount().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getDislikeCount() == null) ? 0 : getDislikeCount().hashCode());
        result = prime * result + ((getShareCount() == null) ? 0 : getShareCount().hashCode());
        result = prime * result + ((getReportCount() == null) ? 0 : getReportCount().hashCode());
        result = prime * result + ((getHotScore() == null) ? 0 : getHotScore().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", statId=").append(statId);
        sb.append(", contentId=").append(contentId);
        sb.append(", contentType=").append(contentType);
        sb.append(", commentCount=").append(commentCount);
        sb.append(", danmakuCount=").append(danmakuCount);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", dislikeCount=").append(dislikeCount);
        sb.append(", shareCount=").append(shareCount);
        sb.append(", reportCount=").append(reportCount);
        sb.append(", hotScore=").append(hotScore);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}