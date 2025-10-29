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
import com.lfy.kcat.content.domain.vo.EpisodesVo;
import com.lfy.kcat.content.domain.bo.EpisodesBo;
import com.lfy.kcat.content.service.IEpisodesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 剧集管理
 * 前端访问路由地址为:/content/episodes
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/episodes")
public class EpisodesController extends BaseController {

    private final IEpisodesService episodesService;

    /**
     * 查询剧集管理列表
     */
    @SaCheckPermission("content:episodes:list")
    @GetMapping("/list")
    public TableDataInfo<EpisodesVo> list(EpisodesBo bo, PageQuery pageQuery) {
        return episodesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出剧集管理列表
     */
    @SaCheckPermission("content:episodes:export")
    @Log(title = "剧集管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EpisodesBo bo, HttpServletResponse response) {
        List<EpisodesVo> list = episodesService.queryList(bo);
        ExcelUtil.exportExcel(list, "剧集管理", EpisodesVo.class, response);
    }

    /**
     * 获取剧集管理详细信息
     *
     * @param episodeId 主键
     */
    @SaCheckPermission("content:episodes:query")
    @GetMapping("/{episodeId}")
    public R<EpisodesVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("episodeId") Long episodeId) {
        return R.ok(episodesService.queryById(episodeId));
    }

    /**
     * 新增剧集管理
     */
    @SaCheckPermission("content:episodes:add")
    @Log(title = "剧集管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EpisodesBo bo) {
        return toAjax(episodesService.insertByBo(bo));
    }

    /**
     * 修改剧集管理
     */
    @SaCheckPermission("content:episodes:edit")
    @Log(title = "剧集管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EpisodesBo bo) {
        return toAjax(episodesService.updateByBo(bo));
    }

    /**
     * 删除剧集管理
     *
     * @param episodeIds 主键串
     */
    @SaCheckPermission("content:episodes:remove")
    @Log(title = "剧集管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{episodeIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("episodeIds") Long[] episodeIds) {
        return toAjax(episodesService.deleteWithValidByIds(List.of(episodeIds), true));
    }
}
