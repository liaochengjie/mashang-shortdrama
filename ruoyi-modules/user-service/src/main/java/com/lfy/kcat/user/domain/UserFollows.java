package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户关注表
 * @TableName user_follows
 */
@TableName(value ="user_follows")
@Data
public class UserFollows {
    /**
     * 关注ID
     */
    @TableId(type = IdType.AUTO)
    private Long followId;

    /**
     * 关注者ID
     */
    private Long followerId;

    /**
     * 被关注者ID
     */
    private Long followeeId;

    /**
     * 状态：0取消关注，1关注中
     */
    private Integer status;

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
        UserFollows other = (UserFollows) that;
        return (this.getFollowId() == null ? other.getFollowId() == null : this.getFollowId().equals(other.getFollowId()))
            && (this.getFollowerId() == null ? other.getFollowerId() == null : this.getFollowerId().equals(other.getFollowerId()))
            && (this.getFolloweeId() == null ? other.getFolloweeId() == null : this.getFolloweeId().equals(other.getFolloweeId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFollowId() == null) ? 0 : getFollowId().hashCode());
        result = prime * result + ((getFollowerId() == null) ? 0 : getFollowerId().hashCode());
        result = prime * result + ((getFolloweeId() == null) ? 0 : getFolloweeId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", followId=").append(followId);
        sb.append(", followerId=").append(followerId);
        sb.append(", followeeId=").append(followeeId);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}