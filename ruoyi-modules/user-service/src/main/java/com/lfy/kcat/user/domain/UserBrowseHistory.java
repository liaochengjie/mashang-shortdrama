package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户短剧浏览历史表
 * @TableName user_browse_history
 */
@TableName(value ="user_browse_history")
@Data
public class UserBrowseHistory {
    /**
     * 历史ID
     */
    @TableId(type = IdType.AUTO)
    private Long historyId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容ID
     */
    private Long dramaId;

    /**
     * 内容标题(冗余)
     */
    private String title;

    /**
     * 内容封面(冗余)
     */
    private String cover;

    /**
     * 浏览时长(秒)
     */
    private Integer browseTime;

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
        UserBrowseHistory other = (UserBrowseHistory) that;
        return (this.getHistoryId() == null ? other.getHistoryId() == null : this.getHistoryId().equals(other.getHistoryId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getCover() == null ? other.getCover() == null : this.getCover().equals(other.getCover()))
            && (this.getBrowseTime() == null ? other.getBrowseTime() == null : this.getBrowseTime().equals(other.getBrowseTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getHistoryId() == null) ? 0 : getHistoryId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getCover() == null) ? 0 : getCover().hashCode());
        result = prime * result + ((getBrowseTime() == null) ? 0 : getBrowseTime().hashCode());
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
        sb.append(", historyId=").append(historyId);
        sb.append(", userId=").append(userId);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", title=").append(title);
        sb.append(", cover=").append(cover);
        sb.append(", browseTime=").append(browseTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}