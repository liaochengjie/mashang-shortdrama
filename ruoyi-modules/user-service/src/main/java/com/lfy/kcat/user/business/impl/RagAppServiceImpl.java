package com.lfy.kcat.user.business.impl;

import com.lfy.kcat.user.business.RagAppService;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liaochengjie
 */
@Service
public class RagAppServiceImpl implements RagAppService {
    @Value("${rag.python-url:http://127.0.0.1:7777}") private String pythonUrl;
    @Value("${rag.content-url:http://127.0.0.1:10001}") private String contentUrl;
    @Value("${rag.internal-token:}") private String token;

    private RestClient client(String url) {
        if (token.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"RAG_NOT_CONFIGURED");
        SimpleClientHttpRequestFactory f=new SimpleClientHttpRequestFactory(); f.setConnectTimeout(5000); f.setReadTimeout(25000);
        return RestClient.builder().baseUrl(url).requestFactory(f).defaultHeader("Authorization","Bearer "+token).build();
    }

    @Override
    public Map search(String keyword, int page,
                         int pageSize, String sessionId) {
        if (keyword.isBlank() || keyword.length()>500 || page<1 || pageSize<1 || pageSize>30 || (page>1 && sessionId==null))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_SEARCH_PARAMETERS");
        Map<String,Object> body=new LinkedHashMap<>(); body.put("query",keyword);body.put("page",page);body.put("pageSize",pageSize);
        if (sessionId!=null) body.put("sessionId",sessionId);
        try { return client(pythonUrl).post().uri("/internal/search/dramas").body(body).retrieve().body(Map.class); }
        catch (RestClientResponseException e) {
            int code=e.getStatusCode().value();
            if (code==410 || code==409 || code==400 || code==422) throw new ResponseStatusException(HttpStatus.valueOf(code),"SEARCH_SESSION_OR_PARAMETERS_INVALID");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"SEARCH_DEPENDENCY_UNAVAILABLE");
        } catch (org.springframework.web.client.ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"SEARCH_DEPENDENCY_UNAVAILABLE");
        }
    }

    @Override
    public Map playback(String dramaId, Long sourceVersion,
                           String buildId, String embeddingProfile,
                           String episodeId) {
        if (!dramaId.matches("[0-9]+")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_DRAMA_ID");
        Map published;
        try { published=client(contentUrl).get().uri("/internal/rag/published/{id}/episodes",dramaId).retrieve().body(Map.class); }
        catch (RestClientResponseException e) {
            if (e.getStatusCode().value()==410) throw new ResponseStatusException(HttpStatus.GONE,"CONTENT_NO_LONGER_AVAILABLE");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"PLAYBACK_VALIDATION_UNAVAILABLE");
        } catch (org.springframework.web.client.ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"PLAYBACK_VALIDATION_UNAVAILABLE");
        }
        if (sourceVersion!=null && (!sourceVersion.toString().equals(published.get("sourceVersion").toString()) ||
            !java.util.Objects.equals(buildId,published.get("buildId")) || !java.util.Objects.equals(embeddingProfile,published.get("embeddingProfile"))))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"PUBLICATION_CHANGED_SEARCH_AGAIN");
        if (episodeId!=null && ((java.util.List<Map>)published.get("episodes")).stream().noneMatch(e -> episodeId.equals(e.get("episode"))))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"EPISODE_NO_LONGER_AVAILABLE");
        return published;
    }
}
