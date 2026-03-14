package org.dromara.common.core.dto.home;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class HomeFeaturedDTO {

    private List<EpisodesDTO> episodes;
    private PaginationDTO pagination;

    @NoArgsConstructor
    @Data
    public static class PaginationDTO {
        // 当前页码
        private Integer page;
        // 每页数量
        private Integer pageSize;
        // 总记录数
        private Integer total;
        // 总页数
        private Integer totalPages;
        // 是否有更多数据
        private Boolean hasMore;
    }

    @NoArgsConstructor
    @Data
    public static class EpisodesDTO {

        private String episode;
        private String title;
        private String dramaTitle;
        private String description;
        private String duration;
        private Integer likeCount;
        private Integer favoriteCount;
        private Integer commentCount;
        private Integer viewCount;
        private Boolean isLiked;
        private Boolean isFavorited;
        private Integer ratingCount;
        private DramaDTO drama;
        private Integer hotScore;
        private List<String> episodeTags;
        private String releaseDate;
        private Integer comprehensiveScore;
        private Integer progress;
        private String lastWatchTime;
        private Integer recommendScore;
        private Integer freshness;
        private String cover;
        private String videoUrl;
        private Integer rank;
        private String featuredReason;
        private String featuredDate;
        private String addToFeaturedTime;

        @NoArgsConstructor
        @Data
        public static class DramaDTO {
            private String id;
            private String title;
            private String cover;
            private List<String> tags;
            private String category;
            private Boolean isNew;
            private Boolean isPopular;
            private String director;
            private List<ActorsDTO> actors;

            @NoArgsConstructor
            @Data
            public static class ActorsDTO {
                private String name;
                private String role;
                private String avatar;
            }
        }
    }
}
