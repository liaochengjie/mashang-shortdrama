package com.lfy.kcat.user.config;

import com.lfy.kcat.user.controller.RagAppController;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

/**
 * @author liaochengjie
 */
@RestControllerAdvice(assignableTypes=RagAppController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RagStatusAdvice {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> status(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(Map.of("error",error.getReason()==null?"REQUEST_REJECTED":error.getReason()));
    }
}
