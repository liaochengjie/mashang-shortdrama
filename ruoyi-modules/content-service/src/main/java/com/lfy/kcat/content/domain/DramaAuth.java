package com.lfy.kcat.content.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @TableName drama_auth
 */
@TableName(value ="drama_auth")
@Data
public class DramaAuth {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 短剧id
     */
    private Long dramaId;

    /**
     * 审核流程id
     */
    private String processId;

    /**
     * 执行到哪一步
     */
    private String stepName;

    /**
     * -1：未通过 0：审核中 1：审核通过
     */
    private Integer authStatus;

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
        DramaAuth other = (DramaAuth) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDramaId() == null ? other.getDramaId() == null : this.getDramaId().equals(other.getDramaId()))
            && (this.getProcessId() == null ? other.getProcessId() == null : this.getProcessId().equals(other.getProcessId()))
            && (this.getStepName() == null ? other.getStepName() == null : this.getStepName().equals(other.getStepName()))
            && (this.getAuthStatus() == null ? other.getAuthStatus() == null : this.getAuthStatus().equals(other.getAuthStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDramaId() == null) ? 0 : getDramaId().hashCode());
        result = prime * result + ((getProcessId() == null) ? 0 : getProcessId().hashCode());
        result = prime * result + ((getStepName() == null) ? 0 : getStepName().hashCode());
        result = prime * result + ((getAuthStatus() == null) ? 0 : getAuthStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dramaId=").append(dramaId);
        sb.append(", processId=").append(processId);
        sb.append(", stepName=").append(stepName);
        sb.append(", authStatus=").append(authStatus);
        sb.append("]");
        return sb.toString();
    }
}
