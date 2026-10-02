package com.lfy.kcat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户等级配置表
 * @TableName user_level_config
 */
@TableName(value ="user_level_config")
@Data
public class UserLevelConfig {
    /**
     * 等级
     */
    @TableId
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 最小经验值
     */
    private Integer minExperience;

    /**
     * 最大经验值
     */
    private Integer maxExperience;

    /**
     * 等级特权，JSON格式
     */
    private String privileges;

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
        UserLevelConfig other = (UserLevelConfig) that;
        return (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getLevelName() == null ? other.getLevelName() == null : this.getLevelName().equals(other.getLevelName()))
            && (this.getMinExperience() == null ? other.getMinExperience() == null : this.getMinExperience().equals(other.getMinExperience()))
            && (this.getMaxExperience() == null ? other.getMaxExperience() == null : this.getMaxExperience().equals(other.getMaxExperience()))
            && (this.getPrivileges() == null ? other.getPrivileges() == null : this.getPrivileges().equals(other.getPrivileges()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getLevelName() == null) ? 0 : getLevelName().hashCode());
        result = prime * result + ((getMinExperience() == null) ? 0 : getMinExperience().hashCode());
        result = prime * result + ((getMaxExperience() == null) ? 0 : getMaxExperience().hashCode());
        result = prime * result + ((getPrivileges() == null) ? 0 : getPrivileges().hashCode());
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
        sb.append(", level=").append(level);
        sb.append(", levelName=").append(levelName);
        sb.append(", minExperience=").append(minExperience);
        sb.append(", maxExperience=").append(maxExperience);
        sb.append(", privileges=").append(privileges);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}