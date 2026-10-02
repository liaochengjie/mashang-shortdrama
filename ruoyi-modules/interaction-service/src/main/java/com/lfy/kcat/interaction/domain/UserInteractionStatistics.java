package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户互动统计表
 * @TableName user_interaction_statistics
 */
@TableName(value ="user_interaction_statistics")
@Data
public class UserInteractionStatistics {
    /**
     * 统计ID
     */
    @TableId(type = IdType.AUTO)
    private Long statId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论总数
     */
    private Integer commentCount;

    /**
     * 弹幕总数
     */
    private Integer danmakuCount;

    /**
     * 点赞总数
     */
    private Integer likeCount;

    /**
     * 点踩总数
     */
    private Integer dislikeCount;

    /**
     * 分享总数
     */
    private Integer shareCount;

    /**
     * 举报总数
     */
    private Integer reportCount;

    /**
     * 收到的点赞数
     */
    private Integer receivedLikeCount;

    /**
     * 收到的点踩数
     */
    private Integer receivedDislikeCount;

    /**
     * 收到的评论数
     */
    private Integer receivedCommentCount;

    /**
     * 被举报次数
     */
    private Integer receivedReportCount;

    /**
     * 最后评论时间
     */
    private Date lastCommentTime;

    /**
     * 最后弹幕时间
     */
    private Date lastDanmakuTime;

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
        UserInteractionStatistics other = (UserInteractionStatistics) that;
        return (this.getStatId() == null ? other.getStatId() == null : this.getStatId().equals(other.getStatId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCommentCount() == null ? other.getCommentCount() == null : this.getCommentCount().equals(other.getCommentCount()))
            && (this.getDanmakuCount() == null ? other.getDanmakuCount() == null : this.getDanmakuCount().equals(other.getDanmakuCount()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getDislikeCount() == null ? other.getDislikeCount() == null : this.getDislikeCount().equals(other.getDislikeCount()))
            && (this.getShareCount() == null ? other.getShareCount() == null : this.getShareCount().equals(other.getShareCount()))
            && (this.getReportCount() == null ? other.getReportCount() == null : this.getReportCount().equals(other.getReportCount()))
            && (this.getReceivedLikeCount() == null ? other.getReceivedLikeCount() == null : this.getReceivedLikeCount().equals(other.getReceivedLikeCount()))
            && (this.getReceivedDislikeCount() == null ? other.getReceivedDislikeCount() == null : this.getReceivedDislikeCount().equals(other.getReceivedDislikeCount()))
            && (this.getReceivedCommentCount() == null ? other.getReceivedCommentCount() == null : this.getReceivedCommentCount().equals(other.getReceivedCommentCount()))
            && (this.getReceivedReportCount() == null ? other.getReceivedReportCount() == null : this.getReceivedReportCount().equals(other.getReceivedReportCount()))
            && (this.getLastCommentTime() == null ? other.getLastCommentTime() == null : this.getLastCommentTime().equals(other.getLastCommentTime()))
            && (this.getLastDanmakuTime() == null ? other.getLastDanmakuTime() == null : this.getLastDanmakuTime().equals(other.getLastDanmakuTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStatId() == null) ? 0 : getStatId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCommentCount() == null) ? 0 : getCommentCount().hashCode());
        result = prime * result + ((getDanmakuCount() == null) ? 0 : getDanmakuCount().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getDislikeCount() == null) ? 0 : getDislikeCount().hashCode());
        result = prime * result + ((getShareCount() == null) ? 0 : getShareCount().hashCode());
        result = prime * result + ((getReportCount() == null) ? 0 : getReportCount().hashCode());
        result = prime * result + ((getReceivedLikeCount() == null) ? 0 : getReceivedLikeCount().hashCode());
        result = prime * result + ((getReceivedDislikeCount() == null) ? 0 : getReceivedDislikeCount().hashCode());
        result = prime * result + ((getReceivedCommentCount() == null) ? 0 : getReceivedCommentCount().hashCode());
        result = prime * result + ((getReceivedReportCount() == null) ? 0 : getReceivedReportCount().hashCode());
        result = prime * result + ((getLastCommentTime() == null) ? 0 : getLastCommentTime().hashCode());
        result = prime * result + ((getLastDanmakuTime() == null) ? 0 : getLastDanmakuTime().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", commentCount=").append(commentCount);
        sb.append(", danmakuCount=").append(danmakuCount);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", dislikeCount=").append(dislikeCount);
        sb.append(", shareCount=").append(shareCount);
        sb.append(", reportCount=").append(reportCount);
        sb.append(", receivedLikeCount=").append(receivedLikeCount);
        sb.append(", receivedDislikeCount=").append(receivedDislikeCount);
        sb.append(", receivedCommentCount=").append(receivedCommentCount);
        sb.append(", receivedReportCount=").append(receivedReportCount);
        sb.append(", lastCommentTime=").append(lastCommentTime);
        sb.append(", lastDanmakuTime=").append(lastDanmakuTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}