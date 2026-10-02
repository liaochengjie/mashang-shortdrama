package com.lfy.kcat.workflow.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author liaochengjie
 */
@Component
@Order(-100)
public class RagProcessIdentityFilter extends OncePerRequestFilter {
    @org.springframework.beans.factory.annotation.Autowired private org.camunda.bpm.engine.ExternalTaskService externalTasks;
    @org.springframework.beans.factory.annotation.Autowired private org.camunda.bpm.engine.RepositoryService repository;
    private final com.fasterxml.jackson.databind.ObjectMapper json=new com.fasterxml.jackson.databind.ObjectMapper();
    @Value("${rag.internal-token:}") private String internalToken;
    @Value("${rag.worker-token:}") private String workerToken;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String path = req.getRequestURI();
        boolean internal = path.startsWith("/internal/rag/");
        boolean external = path.startsWith("/engine-rest/external-task");
        if (internal || external) {
            String secret = internal ? internalToken : workerToken;
            String supplied = req.getHeader("Authorization");
            if (secret.isBlank() || supplied == null || !MessageDigest.isEqual(("Bearer " + secret).getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8))) {
                res.setStatus(401); res.setContentType("application/json"); res.getWriter().write("{\"error\":\"UNAUTHORIZED\"}"); return;
            }
        }
        if (external) {
            if (!"POST".equals(req.getMethod())) { res.sendError(403);return; }
            byte[] body=req.getInputStream().readNBytes(16385);
            if (body.length>16384) { res.sendError(413);return; }
            com.fasterxml.jackson.databind.JsonNode data;
            try { data=json.readTree(body); } catch (Exception e) { res.sendError(400);return; }
            if (data==null || !data.isObject()) { res.sendError(400);return; }
            String worker=data.path("workerId").asText();
            if (!worker.matches("rag-[a-f0-9-]{36}")) { res.sendError(403);return; }
            if (path.equals("/engine-rest/external-task/fetchAndLock")) {
                var topics=data.path("topics");
                if (!topics.isArray() || topics.size()!=1 || !"drama-rag-index".equals(topics.get(0).path("topicName").asText()) || !"DramaAuthProcessV2".equals(topics.get(0).path("processDefinitionKey").asText()) || data.path("maxTasks").asInt()>5) { res.sendError(403);return; }
            } else {
                var match=java.util.regex.Pattern.compile("/engine-rest/external-task/([a-zA-Z0-9-]+)/(extendLock|complete|failure)").matcher(path);
                if (!match.matches()) { res.sendError(403);return; }
                var task=externalTasks.createExternalTaskQuery().externalTaskId(match.group(1)).singleResult();
                if (task==null) { res.sendError(404);return; }
                var definition=repository.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
                if (!"drama-rag-index".equals(task.getTopicName()) || !worker.equals(task.getWorkerId()) || definition==null || !"DramaAuthProcessV2".equals(definition.getKey())) { res.sendError(403);return; }
            }
            req=new HttpServletRequestWrapper(req) {
                @Override public ServletInputStream getInputStream() {
                    var input=new java.io.ByteArrayInputStream(body);
                    return new ServletInputStream() {
                        public int read() { return input.read(); }
                        public boolean isFinished() { return input.available()==0; }
                        public boolean isReady() { return true; }
                        public void setReadListener(ReadListener listener) { throw new UnsupportedOperationException(); }
                    };
                }
                @Override public java.io.BufferedReader getReader() { return new java.io.BufferedReader(new java.io.InputStreamReader(getInputStream(),StandardCharsets.UTF_8)); }
            };
        }
        chain.doFilter(req,res);
    }
}
