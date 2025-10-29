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
import com.lfy.kcat.content.domain.vo.CategoriesVo;
import com.lfy.kcat.content.domain.bo.CategoriesBo;
import com.lfy.kcat.content.service.ICategoriesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 分类管理
 * 前端访问路由地址为:/content/categories
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoriesController extends BaseController {

    private final ICategoriesService categoriesService;

    /**
     * 查询分类管理列表
     */
    @SaCheckPermission("content:categories:list")
    @GetMapping("/list")
    public TableDataInfo<CategoriesVo> list(CategoriesBo bo, PageQuery pageQuery) {
        return categoriesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出分类管理列表
     */
    @SaCheckPermission("content:categories:export")
    @Log(title = "分类管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategoriesBo bo, HttpServletResponse response) {
        List<CategoriesVo> list = categoriesService.queryList(bo);
        ExcelUtil.exportExcel(list, "分类管理", CategoriesVo.class, response);
    }

    /**
     * 获取分类管理详细信息
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("content:categories:query")
    @GetMapping("/{categoryId}")
    public R<CategoriesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("categoryId") Long categoryId) {
        return R.ok(categoriesService.queryById(categoryId));
    }

    /**
     * 新增分类管理
     */
    @SaCheckPermission("content:categories:add")
    @Log(title = "分类管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategoriesBo bo) {
        return toAjax(categoriesService.insertByBo(bo));
    }

    /**
     * 修改分类管理
     */
    @SaCheckPermission("content:categories:edit")
    @Log(title = "分类管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategoriesBo bo) {
        return toAjax(categoriesService.updateByBo(bo));
    }

    /**
     * 删除分类管理
     *
     * @param categoryIds 主键串
     */
    @SaCheckPermission("content:categories:remove")
    @Log(title = "分类管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("categoryIds") Long[] categoryIds) {
        return toAjax(categoriesService.deleteWithValidByIds(List.of(categoryIds), true));
    }
}
