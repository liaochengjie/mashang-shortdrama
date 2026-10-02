package com.lfy.kcat.content.biz.impl;

import com.lfy.kcat.content.biz.RagPublishedViews;
import com.lfy.kcat.content.biz.RagReleaseService;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * @author liaochengjie
 */
@Service
@RequiredArgsConstructor
public class RagPublishedViewsImpl implements RagPublishedViews {
    private final RagReleaseService release;
    private final JdbcTemplate jdbc;

    public org.dromara.common.core.dto.home.HomeFeaturedDTO featured(org.dromara.common.core.dto.PageReqDTO req) {
        int page=Math.max(1,req.getPage()),size=Math.max(1,Math.min(30,req.getPageSize()));
        String where=" FROM kcat_rag_release r JOIN dramas d ON d.drama_id=r.drama_id WHERE r.published_snapshot_id IS NOT NULL AND r.deleted=0 AND r.desired_on_shelf=1 AND d.status=1 AND d.audit_status=1";
        List<Long> ids=jdbc.queryForList("SELECT r.drama_id"+where+" ORDER BY r.drama_id DESC LIMIT ? OFFSET ?",Long.class,size,(page-1)*size);
        var view=new org.dromara.common.core.dto.home.HomeFeaturedDTO();
        var pagination=new org.dromara.common.core.dto.home.HomeFeaturedDTO.PaginationDTO();
        int total=jdbc.queryForObject("SELECT COUNT(*)"+where,Integer.class);
        pagination.setPage(page);pagination.setPageSize(size);pagination.setTotal(total);pagination.setTotalPages((total+size-1)/size);pagination.setHasMore(page*size<total);view.setPagination(pagination);
        view.setEpisodes(ids.stream().map(id -> {
            Map<String,Object> facts=release.published(id);
            var first=episodes(id).getEpisodes().getFirst();
            var dto=new org.dromara.common.core.dto.home.HomeFeaturedDTO.EpisodesDTO();
            dto.setEpisode(first.getEpisode());dto.setTitle(first.getTitle());dto.setDramaTitle(facts.get("title").toString());
            dto.setDescription(first.getDescription());dto.setCover(first.getCover());dto.setVideoUrl(first.getVideoUrl().toString());dto.setDuration(first.getDuration());
            dto.setLikeCount(0);dto.setFavoriteCount(0);dto.setCommentCount(0);dto.setViewCount(0);dto.setIsLiked(false);dto.setIsFavorited(false);dto.setRatingCount(0);
            var drama=new org.dromara.common.core.dto.home.HomeFeaturedDTO.EpisodesDTO.DramaDTO();
            drama.setId(id.toString());drama.setTitle(facts.get("title").toString());drama.setCover(facts.get("cover").toString());drama.setTags((List<String>)facts.get("tags"));
            drama.setActors(((List<Map<String,Object>>)facts.get("actors")).stream().map(a -> {
                var actor=new org.dromara.common.core.dto.home.HomeFeaturedDTO.EpisodesDTO.DramaDTO.ActorsDTO();
                actor.setName(a.get("name").toString());actor.setRole(a.get("role").toString());actor.setAvatar(a.get("avatar").toString());return actor;
            }).toList());dto.setDrama(drama);return dto;
        }).toList());return view;
    }

    public HomeDramaInfoDTO info(Long dramaId) {
        Map<String,Object> facts = release.published(dramaId);
        HomeDramaInfoDTO info = new HomeDramaInfoDTO();
        info.setTitle(facts.get("title").toString()); info.setCover(facts.get("cover").toString());
        info.setDescription(facts.get("description").toString());
        info.setTotalEpisodeCount(((List<?>)facts.get("episodes")).size()); info.setViewCount(0); info.setLikeCount(0);
        List<Map<String,Object>> actors = (List<Map<String,Object>>)facts.get("actors");
        info.setActors(actors.stream().map(a -> {
            HomeDramaInfoDTO.ActorsDTO dto = new HomeDramaInfoDTO.ActorsDTO();
            dto.setName(a.get("name").toString()); dto.setRole(a.get("role").toString()); dto.setAvatar(a.get("avatar").toString()); return dto;
        }).toList());
        return info;
    }

    public HomeDramaEpisodesDTO episodes(Long dramaId) {
        Map<String,Object> facts = release.published(dramaId);
        HomeDramaEpisodesDTO view = new HomeDramaEpisodesDTO();
        view.setDramaId(dramaId.toString()); view.setSourceVersion(((Number)facts.get("sourceVersion")).longValue());
        view.setBuildId(facts.get("buildId").toString()); view.setEmbeddingProfile(facts.get("embeddingProfile").toString());
        HomeDramaEpisodesDTO.DramaInfoDTO info = new HomeDramaEpisodesDTO.DramaInfoDTO();
        info.setId(dramaId.toString()); info.setTitle(facts.get("title").toString()); info.setCover(facts.get("cover").toString());
        info.setDescription(facts.get("description").toString()); info.setTags((List<String>)facts.get("tags"));
        List<Map<String,Object>> eps = (List<Map<String,Object>>)facts.get("episodes");
        info.setTotalEpisodes(eps.size()); info.setCurrentEpisode(eps.size()); info.setCurrentWatchEpisode(0); info.setIsFollowing(false); info.setIsCompleted(true);
        view.setDramaInfo(info);
        view.setEpisodes(eps.stream().map(e -> {
            HomeDramaEpisodesDTO.EpisodesDTO dto = new HomeDramaEpisodesDTO.EpisodesDTO();
            String episodeId=e.get("episodeId").toString();
            Map<String,Object> media=(Map<String,Object>)e.get("media");
            dto.setEpisode(episodeId); dto.setDramaId(dramaId.toString()); dto.setCid(dramaId.toString());
            dto.setTitle("第"+e.get("episodeNumber")+"集"); dto.setDramaTitle(facts.get("title").toString());
            dto.setDescription(e.get("description").toString()); dto.setDuration(String.valueOf(e.get("duration")));
            dto.setCover(facts.get("cover").toString()); dto.setEpisodeNumber(((Number)e.get("episodeNumber")).intValue());
            dto.setMediaIdentity(media.get("identity").toString());
            dto.setVideoUrl(jdbc.queryForObject("SELECT output_url FROM kcat_rag_media WHERE snapshot_id=? AND episode_id=? AND media_identity=? AND state='READY'", String.class,facts.get("snapshotId"),episodeId,media.get("identity")));
            dto.setStatus("1"); dto.setIsVip(false); dto.setLikeCount(0); dto.setFavoriteCount(0); dto.setCommentCount(0); dto.setViewCount(0);
            dto.setIsLiked(false); dto.setIsFavorited(false); dto.setProgress(0); return dto;
        }).toList());
        HomeDramaEpisodesDTO.StatisticsDTO stats = new HomeDramaEpisodesDTO.StatisticsDTO();
        stats.setTotal(eps.size()); stats.setAvailableCount(eps.size()); stats.setComingCount(0); stats.setWatchedCount(0); stats.setUnwatchedCount(eps.size());
        view.setStatistics(stats); view.setSort("asc"); view.setStatus("all"); return view;
    }
}
