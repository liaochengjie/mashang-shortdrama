package com.lfy.kcat.interaction.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 弹幕屏蔽词表
 * @TableName danmaku_filter_words
 */
@TableName(value ="danmaku_filter_words")
@Data
public class DanmakuFilterWords {
    /**
     * 过滤ID
     */
    @TableId(type = IdType.AUTO)
    private Long filterId;

    /**
     * 屏蔽关键词
     */
    private String keyword;

    /**
     * 过滤类型：0完全匹配，1模糊匹配，2正则匹配
     */
    private Integer filterType;

    /**
     * 替换内容，为空则直接过滤
     */
    private String replacement;

    /**
     * 过滤等级：1低，2中，3高
     */
    private Integer level;

    /**
     * 是否启用
     */
    private Integer isEnabled;

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
        DanmakuFilterWords other = (DanmakuFilterWords) that;
        return (this.getFilterId() == null ? other.getFilterId() == null : this.getFilterId().equals(other.getFilterId()))
            && (this.getKeyword() == null ? other.getKeyword() == null : this.getKeyword().equals(other.getKeyword()))
            && (this.getFilterType() == null ? other.getFilterType() == null : this.getFilterType().equals(other.getFilterType()))
            && (this.getReplacement() == null ? other.getReplacement() == null : this.getReplacement().equals(other.getReplacement()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getIsEnabled() == null ? other.getIsEnabled() == null : this.getIsEnabled().equals(other.getIsEnabled()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFilterId() == null) ? 0 : getFilterId().hashCode());
        result = prime * result + ((getKeyword() == null) ? 0 : getKeyword().hashCode());
        result = prime * result + ((getFilterType() == null) ? 0 : getFilterType().hashCode());
        result = prime * result + ((getReplacement() == null) ? 0 : getReplacement().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getIsEnabled() == null) ? 0 : getIsEnabled().hashCode());
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
        sb.append(", filterId=").append(filterId);
        sb.append(", keyword=").append(keyword);
        sb.append(", filterType=").append(filterType);
        sb.append(", replacement=").append(replacement);
        sb.append(", level=").append(level);
        sb.append(", isEnabled=").append(isEnabled);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}