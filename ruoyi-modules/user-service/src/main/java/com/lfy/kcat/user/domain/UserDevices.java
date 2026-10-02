package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户设备表
 * @TableName user_devices
 */
@TableName(value ="user_devices")
@Data
public class UserDevices {
    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Long deviceId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备唯一标识
     */
    private String deviceUuid;

    /**
     * 设备类型：iOS/Android/Web
     */
    private String deviceType;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * 设备推送令牌
     */
    private String deviceToken;

    /**
     * App版本
     */
    private String appVersion;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 是否在线
     */
    private Integer isOnline;

    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;

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
        UserDevices other = (UserDevices) that;
        return (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getDeviceUuid() == null ? other.getDeviceUuid() == null : this.getDeviceUuid().equals(other.getDeviceUuid()))
            && (this.getDeviceType() == null ? other.getDeviceType() == null : this.getDeviceType().equals(other.getDeviceType()))
            && (this.getDeviceModel() == null ? other.getDeviceModel() == null : this.getDeviceModel().equals(other.getDeviceModel()))
            && (this.getSystemVersion() == null ? other.getSystemVersion() == null : this.getSystemVersion().equals(other.getSystemVersion()))
            && (this.getDeviceToken() == null ? other.getDeviceToken() == null : this.getDeviceToken().equals(other.getDeviceToken()))
            && (this.getAppVersion() == null ? other.getAppVersion() == null : this.getAppVersion().equals(other.getAppVersion()))
            && (this.getIpAddress() == null ? other.getIpAddress() == null : this.getIpAddress().equals(other.getIpAddress()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getIsOnline() == null ? other.getIsOnline() == null : this.getIsOnline().equals(other.getIsOnline()))
            && (this.getLastActiveTime() == null ? other.getLastActiveTime() == null : this.getLastActiveTime().equals(other.getLastActiveTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDeviceUuid() == null) ? 0 : getDeviceUuid().hashCode());
        result = prime * result + ((getDeviceType() == null) ? 0 : getDeviceType().hashCode());
        result = prime * result + ((getDeviceModel() == null) ? 0 : getDeviceModel().hashCode());
        result = prime * result + ((getSystemVersion() == null) ? 0 : getSystemVersion().hashCode());
        result = prime * result + ((getDeviceToken() == null) ? 0 : getDeviceToken().hashCode());
        result = prime * result + ((getAppVersion() == null) ? 0 : getAppVersion().hashCode());
        result = prime * result + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getIsOnline() == null) ? 0 : getIsOnline().hashCode());
        result = prime * result + ((getLastActiveTime() == null) ? 0 : getLastActiveTime().hashCode());
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
        sb.append(", deviceId=").append(deviceId);
        sb.append(", userId=").append(userId);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", deviceModel=").append(deviceModel);
        sb.append(", systemVersion=").append(systemVersion);
        sb.append(", deviceToken=").append(deviceToken);
        sb.append(", appVersion=").append(appVersion);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", location=").append(location);
        sb.append(", isOnline=").append(isOnline);
        sb.append(", lastActiveTime=").append(lastActiveTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}