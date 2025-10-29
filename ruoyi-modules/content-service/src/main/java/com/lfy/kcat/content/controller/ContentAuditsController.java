package com.lfy.kcat.content.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import com.lfy.kcat.content.domain.vo.ContentAuditsVo;
import com.lfy.kcat.content.domain.bo.ContentAuditsBo;
import com.lfy.kcat.content.service.IContentAuditsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 内容审核
 * 前端访问路由地址为:/content/contentAudits
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/contentAudits")
public class ContentAuditsController extends BaseController {

    private final IContentAuditsService contentAuditsService;

    /**
     * 查询内容审核列表
     */
    @SaCheckPermission("content:contentAudits:list")
    @GetMapping("/list")
    public TableDataInfo<ContentAuditsVo> list(ContentAuditsBo bo, PageQuery pageQuery) {
        return contentAuditsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出内容审核列表
     */
    @SaCheckPermission("content:contentAudits:export")
    @Log(title = "内容审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ContentAuditsBo bo, HttpServletResponse response) {
        List<ContentAuditsVo> list = contentAuditsService.queryList(bo);
        ExcelUtil.exportExcel(list, "内容审核", ContentAuditsVo.class, response);
    }

    /**
     * 获取内容审核详细信息
     *
     * @param auditId 主键
     */
    @SaCheckPermission("content:contentAudits:query")
    @GetMapping("/{auditId}")
    public R<ContentAuditsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("auditId") Long auditId) {
        return R.ok(contentAuditsService.queryById(auditId));
    }

    /**
     * 新增内容审核
     */
    @SaCheckPermission("content:contentAudits:add")
    @Log(title = "内容审核", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ContentAuditsBo bo) {
        return toAjax(contentAuditsService.insertByBo(bo));
    }

    /**
     * 修改内容审核
     */
    @SaCheckPermission("content:contentAudits:edit")
    @Log(title = "内容审核", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ContentAuditsBo bo) {
        return toAjax(contentAuditsService.updateByBo(bo));
    }

    /**
     * 删除内容审核
     *
     * @param auditIds 主键串
     */
    @SaCheckPermission("content:contentAudits:remove")
    @Log(title = "内容审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{auditIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("auditIds") Long[] auditIds) {
        return toAjax(contentAuditsService.deleteWithValidByIds(List.of(auditIds), true));
    }
}
