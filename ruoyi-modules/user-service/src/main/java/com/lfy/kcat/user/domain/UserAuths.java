package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户认证表
 * @TableName user_auths
 */
@TableName(value ="user_auths")
@Data
public class UserAuths {
    /**
     * 认证ID
     */
    @TableId(type = IdType.AUTO)
    private Long authId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 认证类型：password/sms/wechat/qq/apple
     */
    private String authType;

    /**
     * 认证标识
     */
    private String authKey;

    /**
     * 认证凭证
     */
    private String authCredential;

    /**
     * 是否已验证
     */
    private Integer isVerified;

    /**
     * 验证时间
     */
    private Date verifyTime;

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
        UserAuths other = (UserAuths) that;
        return (this.getAuthId() == null ? other.getAuthId() == null : this.getAuthId().equals(other.getAuthId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAuthType() == null ? other.getAuthType() == null : this.getAuthType().equals(other.getAuthType()))
            && (this.getAuthKey() == null ? other.getAuthKey() == null : this.getAuthKey().equals(other.getAuthKey()))
            && (this.getAuthCredential() == null ? other.getAuthCredential() == null : this.getAuthCredential().equals(other.getAuthCredential()))
            && (this.getIsVerified() == null ? other.getIsVerified() == null : this.getIsVerified().equals(other.getIsVerified()))
            && (this.getVerifyTime() == null ? other.getVerifyTime() == null : this.getVerifyTime().equals(other.getVerifyTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAuthId() == null) ? 0 : getAuthId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAuthType() == null) ? 0 : getAuthType().hashCode());
        result = prime * result + ((getAuthKey() == null) ? 0 : getAuthKey().hashCode());
        result = prime * result + ((getAuthCredential() == null) ? 0 : getAuthCredential().hashCode());
        result = prime * result + ((getIsVerified() == null) ? 0 : getIsVerified().hashCode());
        result = prime * result + ((getVerifyTime() == null) ? 0 : getVerifyTime().hashCode());
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
        sb.append(", authId=").append(authId);
        sb.append(", userId=").append(userId);
        sb.append(", authType=").append(authType);
        sb.append(", authKey=").append(authKey);
        sb.append(", authCredential=").append(authCredential);
        sb.append(", isVerified=").append(isVerified);
        sb.append(", verifyTime=").append(verifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}