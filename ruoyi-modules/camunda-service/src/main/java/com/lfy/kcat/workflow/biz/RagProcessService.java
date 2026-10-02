package com.lfy.kcat.workflow.biz;

import java.util.Map;

/**
 * 版本化审核流程的启动、审批及外部任务恢复。
 *
 * @author liaochengjie
 */
public interface RagProcessService {
    Map<String,Object> start(Map<String,Object> variables);

    Map<String,Object> decide(Map<String,Object> decision);

    Map<String,Object> retryExternal(String taskId);
}
