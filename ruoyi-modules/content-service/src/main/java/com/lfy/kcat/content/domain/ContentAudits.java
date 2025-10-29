package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 内容审核对象 content_audits
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("content_audits")
public class ContentAudits extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审核ID
     */
    @TableId(value = "audit_id")
    private Long auditId;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容类型：1短剧，2剧集，3评论，4弹幕
     */
    private Long contentType;

    /**
     * 审核类型：1机器审核，2人工审核
     */
    private Long auditType;

    /**
     * 审核员ID
     */
    private Long auditorId;

    /**
     * 审核结果：0拒绝，1通过，2需要人工审核
     */
    private Long auditResult;

    /**
     * 审核原因
     */
    private String auditReason;

    /**
     * 审核分数
     */
    private Long auditScore;


}
