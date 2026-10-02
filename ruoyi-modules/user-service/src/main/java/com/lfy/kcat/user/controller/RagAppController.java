package com.lfy.kcat.user.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.lfy.kcat.user.business.RagAppService;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 面向 App 的公开情节检索接口。
 *
 * @author liaochengjie
 */
@RestController
@SaIgnore
@RequestMapping("/api")
@RequiredArgsConstructor
public class RagAppController {
    private final RagAppService rag;

    @GetMapping("/search/dramas")
    public R<Map> search(@RequestParam String keyword, @RequestParam(defaultValue="1") int page,
                         @RequestParam(defaultValue="15") int pageSize, @RequestParam(required=false) String sessionId) {
        return R.ok(rag.search(keyword, page, pageSize, sessionId));
    }

    @GetMapping("/rag/dramas/{dramaId}/playback")
    public R<Map> playback(@PathVariable String dramaId, @RequestParam(required=false) Long sourceVersion,
                           @RequestParam(required=false) String buildId, @RequestParam(required=false) String embeddingProfile,
                           @RequestParam(required=false) String episodeId) {
        return R.ok(rag.playback(dramaId, sourceVersion, buildId, embeddingProfile, episodeId));
    }
}
