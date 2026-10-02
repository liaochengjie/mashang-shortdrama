package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName comments
 */
@TableName(value ="comments")
@Data
public class Comments {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long commentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称(冗余)
     */
    private String userNickname;

    /**
     * 用户头像(冗余)
     */
    private String userAvatar;

    /**
     * 短剧ID
     */
    private Long dramaId;

    /**
     * 剧集ID，可为空表示评论整部剧
     */
    private Long episodeId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID，回复评论时使用
     */
    private Long parentId;

    /**
     * 根评论ID
     */
    private Long rootId;

    /**
     * 回复用户ID
     */
    private Long replyUserId;

    /**
     * 回复用户昵称
     */
    private String replyUserNickname;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 点踩数
     */
    private Integer dislikeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 是否热门评论
     */
    private Integer isHot;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 状态：0删除，1正常，2审核中，3已屏蔽
     */
    private Integer status;

    /**
     * 审核状态：0待审核，1通过，2拒绝
     */
    private Integer auditStatus;

    /**
     * 审核拒绝原因
     */
    private String auditReason;

    /**
     * 创建时间
     */
    private Date createTime;

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
        Comments other = (Comments) that;
        return (this.getCommentId() == null ? other.getCommentId() == null : this.getCommentId().equals(other.getCommentId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserNickname() == null ? other.getUserNickname() == null : this.getUserNickname().equals(other.getUserNickname()))
            && (this.getUserAvatar() == null ? other.getUserAvatar() == null : this.getUserAvatar().equals(other.getUserAvatar()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getEpisodeId() == null ? other.getEpisodeId() == null : this.getEpisodeId().equals(other.getEpisodeId()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getRootId() == null ? other.getRootId() == null : this.getRootId().equals(other.getRootId()))
            && (this.getReplyUserId() == null ? other.getReplyUserId() == null : this.getReplyUserId().equals(other.getReplyUserId()))
            && (this.getReplyUserNickname() == null ? other.getReplyUserNickname() == null : this.getReplyUserNickname().equals(other.getReplyUserNickname()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getDislikeCount() == null ? other.getDislikeCount() == null : this.getDislikeCount().equals(other.getDislikeCount()))
            && (this.getReplyCount() == null ? other.getReplyCount() == null : this.getReplyCount().equals(other.getReplyCount()))
            && (this.getIsHot() == null ? other.getIsHot() == null : this.getIsHot().equals(other.getIsHot()))
            && (this.getIsTop() == null ? other.getIsTop() == null : this.getIsTop().equals(other.getIsTop()))
            && (this.getIpAddress() == null ? other.getIpAddress() == null : this.getIpAddress().equals(other.getIpAddress()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getDeviceInfo() == null ? other.getDeviceInfo() == null : this.getDeviceInfo().equals(other.getDeviceInfo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getAuditReason() == null ? other.getAuditReason() == null : this.getAuditReason().equals(other.getAuditReason()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCommentId() == null) ? 0 : getCommentId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserNickname() == null) ? 0 : getUserNickname().hashCode());
        result = prime * result + ((getUserAvatar() == null) ? 0 : getUserAvatar().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getEpisodeId() == null) ? 0 : getEpisodeId().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getRootId() == null) ? 0 : getRootId().hashCode());
        result = prime * result + ((getReplyUserId() == null) ? 0 : getReplyUserId().hashCode());
        result = prime * result + ((getReplyUserNickname() == null) ? 0 : getReplyUserNickname().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getDislikeCount() == null) ? 0 : getDislikeCount().hashCode());
        result = prime * result + ((getReplyCount() == null) ? 0 : getReplyCount().hashCode());
        result = prime * result + ((getIsHot() == null) ? 0 : getIsHot().hashCode());
        result = prime * result + ((getIsTop() == null) ? 0 : getIsTop().hashCode());
        result = prime * result + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getDeviceInfo() == null) ? 0 : getDeviceInfo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getAuditReason() == null) ? 0 : getAuditReason().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", commentId=").append(commentId);
        sb.append(", userId=").append(userId);
        sb.append(", userNickname=").append(userNickname);
        sb.append(", userAvatar=").append(userAvatar);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", episodeId=").append(episodeId);
        sb.append(", content=").append(content);
        sb.append(", parentId=").append(parentId);
        sb.append(", rootId=").append(rootId);
        sb.append(", replyUserId=").append(replyUserId);
        sb.append(", replyUserNickname=").append(replyUserNickname);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", dislikeCount=").append(dislikeCount);
        sb.append(", replyCount=").append(replyCount);
        sb.append(", isHot=").append(isHot);
        sb.append(", isTop=").append(isTop);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", location=").append(location);
        sb.append(", deviceInfo=").append(deviceInfo);
        sb.append(", status=").append(status);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", auditReason=").append(auditReason);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}