package com.lfy.kcat.content.biz.impl;

import com.lfy.kcat.content.biz.RagReleaseService;
import com.lfy.kcat.content.biz.RagPublishedViews;

import com.alibaba.fastjson2.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lfy.kcat.content.mapper.DramaActorsMapper;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO.StatisticsDTO;

import com.lfy.kcat.content.domain.*;
import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.mapper.EpisodesMapper;
import com.lfy.kcat.content.service.DramaActorsService;
import com.lfy.kcat.content.service.DramaCategoriesService;
import com.lfy.kcat.content.service.DramaTagsService;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO.EpisodesDTO.DramaDTO;

import com.lfy.kcat.content.biz.DramaHomeService;
import com.lfy.kcat.content.domain.vo.DramasVo;
import com.lfy.kcat.content.service.IDramasService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liaochengjie
 */
@Service
@Slf4j
public class DramaHomeServiceImpl implements DramaHomeService {
    @Autowired
    private RagReleaseService ragRelease;
    @Autowired
    private RagPublishedViews ragViews;

    @Autowired
    IDramasService iDramasService;

    @Autowired
    DramaCategoriesService dramaCategoriesService;

    @Autowired
    DramaActorsService dramaActorsService;

    @Autowired
    DramaTagsService dramaTagsService;

    @Autowired
    EpisodesMapper episodesMapper;

    @Autowired
    DramaActorsMapper dramaActorsMapper;

    /**
     * 获取首页精选视频数据
     *
     * @param pageReqDTO
     * @return
     */
    @Override
    public HomeFeaturedDTO getHomeFeaturedDrama(PageReqDTO pageReqDTO) {
        if (ragRelease.enabled()) return ragViews.featured(pageReqDTO);
        HomeFeaturedDTO featuredDTO = new HomeFeaturedDTO();
        //1）、信息流所在的某一集信息
        PageQuery pageQuery = new PageQuery(pageReqDTO.getPageSize(), pageReqDTO.getPage());
        //后续需要加两个条件status=1 and audi_status=1
        DramasBo dramasBo = new DramasBo();
        dramasBo.setStatus(1L);
        dramasBo.setAuditStatus(1L);
        //分页查询剧集信息
        TableDataInfo<DramasVo> tableDataInfo = iDramasService.queryPageList(dramasBo, pageQuery);

        //将查询到的剧集信息转换为EpisodesDTO
        List<HomeFeaturedDTO.EpisodesDTO> episodesDTOS = tableDataInfo.getRows()
            .stream()
            .map(dramasVo -> {
                HomeFeaturedDTO.EpisodesDTO episodesDTO = new HomeFeaturedDTO.EpisodesDTO();

                //1.查询信息流信息
                Episodes infoFlow = episodesMapper.getDramaInfoFlowsEpisode(dramasVo.getDramaId());
                //非空判断
                if (infoFlow != null) {
                    episodesDTO.setEpisode(infoFlow.getEpisodeId().toString());
                    episodesDTO.setTitle("第" + infoFlow.getEpisodeNumber() + "集");
                } else {
                    // 当没有找到首集或预告集时，设置默认值或跳过
                    log.warn("剧集 {} 未找到首集或预告集信息", dramasVo.getDramaId());
                    episodesDTO.setEpisode("0"); // 设置默认值
                    episodesDTO.setTitle("暂无集数信息");
                }
//            episodesDTO.setEpisode(infoFlow.getEpisodeId().toString());
//            episodesDTO.setTitle("第"+infoFlow.getEpisodeNumber()+"集");
                episodesDTO.setDramaTitle(dramasVo.getTitle());
                episodesDTO.setDescription(dramasVo.getDescription());
                episodesDTO.setDuration(String.valueOf(dramasVo.getTotalDuration()));
                episodesDTO.setLikeCount(dramasVo.getLikeCount().intValue());
                //TODO 通过互动服务获取点赞量
                episodesDTO.setFavoriteCount(0);
                episodesDTO.setCommentCount(dramasVo.getCommentCount().intValue());
                episodesDTO.setViewCount(dramasVo.getPlayCount().intValue());

                //TODO 通过互动服务获取该用户是否喜欢和收藏
                episodesDTO.setIsLiked(false);
                episodesDTO.setIsFavorited(false);
                episodesDTO.setRatingCount(0);

                DramaDTO dramaDTO = new DramaDTO();
                dramaDTO.setId(dramasVo.getDramaId().toString());
                dramaDTO.setTitle(dramasVo.getTitle());
                dramaDTO.setCover(dramasVo.getCover());


                //查询剧集标签
                List<Tags> dramaTags = dramaTagsService.getDramaTagsByDramaId(dramasVo.getDramaId());
                List<String> tagsList = dramaTags.stream().map(t -> t.getName()).toList();
                dramaDTO.setTags(tagsList);


                //查询剧集分类
                List<Categories> categories = dramaCategoriesService.getDramaCategoriesInfo(dramasVo.getDramaId());
                List<String> list = categories.stream().map(Categories::getName).toList();
                dramaDTO.setCategory(String.join(",", list));
                dramaDTO.setIsNew(false);
                dramaDTO.setIsPopular(false);
                dramaDTO.setDirector(dramasVo.getDirector());

                //查询演员数据
                //调用方法获取演员角色信息
                List<ActorRoleInfoEntity> actorRoleInfoEntities = dramaActorsService.getDramaActorsInfo(dramasVo.getDramaId());
                //封装演员的名字和角色和头像
                List<DramaDTO.ActorsDTO> actorsDTOList = actorRoleInfoEntities.stream().map(ar -> {
                    DramaDTO.ActorsDTO actorsDTO = new DramaDTO.ActorsDTO();
                    actorsDTO.setName(ar.getActorName());
                    actorsDTO.setRole(ar.getRoleName());
                    actorsDTO.setAvatar(ar.getAvatar());
                    return actorsDTO;
                }).toList();
                dramaDTO.setActors(actorsDTOList);
                episodesDTO.setHotScore(0);
                episodesDTO.setDrama(dramaDTO);
                episodesDTO.setCover(dramasVo.getCover());
                episodesDTO.setVideoUrl(dramasVo.getTrailerUrl());

                return episodesDTO;

            }).toList();
        //封装剧集数据
        featuredDTO.setEpisodes(episodesDTOS);
        //1/2）、分页
        HomeFeaturedDTO.PaginationDTO dto = new HomeFeaturedDTO.PaginationDTO();
        dto.setPage(pageReqDTO.getPage());
        dto.setPageSize(pageReqDTO.getPageSize());
        dto.setTotal(Integer.parseInt(tableDataInfo.getTotal() + ""));
        //总页码
        dto.setTotalPages((int) Math.ceil((double) dto.getTotal() / dto.getPageSize()));
        dto.setHasMore(dto.getPage() < dto.getTotalPages());

        featuredDTO.setPagination(dto);
        log.info("首页精选视频数据准备完成：{}", featuredDTO);
        return featuredDTO;

    }


    /**
     * 获取剧集详情数据
     *
     * @param dramaId
     * @return
     */
    @Override
    public HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId) {
        if (ragRelease.enabled()) return ragViews.episodes(dramaId);
        log.info("开始获取短剧所有数据,dramaId:{}", dramaId);
        //查询剧集详情数据
        HomeDramaEpisodesDTO homeDramaEpisodesDTO = new HomeDramaEpisodesDTO();
        //剧集id
        homeDramaEpisodesDTO.setDramaId(dramaId.toString());


        DramasVo dramasVo = iDramasService.queryById(dramaId);
        //查询DramaInfoDTO并且封装返回
        HomeDramaEpisodesDTO.DramaInfoDTO dramaInfoDTO = extracted(dramaId, dramasVo);
        homeDramaEpisodesDTO.setDramaInfo(dramaInfoDTO);

        //查询剧集数据
        LambdaQueryWrapper<Episodes> eq = Wrappers.lambdaQuery(Episodes.class).eq(Episodes::getDramaId, dramaId);
        List<Episodes> episodesList = episodesMapper.selectList(eq);
        List<HomeDramaEpisodesDTO.EpisodesDTO> episodesDTOList = episodesList.stream().map(episodes -> {

            HomeDramaEpisodesDTO.EpisodesDTO episodesDTO = new HomeDramaEpisodesDTO.EpisodesDTO();
            episodesDTO.setCid(dramaId.toString());
            episodesDTO.setEpisode(episodes.getEpisodeId().toString());
            episodesDTO.setTitle("第" + episodes.getEpisodeNumber() + "集");
            episodesDTO.setDramaTitle(episodes.getTitle());
            episodesDTO.setDescription(episodes.getDescription());
            episodesDTO.setDuration(episodes.getDuration().toString());
            //TODO 从互动服务获取点赞量
            episodesDTO.setLikeCount(0);
            //TODO 从互动服务获取收藏量
            episodesDTO.setFavoriteCount(0);
            //TODO 从互动服务获取播放量
            episodesDTO.setViewCount(0);
            //TODO 从互动服务获取评论量
            episodesDTO.setCommentCount(0);
            //TODO 从互动服务获取是否喜欢和收藏
            episodesDTO.setIsLiked(false);
            episodesDTO.setIsFavorited(false);
            //
            episodesDTO.setStatus(episodes.getStatus().toString());
            episodesDTO.setIsVip(episodes.getIsFree() == 0);
            String releaseDate = DateUtils.format(dramasVo.getReleaseDate());
            episodesDTO.setReleaseDate(releaseDate);
            episodesDTO.setDramaId(dramaId.toString());
            //TODO 从播放服务获取进度
            episodesDTO.setProgress(0);
            //TODO 从播放服务获取最后播放时间
            episodesDTO.setLastWatchTime("");
            episodesDTO.setRating(0.0D);
            episodesDTO.setRatingCount(0);
            episodesDTO.setIsWatched(false);
            episodesDTO.setIsCurrentWatch(false);
            episodesDTO.setCover(dramasVo.getCover());
            String quality = "720";
            if ("720".equals(quality)) {
                episodesDTO.setVideoUrl(episodes.getVideoUrlHd());
            } else {
                episodesDTO.setVideoUrl(episodes.getVideoUrlLow());
            }

            return episodesDTO;
        }).toList();
        //封装剧集数据
        homeDramaEpisodesDTO.setEpisodes(episodesDTOList);
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setTotal(episodesDTOList.size());
        statisticsDTO.setAvailableCount(episodesDTOList.size());
        statisticsDTO.setComingCount(0);
        statisticsDTO.setWatchedCount(0);
        statisticsDTO.setUnwatchedCount(0);

        homeDramaEpisodesDTO.setStatistics(statisticsDTO);
        homeDramaEpisodesDTO.setSort("asc");
        homeDramaEpisodesDTO.setStatus("all");
        log.info("获取成功，正在返回剧集详情数据:{}", homeDramaEpisodesDTO);
        return homeDramaEpisodesDTO;
    }

    @Override
    public HomeDramaInfoDTO getDramaInfo(Long dramaId) {
        if (ragRelease.enabled()) return ragViews.info(dramaId);
        HomeDramaInfoDTO homeDramaInfoDTO = new HomeDramaInfoDTO();
        DramasVo dramasVo = iDramasService.queryById(dramaId);
        homeDramaInfoDTO.setCover(dramasVo.getCover());
        homeDramaInfoDTO.setTitle(dramasVo.getTitle());
        homeDramaInfoDTO.setViewCount(0);
        homeDramaInfoDTO.setLikeCount(0);
        //通过短剧演员关联表查询
        List<ActorRoleInfoEntity> dramaActorsInfo = dramaActorsMapper.getDramaActorsInfo(dramaId);
        List<HomeDramaInfoDTO.ActorsDTO> actorsDTOList = dramaActorsInfo.stream().map(actorRoleInfoEntity -> {
            HomeDramaInfoDTO.ActorsDTO actorsDTO = new HomeDramaInfoDTO.ActorsDTO();
            actorsDTO.setName(actorRoleInfoEntity.getActorName());
            actorsDTO.setAvatar(actorRoleInfoEntity.getAvatar());
            actorsDTO.setRole(actorRoleInfoEntity.getRoleName());
            return actorsDTO;
        }).toList();
        homeDramaInfoDTO.setActors(actorsDTOList);
        homeDramaInfoDTO.setDescription(dramasVo.getDescription());
        homeDramaInfoDTO.setTotalEpisodeCount(dramasVo.getTotalEpisodes().intValue());
        log.info("获取短剧详细数据成功...{}", homeDramaInfoDTO);
        return homeDramaInfoDTO;
    }


    /**
     * 封装剧集详情数据
     *
     * @param dramaId
     * @param dramasVo
     * @return
     */
    private HomeDramaEpisodesDTO.DramaInfoDTO extracted(Long dramaId, DramasVo dramasVo) {
        HomeDramaEpisodesDTO.DramaInfoDTO dramaInfoDTO = new HomeDramaEpisodesDTO.DramaInfoDTO();
        dramaInfoDTO.setId(dramasVo.getDramaId().toString());
        dramaInfoDTO.setTitle(dramasVo.getTitle());
        dramaInfoDTO.setCover(dramasVo.getCover());
        dramaInfoDTO.setTotalEpisodes(dramasVo.getTotalEpisodes().intValue());
        dramaInfoDTO.setCurrentEpisode(dramasVo.getCurrentEpisodes().intValue());
        dramaInfoDTO.setCurrentWatchEpisode(0);
        dramaInfoDTO.setIsCompleted(false);
        //TODO 通过互动服务获取该用户是否在追该剧
        dramaInfoDTO.setIsFollowing(false);
        List<Tags> dramaTagsByDramaId = dramaTagsService.getDramaTagsByDramaId(dramaId);
        List<String> tagList = dramaTagsByDramaId.stream().map(Tags::getName).toList();
        dramaInfoDTO.setTags(tagList);
        dramaInfoDTO.setDescription(dramasVo.getDescription());
        return dramaInfoDTO;
    }
}
