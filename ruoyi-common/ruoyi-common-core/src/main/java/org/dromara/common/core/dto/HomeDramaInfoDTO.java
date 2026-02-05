package org.dromara.common.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class HomeDramaInfoDTO {
    private String cover;
    private String title;
    private Integer viewCount;
    private Integer likeCount;
    private List<ActorsDTO> actors;
    private String description;
    private Integer totalEpisodeCount;

    @NoArgsConstructor
    @Data
    public static class ActorsDTO {
        private String name;
        private String role;
        private String avatar;
    }
}
