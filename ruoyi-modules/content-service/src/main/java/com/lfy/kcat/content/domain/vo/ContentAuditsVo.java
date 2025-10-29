package com.lfy.kcat.content.domain.vo;

import com.lfy.kcat.content.domain.ContentAudits;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 内容审核视图对象 content_audits
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ContentAudits.class)
public class ContentAuditsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审核ID
     */
    @ExcelProperty(value = "审核ID")
    private Long auditId;

    /**
     * 内容ID
     */
    @ExcelProperty(value = "内容ID")
    private Long contentId;

    /**
     * 内容类型：1短剧，2剧集，3评论，4弹幕
     */
    @ExcelProperty(value = "内容类型：1短剧，2剧集，3评论，4弹幕")
    private Long contentType;

    /**
     * 审核类型：1机器审核，2人工审核
     */
    @ExcelProperty(value = "审核类型：1机器审核，2人工审核")
    private Long auditType;

    /**
     * 审核员ID
     */
    @ExcelProperty(value = "审核员ID")
    private Long auditorId;

    /**
     * 审核结果：0拒绝，1通过，2需要人工审核
     */
    @ExcelProperty(value = "审核结果：0拒绝，1通过，2需要人工审核")
    private Long auditResult;

    /**
     * 审核原因
     */
    @ExcelProperty(value = "审核原因")
    private String auditReason;

    /**
     * 审核分数
     */
    @ExcelProperty(value = "审核分数")
    private Long auditScore;


}
