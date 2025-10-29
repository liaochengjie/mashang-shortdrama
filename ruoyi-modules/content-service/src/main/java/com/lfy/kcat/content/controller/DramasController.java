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
import com.lfy.kcat.content.domain.vo.DramasVo;
import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.service.IDramasService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 短剧管理
 * 前端访问路由地址为:/content/dramas
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dramas")
public class DramasController extends BaseController {

    private final IDramasService dramasService;

    /**
     * 查询短剧管理列表
     */
    @SaCheckPermission("content:dramas:list")
    @GetMapping("/list")
    public TableDataInfo<DramasVo> list(DramasBo bo, PageQuery pageQuery) {
        return dramasService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出短剧管理列表
     */
    @SaCheckPermission("content:dramas:export")
    @Log(title = "短剧管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DramasBo bo, HttpServletResponse response) {
        List<DramasVo> list = dramasService.queryList(bo);
        ExcelUtil.exportExcel(list, "短剧管理", DramasVo.class, response);
    }

    /**
     * 获取短剧管理详细信息
     *
     * @param dramaId 主键
     */
    @SaCheckPermission("content:dramas:query")
    @GetMapping("/{dramaId}")
    public R<DramasVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("dramaId") Long dramaId) {
        return R.ok(dramasService.queryById(dramaId));
    }

    /**
     * 新增短剧管理
     */
    @SaCheckPermission("content:dramas:add")
    @Log(title = "短剧管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DramasBo bo) {
        return toAjax(dramasService.insertByBo(bo));
    }

    /**
     * 修改短剧管理
     */
    @SaCheckPermission("content:dramas:edit")
    @Log(title = "短剧管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DramasBo bo) {
        return toAjax(dramasService.updateByBo(bo));
    }

    /**
     * 删除短剧管理
     *
     * @param dramaIds 主键串
     */
    @SaCheckPermission("content:dramas:remove")
    @Log(title = "短剧管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dramaIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("dramaIds") Long[] dramaIds) {
        return toAjax(dramasService.deleteWithValidByIds(List.of(dramaIds), true));
    }
}
