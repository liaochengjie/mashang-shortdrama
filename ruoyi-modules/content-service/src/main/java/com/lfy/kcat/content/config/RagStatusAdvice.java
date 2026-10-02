package com.lfy.kcat.content.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

/** Preserve HTTP conflicts; the legacy generic handler returns R.fail with HTTP 200.
 * @author liaochengjie
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RagStatusAdvice {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> status(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(Map.of("error",error.getReason()==null?"REQUEST_REJECTED":error.getReason()));
    }
}
