package com.lfy.kcat.content.biz;


import com.lfy.kcat.content.vo.ManualAuthTaskVo;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;

public interface AuthCheckService {
    String getProcessIdByDramaId(String dramaId);

    Integer getAiCheckStatus(String dramaId);

    void saveManualAuthData(ManualAuthTaskVo manualAuthTaskVo, String authorization);

    void completeAuthUpdateDb(DramaAuthCompleteDTO dramaAuthCompleteDTO);
}
