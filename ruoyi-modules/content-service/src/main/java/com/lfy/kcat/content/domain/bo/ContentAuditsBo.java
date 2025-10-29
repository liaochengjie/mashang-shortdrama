package com.lfy.kcat.content.domain.bo;

import com.lfy.kcat.content.domain.ContentAudits;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 内容审核业务对象 content_audits
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ContentAudits.class, reverseConvertGenerate = false)
public class ContentAuditsBo extends BaseEntity {

    /**
     * 审核ID
     */
    @NotNull(message = "审核ID不能为空", groups = { EditGroup.class })
    private Long auditId;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long contentId;

    /**
     * 内容类型：1短剧，2剧集，3评论，4弹幕
     */
    @NotNull(message = "内容类型：1短剧，2剧集，3评论，4弹幕不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long contentType;

    /**
     * 审核类型：1机器审核，2人工审核
     */
    @NotNull(message = "审核类型：1机器审核，2人工审核不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long auditType;

    /**
     * 审核员ID
     */
    private Long auditorId;

    /**
     * 审核结果：0拒绝，1通过，2需要人工审核
     */
    @NotNull(message = "审核结果：0拒绝，1通过，2需要人工审核不能为空", groups = { AddGroup.class, EditGroup.class })
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
