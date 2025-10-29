package com.lfy.kcat.content.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.lfy.kcat.content.domain.bo.ContentAuditsBo;
import com.lfy.kcat.content.domain.vo.ContentAuditsVo;
import com.lfy.kcat.content.domain.ContentAudits;
import com.lfy.kcat.content.mapper.ContentAuditsMapper;
import com.lfy.kcat.content.service.IContentAuditsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 内容审核Service业务层处理
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ContentAuditsServiceImpl implements IContentAuditsService {

    private final ContentAuditsMapper baseMapper;

    /**
     * 查询内容审核
     *
     * @param auditId 主键
     * @return 内容审核
     */
    @Override
    public ContentAuditsVo queryById(Long auditId){
        return baseMapper.selectVoById(auditId);
    }

    /**
     * 分页查询内容审核列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 内容审核分页列表
     */
    @Override
    public TableDataInfo<ContentAuditsVo> queryPageList(ContentAuditsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ContentAudits> lqw = buildQueryWrapper(bo);
        Page<ContentAuditsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的内容审核列表
     *
     * @param bo 查询条件
     * @return 内容审核列表
     */
    @Override
    public List<ContentAuditsVo> queryList(ContentAuditsBo bo) {
        LambdaQueryWrapper<ContentAudits> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ContentAudits> buildQueryWrapper(ContentAuditsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ContentAudits> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ContentAudits::getAuditId);
        lqw.eq(bo.getContentId() != null, ContentAudits::getContentId, bo.getContentId());
        lqw.eq(bo.getContentType() != null, ContentAudits::getContentType, bo.getContentType());
        lqw.eq(bo.getAuditType() != null, ContentAudits::getAuditType, bo.getAuditType());
        lqw.eq(bo.getAuditorId() != null, ContentAudits::getAuditorId, bo.getAuditorId());
        lqw.eq(bo.getAuditResult() != null, ContentAudits::getAuditResult, bo.getAuditResult());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditReason()), ContentAudits::getAuditReason, bo.getAuditReason());
        lqw.eq(bo.getAuditScore() != null, ContentAudits::getAuditScore, bo.getAuditScore());
        return lqw;
    }

    /**
     * 新增内容审核
     *
     * @param bo 内容审核
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ContentAuditsBo bo) {
        ContentAudits add = MapstructUtils.convert(bo, ContentAudits.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setAuditId(add.getAuditId());
        }
        return flag;
    }

    /**
     * 修改内容审核
     *
     * @param bo 内容审核
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ContentAuditsBo bo) {
        ContentAudits update = MapstructUtils.convert(bo, ContentAudits.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ContentAudits entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除内容审核信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
