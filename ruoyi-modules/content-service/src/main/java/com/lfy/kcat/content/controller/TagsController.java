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
import com.lfy.kcat.content.domain.vo.TagsVo;
import com.lfy.kcat.content.domain.bo.TagsBo;
import com.lfy.kcat.content.service.ITagsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 标签管理
 * 前端访问路由地址为:/content/tags
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tags")
public class TagsController extends BaseController {

    private final ITagsService tagsService;

    /**
     * 查询标签管理列表
     */
    @SaCheckPermission("content:tags:list")
    @GetMapping("/list")
    public TableDataInfo<TagsVo> list(TagsBo bo, PageQuery pageQuery) {
        return tagsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出标签管理列表
     */
    @SaCheckPermission("content:tags:export")
    @Log(title = "标签管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TagsBo bo, HttpServletResponse response) {
        List<TagsVo> list = tagsService.queryList(bo);
        ExcelUtil.exportExcel(list, "标签管理", TagsVo.class, response);
    }

    /**
     * 获取标签管理详细信息
     *
     * @param tagId 主键
     */
    @SaCheckPermission("content:tags:query")
    @GetMapping("/{tagId}")
    public R<TagsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("tagId") Long tagId) {
        return R.ok(tagsService.queryById(tagId));
    }

    /**
     * 新增标签管理
     */
    @SaCheckPermission("content:tags:add")
    @Log(title = "标签管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TagsBo bo) {
        return toAjax(tagsService.insertByBo(bo));
    }

    /**
     * 修改标签管理
     */
    @SaCheckPermission("content:tags:edit")
    @Log(title = "标签管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TagsBo bo) {
        return toAjax(tagsService.updateByBo(bo));
    }

    /**
     * 删除标签管理
     *
     * @param tagIds 主键串
     */
    @SaCheckPermission("content:tags:remove")
    @Log(title = "标签管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tagIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("tagIds") Long[] tagIds) {
        return toAjax(tagsService.deleteWithValidByIds(List.of(tagIds), true));
    }
}
