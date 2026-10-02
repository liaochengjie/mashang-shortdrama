package com.lfy.kcat.content.service.impl;

import com.lfy.kcat.content.biz.RagReleaseService;

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
import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.domain.vo.DramasVo;
import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.lfy.kcat.content.service.IDramasService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 短剧管理Service业务层处理
 *
 * @author liaochengjie
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DramasServiceImpl implements IDramasService {

    private final DramasMapper baseMapper;
    private final RagReleaseService ragRelease;

    /**
     * 查询短剧管理
     *
     * @param dramaId 主键
     * @return 短剧管理
     */
    @Override
    public DramasVo queryById(Long dramaId){
        return baseMapper.selectVoById(dramaId);
    }

    /**
     * 分页查询短剧管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 短剧管理分页列表
     */
    @Override
    public TableDataInfo<DramasVo> queryPageList(DramasBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Dramas> lqw = buildQueryWrapper(bo);
        Page<DramasVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的短剧管理列表
     *
     * @param bo 查询条件
     * @return 短剧管理列表
     */
    @Override
    public List<DramasVo> queryList(DramasBo bo) {
        LambdaQueryWrapper<Dramas> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Dramas> buildQueryWrapper(DramasBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Dramas> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Dramas::getDramaId);
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), Dramas::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getSubTitle()), Dramas::getSubTitle, bo.getSubTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getCover()), Dramas::getCover, bo.getCover());
        lqw.eq(StringUtils.isNotBlank(bo.getPoster()), Dramas::getPoster, bo.getPoster());
        lqw.eq(StringUtils.isNotBlank(bo.getTrailerUrl()), Dramas::getTrailerUrl, bo.getTrailerUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Dramas::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getStoryLine()), Dramas::getStoryLine, bo.getStoryLine());
        lqw.eq(StringUtils.isNotBlank(bo.getDirector()), Dramas::getDirector, bo.getDirector());
        lqw.eq(StringUtils.isNotBlank(bo.getScreenwriter()), Dramas::getScreenwriter, bo.getScreenwriter());
        lqw.eq(StringUtils.isNotBlank(bo.getProductionCompany()), Dramas::getProductionCompany, bo.getProductionCompany());
        lqw.eq(bo.getReleaseDate() != null, Dramas::getReleaseDate, bo.getReleaseDate());
        lqw.eq(bo.getTotalEpisodes() != null, Dramas::getTotalEpisodes, bo.getTotalEpisodes());
        lqw.eq(bo.getCurrentEpisodes() != null, Dramas::getCurrentEpisodes, bo.getCurrentEpisodes());
        lqw.eq(bo.getEpisodeDuration() != null, Dramas::getEpisodeDuration, bo.getEpisodeDuration());
        lqw.eq(bo.getTotalDuration() != null, Dramas::getTotalDuration, bo.getTotalDuration());
        lqw.eq(StringUtils.isNotBlank(bo.getLanguage()), Dramas::getLanguage, bo.getLanguage());
        lqw.eq(StringUtils.isNotBlank(bo.getRegion()), Dramas::getRegion, bo.getRegion());
        lqw.eq(bo.getYear() != null, Dramas::getYear, bo.getYear());
        lqw.eq(bo.getIsFinished() != null, Dramas::getIsFinished, bo.getIsFinished());
        lqw.eq(bo.getIsVip() != null, Dramas::getIsVip, bo.getIsVip());
        lqw.eq(bo.getIsNew() != null, Dramas::getIsNew, bo.getIsNew());
        lqw.eq(bo.getIsHot() != null, Dramas::getIsHot, bo.getIsHot());
        lqw.eq(bo.getIsRecommended() != null, Dramas::getIsRecommended, bo.getIsRecommended());
        lqw.eq(StringUtils.isNotBlank(bo.getQuality()), Dramas::getQuality, bo.getQuality());
        lqw.eq(StringUtils.isNotBlank(bo.getAgeRating()), Dramas::getAgeRating, bo.getAgeRating());
        lqw.eq(bo.getPlayCount() != null, Dramas::getPlayCount, bo.getPlayCount());
        lqw.eq(bo.getLikeCount() != null, Dramas::getLikeCount, bo.getLikeCount());
        lqw.eq(bo.getCommentCount() != null, Dramas::getCommentCount, bo.getCommentCount());
        lqw.eq(bo.getShareCount() != null, Dramas::getShareCount, bo.getShareCount());
        lqw.eq(bo.getCollectionCount() != null, Dramas::getCollectionCount, bo.getCollectionCount());
        lqw.eq(bo.getFollowCount() != null, Dramas::getFollowCount, bo.getFollowCount());
        lqw.eq(bo.getRatingScore() != null, Dramas::getRatingScore, bo.getRatingScore());
        lqw.eq(bo.getRatingCount() != null, Dramas::getRatingCount, bo.getRatingCount());
        lqw.eq(bo.getStatus() != null, Dramas::getStatus, bo.getStatus());
        lqw.eq(bo.getAuditStatus() != null, Dramas::getAuditStatus, bo.getAuditStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditReason()), Dramas::getAuditReason, bo.getAuditReason());
        lqw.eq(bo.getSortOrder() != null, Dramas::getSortOrder, bo.getSortOrder());
        return lqw;
    }

    /**
     * 新增短剧管理
     *
     * @param bo 短剧管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(DramasBo bo) {
        Dramas add = MapstructUtils.convert(bo, Dramas.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDramaId(add.getDramaId());
        }
        return flag;
    }

    /**
     * 修改短剧管理
     *
     * @param bo 短剧管理
     * @return 是否修改成功
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Boolean updateByBo(DramasBo bo) {
        Dramas update = MapstructUtils.convert(bo, Dramas.class);
        validEntityBeforeSave(update);
        Dramas prior = baseMapper.selectById(bo.getDramaId());
        // Audit is owned by the versioned approval flow, not generic CRUD form fields.
        if (prior!=null && ragRelease.managed(bo.getDramaId())) update.setAuditStatus(prior.getAuditStatus());
        boolean changed = baseMapper.updateById(update) > 0;
        if (changed && prior != null && ragRelease.enabled()) {
            if (bo.getStatus()!=null && !java.util.Objects.equals(prior.getStatus(),bo.getStatus())) ragRelease.shelfIntent(bo.getDramaId(), bo.getStatus());
            Dramas current = baseMapper.selectById(bo.getDramaId());
            if (!java.util.Objects.equals(prior.getTitle(), current.getTitle()) || !java.util.Objects.equals(prior.getDescription(), current.getDescription()) ||
                !java.util.Objects.equals(prior.getStoryLine(), current.getStoryLine()) || !java.util.Objects.equals(prior.getCover(), current.getCover()))
                ragRelease.refreshIfManaged(bo.getDramaId());
        }
        return changed;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Dramas entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除短剧管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        ragRelease.deleted(ids);
        return baseMapper.deleteByIds(ids) > 0;
    }
}
