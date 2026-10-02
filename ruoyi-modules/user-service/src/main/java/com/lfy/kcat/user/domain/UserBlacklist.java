package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户黑名单表
 * @TableName user_blacklist
 */
@TableName(value ="user_blacklist")
@Data
public class UserBlacklist {
    /**
     * 黑名单ID
     */
    @TableId(type = IdType.AUTO)
    private Long blacklistId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑原因
     */
    private String reason;

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
        UserBlacklist other = (UserBlacklist) that;
        return (this.getBlacklistId() == null ? other.getBlacklistId() == null : this.getBlacklistId().equals(other.getBlacklistId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getBlockedUserId() == null ? other.getBlockedUserId() == null : this.getBlockedUserId().equals(other.getBlockedUserId()))
            && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBlacklistId() == null) ? 0 : getBlacklistId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getBlockedUserId() == null) ? 0 : getBlockedUserId().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", blacklistId=").append(blacklistId);
        sb.append(", userId=").append(userId);
        sb.append(", blockedUserId=").append(blockedUserId);
        sb.append(", reason=").append(reason);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}