package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户收藏表
 * @TableName user_collections
 */
@TableName(value ="user_collections")
@Data
public class UserCollections {
    /**
     * 收藏ID
     */
    @TableId(type = IdType.AUTO)
    private Long collectionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 短剧ID
     */
    private Long dramaId;

    /**
     * 短剧标题(冗余)
     */
    private String dramaTitle;

    /**
     * 短剧封面(冗余)
     */
    private String dramaCover;

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
        UserCollections other = (UserCollections) that;
        return (this.getCollectionId() == null ? other.getCollectionId() == null : this.getCollectionId().equals(other.getCollectionId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getDramaTitle() == null ? other.getDramaTitle() == null : this.getDramaTitle().equals(other.getDramaTitle()))
            && (this.getDramaCover() == null ? other.getDramaCover() == null : this.getDramaCover().equals(other.getDramaCover()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCollectionId() == null) ? 0 : getCollectionId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getDramaTitle() == null) ? 0 : getDramaTitle().hashCode());
        result = prime * result + ((getDramaCover() == null) ? 0 : getDramaCover().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", collectionId=").append(collectionId);
        sb.append(", userId=").append(userId);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", dramaTitle=").append(dramaTitle);
        sb.append(", dramaCover=").append(dramaCover);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}