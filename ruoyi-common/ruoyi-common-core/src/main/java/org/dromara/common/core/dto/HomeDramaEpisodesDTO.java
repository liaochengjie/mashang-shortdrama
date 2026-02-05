package org.dromara.common.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class HomeDramaEpisodesDTO {
    private Integer dramaId;
    private DramaInfoDTO dramaInfo;
    private List<EpisodesDTO> episodes;
    private StatisticsDTO statistics;
    private String sort;
    private String status;

    @NoArgsConstructor
    @Data
    public static class DramaInfoDTO {
        private String id;
        private String title;
        private String cover;
        private Integer totalEpisodes;
        private Integer currentEpisode;
        private Integer currentWatchEpisode;
        private Boolean isCompleted;
        private Boolean isFollowing;
        private List<String> tags;
        private String description;
    }

    @NoArgsConstructor
    @Data
    public static class StatisticsDTO {
        private Integer total;
        private Integer availableCount;
        private Integer comingCount;
        private Integer watchedCount;
        private Integer unwatchedCount;
    }

    @NoArgsConstructor
    @Data
    public static class EpisodesDTO {
        //剧集id
        private String cid;
        private String episode;
        private String title;
        private String dramaTitle;
        private String description;
        private String duration;
        private Integer likeCount;
        private Integer favoriteCount;
        private Integer viewCount;
        private Integer commentCount;
        private Boolean isLiked;
        private Boolean isFavorited;
        private String status;
        private Boolean isVip;
        private String releaseDate;
        private String dramaId;
        private Integer progress;
        private String lastWatchTime;
        private Double rating;
        private Integer ratingCount;
        private Boolean isWatched;
        private Boolean isCurrentWatch;
        private String cover;
        private Object videoUrl;
    }
}
