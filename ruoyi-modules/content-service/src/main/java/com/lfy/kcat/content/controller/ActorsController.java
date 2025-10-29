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
import com.lfy.kcat.content.domain.vo.ActorsVo;
import com.lfy.kcat.content.domain.bo.ActorsBo;
import com.lfy.kcat.content.service.IActorsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 演员管理
 * 前端访问路由地址为:/content/actors
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/actors")
public class ActorsController extends BaseController {

    private final IActorsService actorsService;

    /**
     * 查询演员管理列表
     */
    @SaCheckPermission("content:actors:list")
    @GetMapping("/list")
    public TableDataInfo<ActorsVo> list(ActorsBo bo, PageQuery pageQuery) {
        return actorsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出演员管理列表
     */
    @SaCheckPermission("content:actors:export")
    @Log(title = "演员管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ActorsBo bo, HttpServletResponse response) {
        List<ActorsVo> list = actorsService.queryList(bo);
        ExcelUtil.exportExcel(list, "演员管理", ActorsVo.class, response);
    }

    /**
     * 获取演员管理详细信息
     *
     * @param actorId 主键
     */
    @SaCheckPermission("content:actors:query")
    @GetMapping("/{actorId}")
    public R<ActorsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("actorId") Long actorId) {
        return R.ok(actorsService.queryById(actorId));
    }

    /**
     * 新增演员管理
     */
    @SaCheckPermission("content:actors:add")
    @Log(title = "演员管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActorsBo bo) {
        return toAjax(actorsService.insertByBo(bo));
    }

    /**
     * 修改演员管理
     */
    @SaCheckPermission("content:actors:edit")
    @Log(title = "演员管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActorsBo bo) {
        return toAjax(actorsService.updateByBo(bo));
    }

    /**
     * 删除演员管理
     *
     * @param actorIds 主键串
     */
    @SaCheckPermission("content:actors:remove")
    @Log(title = "演员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{actorIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("actorIds") Long[] actorIds) {
        return toAjax(actorsService.deleteWithValidByIds(List.of(actorIds), true));
    }
}
