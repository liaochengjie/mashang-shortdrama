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
import com.lfy.kcat.content.domain.bo.EpisodesBo;
import com.lfy.kcat.content.domain.vo.EpisodesVo;
import com.lfy.kcat.content.domain.Episodes;
import com.lfy.kcat.content.mapper.EpisodesMapper;
import com.lfy.kcat.content.service.IEpisodesService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 剧集管理Service业务层处理
 *
 * @author liaochengjie
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EpisodesServiceImpl implements IEpisodesService {

    private final EpisodesMapper baseMapper;
    private final RagReleaseService ragRelease;

    /**
     * 查询剧集管理
     *
     * @param episodeId 主键
     * @return 剧集管理
     */
    @Override
    public EpisodesVo queryById(Long episodeId){
        return baseMapper.selectVoById(episodeId);
    }

    /**
     * 分页查询剧集管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 剧集管理分页列表
     */
    @Override
    public TableDataInfo<EpisodesVo> queryPageList(EpisodesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Episodes> lqw = buildQueryWrapper(bo);
        Page<EpisodesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的剧集管理列表
     *
     * @param bo 查询条件
     * @return 剧集管理列表
     */
    @Override
    public List<EpisodesVo> queryList(EpisodesBo bo) {
        LambdaQueryWrapper<Episodes> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Episodes> buildQueryWrapper(EpisodesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Episodes> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Episodes::getEpisodeId);
        lqw.eq(bo.getDramaId() != null, Episodes::getDramaId, bo.getDramaId());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), Episodes::getTitle, bo.getTitle());
        lqw.eq(bo.getEpisodeNumber() != null, Episodes::getEpisodeNumber, bo.getEpisodeNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getCover()), Episodes::getCover, bo.getCover());
        lqw.eq(bo.getDuration() != null, Episodes::getDuration, bo.getDuration());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Episodes::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getVideoUrl()), Episodes::getVideoUrl, bo.getVideoUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getVideoUrlHd()), Episodes::getVideoUrlHd, bo.getVideoUrlHd());
        lqw.eq(StringUtils.isNotBlank(bo.getVideoUrlSd()), Episodes::getVideoUrlSd, bo.getVideoUrlSd());
        lqw.eq(StringUtils.isNotBlank(bo.getSubtitleUrl()), Episodes::getSubtitleUrl, bo.getSubtitleUrl());
        lqw.eq(bo.getIsFree() != null, Episodes::getIsFree, bo.getIsFree());
        lqw.eq(bo.getIsTrailer() != null, Episodes::getIsTrailer, bo.getIsTrailer());
        lqw.eq(bo.getCoinPrice() != null, Episodes::getCoinPrice, bo.getCoinPrice());
        lqw.eq(bo.getPlayCount() != null, Episodes::getPlayCount, bo.getPlayCount());
        lqw.eq(bo.getLikeCount() != null, Episodes::getLikeCount, bo.getLikeCount());
        lqw.eq(bo.getCommentCount() != null, Episodes::getCommentCount, bo.getCommentCount());
        lqw.eq(bo.getShareCount() != null, Episodes::getShareCount, bo.getShareCount());
        lqw.eq(bo.getDanmakuCount() != null, Episodes::getDanmakuCount, bo.getDanmakuCount());
        lqw.eq(bo.getPublishTime() != null, Episodes::getPublishTime, bo.getPublishTime());
        lqw.eq(bo.getStatus() != null, Episodes::getStatus, bo.getStatus());
        lqw.eq(bo.getAuditStatus() != null, Episodes::getAuditStatus, bo.getAuditStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditReason()), Episodes::getAuditReason, bo.getAuditReason());
        return lqw;
    }

    /**
     * 新增剧集管理
     *
     * @param bo 剧集管理
     * @return 是否新增成功
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Boolean insertByBo(EpisodesBo bo) {
        Episodes add = MapstructUtils.convert(bo, Episodes.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setEpisodeId(add.getEpisodeId());
            ragRelease.refreshIfManaged(add.getDramaId());
        }
        return flag;
    }

    /**
     * 修改剧集管理
     *
     * @param bo 剧集管理
     * @return 是否修改成功
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Boolean updateByBo(EpisodesBo bo) {
        Episodes update = MapstructUtils.convert(bo, Episodes.class);
        validEntityBeforeSave(update);
        Episodes prior = baseMapper.selectById(bo.getEpisodeId());
        boolean changed = baseMapper.updateById(update) > 0;
        if (changed && prior != null && ragRelease.enabled()) {
            Episodes current = baseMapper.selectById(bo.getEpisodeId());
            if (!java.util.Objects.equals(prior.getVideoUrl(), current.getVideoUrl()) || !java.util.Objects.equals(prior.getSubtitleUrl(), current.getSubtitleUrl()) ||
                !java.util.Objects.equals(prior.getDescription(), current.getDescription()) || !java.util.Objects.equals(prior.getTitle(), current.getTitle()) ||
                !java.util.Objects.equals(prior.getEpisodeNumber(), current.getEpisodeNumber()) || !java.util.Objects.equals(prior.getCover(), current.getCover()) ||
                !java.util.Objects.equals(prior.getDuration(), current.getDuration()) || !java.util.Objects.equals(prior.getDramaId(),current.getDramaId())) {
                ragRelease.refreshIfManaged(prior.getDramaId());
                if (!java.util.Objects.equals(prior.getDramaId(),current.getDramaId())) ragRelease.refreshIfManaged(current.getDramaId());
            }
        }
        return changed;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Episodes entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除剧集管理信息
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
        List<Long> dramas=baseMapper.selectByIds(ids).stream().map(Episodes::getDramaId).distinct().sorted().toList();
        boolean changed=baseMapper.deleteByIds(ids)>0;
        if (changed) for (Long id:dramas) ragRelease.refreshIfManaged(id);
        // Removing the last required episode is rejected by capture and rolls back this transaction.
        return changed;
    }
}
