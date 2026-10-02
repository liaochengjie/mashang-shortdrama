package com.lfy.kcat.workflow.biz.impl;

import com.lfy.kcat.workflow.biz.RagProcessService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * @author liaochengjie
 */
@Service
@RequiredArgsConstructor
public class RagProcessServiceImpl implements RagProcessService {
    private final RuntimeService runtime;
    private final TaskService tasks;
    private final JdbcTemplate jdbc;
    private final org.camunda.bpm.engine.ExternalTaskService externalTasks;

    @Override
    public Map<String,Object> retryExternal(String taskId) {
        var task=externalTasks.createExternalTaskQuery().externalTaskId(taskId).singleResult();
        if (task==null || !"drama-rag-index".equals(task.getTopicName())) throw conflict("EXTERNAL_TASK_NOT_FOUND_IN_RAG_TOPIC");
        externalTasks.setRetries(taskId,3);
        return Map.of("state","RETRIES_RESTORED","retries",3);
    }

    @Override
    @Transactional
    public Map<String,Object> start(Map<String,Object> variables) {
        String snapshotId = variables.get("snapshotId").toString();
        String key = "drama:" + variables.get("dramaId") + ":" + variables.get("sourceVersion");
        jdbc.update("INSERT IGNORE INTO kcat_rag_process_start(business_key,snapshot_id) VALUES(?,?)", key, snapshotId);
        Map<String,Object> prior = jdbc.queryForMap("SELECT * FROM kcat_rag_process_start WHERE business_key=? FOR UPDATE", key);
        if (!snapshotId.equals(prior.get("snapshot_id"))) throw conflict("BUSINESS_KEY_CONFLICT");
        String processId = (String)prior.get("process_id");
        if (processId == null) {
            processId = runtime.startProcessInstanceByKey("DramaAuthProcessV2", key, variables).getId();
            jdbc.update("UPDATE kcat_rag_process_start SET process_id=? WHERE business_key=?", processId, key);
        }
        return Map.of("processId", processId);
    }

    @Override
    @Transactional
    public Map<String,Object> decide(Map<String,Object> decision) {
        String snapshotId = decision.get("snapshotId").toString();
        Map<String,Object> row = jdbc.queryForMap("SELECT * FROM kcat_rag_process_start WHERE snapshot_id=? FOR UPDATE", snapshotId);
        String state = Boolean.TRUE.equals(decision.get("approve")) ? "APPROVED" : "REJECTED";
        if (row.get("decision_state") != null) {
            if (!state.equals(row.get("decision_state"))) throw conflict("DECISION_CONFLICT");
            return Map.of("state", state);
        }
        if (!row.get("process_id").equals(decision.get("processId"))) throw conflict("PROCESS_VERSION_CONFLICT");
        Task task = tasks.createTaskQuery().processInstanceId(row.get("process_id").toString()).taskDefinitionKey("HumanReview").singleResult();
        if (task == null) throw conflict("HUMAN_TASK_NOT_ACTIVE");
        tasks.setAssignee(task.getId(), decision.get("userName").toString());
        tasks.complete(task.getId(), Map.of("approve", Boolean.TRUE.equals(decision.get("approve")), "auditReason", decision.get("auditReason")));
        jdbc.update("UPDATE kcat_rag_process_start SET decision_state=? WHERE snapshot_id=?", state, snapshotId);
        return Map.of("state", state);
    }

    private ResponseStatusException conflict(String code) { return new ResponseStatusException(HttpStatus.CONFLICT, code); }
}
