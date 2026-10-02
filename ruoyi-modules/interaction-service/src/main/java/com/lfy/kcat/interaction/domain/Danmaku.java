package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 弹幕表
 * @TableName danmaku
 */
@TableName(value ="danmaku")
@Data
public class Danmaku {
    /**
     * 弹幕ID
     */
    @TableId(type = IdType.AUTO)
    private Long danmakuId;

    /**
     * 剧集ID
     */
    private Long episodeId;

    /**
     * 短剧ID(冗余)
     */
    private Long dramaId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称(冗余)
     */
    private String userNickname;

    /**
     * 弹幕内容
     */
    private String content;

    /**
     * 视频时间点(秒)
     */
    private Double timePoint;

    /**
     * 弹幕颜色
     */
    private String color;

    /**
     * 弹幕位置：0滚动，1顶部，2底部
     */
    private Integer position;

    /**
     * 字体大小：0正常，1小，2大
     */
    private Integer fontSize;

    /**
     * 弹幕速度：0正常，1慢，2快
     */
    private Integer speed;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 点踩数
     */
    private Integer dislikeCount;

    /**
     * 举报数
     */
    private Integer reportCount;

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
        Danmaku other = (Danmaku) that;
        return (this.getDanmakuId() == null ? other.getDanmakuId() == null : this.getDanmakuId().equals(other.getDanmakuId()))
            && (this.getEpisodeId() == null ? other.getEpisodeId() == null : this.getEpisodeId().equals(other.getEpisodeId()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserNickname() == null ? other.getUserNickname() == null : this.getUserNickname().equals(other.getUserNickname()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getTimePoint() == null ? other.getTimePoint() == null : this.getTimePoint().equals(other.getTimePoint()))
            && (this.getColor() == null ? other.getColor() == null : this.getColor().equals(other.getColor()))
            && (this.getPosition() == null ? other.getPosition() == null : this.getPosition().equals(other.getPosition()))
            && (this.getFontSize() == null ? other.getFontSize() == null : this.getFontSize().equals(other.getFontSize()))
            && (this.getSpeed() == null ? other.getSpeed() == null : this.getSpeed().equals(other.getSpeed()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getDislikeCount() == null ? other.getDislikeCount() == null : this.getDislikeCount().equals(other.getDislikeCount()))
            && (this.getReportCount() == null ? other.getReportCount() == null : this.getReportCount().equals(other.getReportCount()))
            && (this.getIpAddress() == null ? other.getIpAddress() == null : this.getIpAddress().equals(other.getIpAddress()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getDeviceInfo() == null ? other.getDeviceInfo() == null : this.getDeviceInfo().equals(other.getDeviceInfo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getAuditReason() == null ? other.getAuditReason() == null : this.getAuditReason().equals(other.getAuditReason()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDanmakuId() == null) ? 0 : getDanmakuId().hashCode());
        result = prime * result + ((getEpisodeId() == null) ? 0 : getEpisodeId().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserNickname() == null) ? 0 : getUserNickname().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getTimePoint() == null) ? 0 : getTimePoint().hashCode());
        result = prime * result + ((getColor() == null) ? 0 : getColor().hashCode());
        result = prime * result + ((getPosition() == null) ? 0 : getPosition().hashCode());
        result = prime * result + ((getFontSize() == null) ? 0 : getFontSize().hashCode());
        result = prime * result + ((getSpeed() == null) ? 0 : getSpeed().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getDislikeCount() == null) ? 0 : getDislikeCount().hashCode());
        result = prime * result + ((getReportCount() == null) ? 0 : getReportCount().hashCode());
        result = prime * result + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getDeviceInfo() == null) ? 0 : getDeviceInfo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getAuditReason() == null) ? 0 : getAuditReason().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", danmakuId=").append(danmakuId);
        sb.append(", episodeId=").append(episodeId);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", userId=").append(userId);
        sb.append(", userNickname=").append(userNickname);
        sb.append(", content=").append(content);
        sb.append(", timePoint=").append(timePoint);
        sb.append(", color=").append(color);
        sb.append(", position=").append(position);
        sb.append(", fontSize=").append(fontSize);
        sb.append(", speed=").append(speed);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", dislikeCount=").append(dislikeCount);
        sb.append(", reportCount=").append(reportCount);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", location=").append(location);
        sb.append(", deviceInfo=").append(deviceInfo);
        sb.append(", status=").append(status);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", auditReason=").append(auditReason);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}