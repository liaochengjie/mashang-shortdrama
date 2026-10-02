package com.lfy.kcat.workflow.Controller;

import com.lfy.kcat.workflow.biz.RagProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 内部版本化流程接口。
 *
 * @author liaochengjie
 */
@RestController
@RequestMapping("/internal/rag")
@RequiredArgsConstructor
public class RagProcessController {
    private final RagProcessService processes;

    @PostMapping("/external-tasks/{taskId}/retry")
    public Map<String,Object> retryExternal(@PathVariable String taskId) {
        return processes.retryExternal(taskId);
    }

    @PostMapping("/processes")
    public Map<String,Object> start(@RequestBody Map<String,Object> variables) {
        return processes.start(variables);
    }

    @PostMapping("/decisions")
    public Map<String,Object> decide(@RequestBody Map<String,Object> decision) {
        return processes.decide(decision);
    }
}
