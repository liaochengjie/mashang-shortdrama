package com.lfy.kcat.content.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RagIdentityFilter extends OncePerRequestFilter {
    private final RagProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/internal/rag/")) {
            String supplied = request.getHeader("Authorization");
            String expected = "Bearer " + properties.getInternalToken();
            if (!properties.isEnabled() || properties.getInternalToken().isBlank() || supplied == null ||
                !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8))) {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"UNAUTHORIZED\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
