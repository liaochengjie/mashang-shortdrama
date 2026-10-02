package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 分享记录表
 * @TableName share_records
 */
@TableName(value ="share_records")
@Data
public class ShareRecords {
    /**
     * 分享ID
     */
    @TableId(type = IdType.AUTO)
    private Long shareId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 短剧ID
     */
    private Long dramaId;

    /**
     * 剧集ID
     */
    private Long episodeId;

    /**
     * 分享类型：1微信好友，2微信朋友圈，3QQ，4微博，5复制链接
     */
    private Integer shareType;

    /**
     * 分享平台
     */
    private String sharePlatform;

    /**
     * 分享内容
     */
    private String shareContent;

    /**
     * 分享链接
     */
    private String shareUrl;

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
        ShareRecords other = (ShareRecords) that;
        return (this.getShareId() == null ? other.getShareId() == null : this.getShareId().equals(other.getShareId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getEpisodeId() == null ? other.getEpisodeId() == null : this.getEpisodeId().equals(other.getEpisodeId()))
            && (this.getShareType() == null ? other.getShareType() == null : this.getShareType().equals(other.getShareType()))
            && (this.getSharePlatform() == null ? other.getSharePlatform() == null : this.getSharePlatform().equals(other.getSharePlatform()))
            && (this.getShareContent() == null ? other.getShareContent() == null : this.getShareContent().equals(other.getShareContent()))
            && (this.getShareUrl() == null ? other.getShareUrl() == null : this.getShareUrl().equals(other.getShareUrl()))
            && (this.getIpAddress() == null ? other.getIpAddress() == null : this.getIpAddress().equals(other.getIpAddress()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getDeviceInfo() == null ? other.getDeviceInfo() == null : this.getDeviceInfo().equals(other.getDeviceInfo()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShareId() == null) ? 0 : getShareId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getEpisodeId() == null) ? 0 : getEpisodeId().hashCode());
        result = prime * result + ((getShareType() == null) ? 0 : getShareType().hashCode());
        result = prime * result + ((getSharePlatform() == null) ? 0 : getSharePlatform().hashCode());
        result = prime * result + ((getShareContent() == null) ? 0 : getShareContent().hashCode());
        result = prime * result + ((getShareUrl() == null) ? 0 : getShareUrl().hashCode());
        result = prime * result + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getDeviceInfo() == null) ? 0 : getDeviceInfo().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shareId=").append(shareId);
        sb.append(", userId=").append(userId);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", episodeId=").append(episodeId);
        sb.append(", shareType=").append(shareType);
        sb.append(", sharePlatform=").append(sharePlatform);
        sb.append(", shareContent=").append(shareContent);
        sb.append(", shareUrl=").append(shareUrl);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", location=").append(location);
        sb.append(", deviceInfo=").append(deviceInfo);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}