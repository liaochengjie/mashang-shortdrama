package com.lfy.kcat.content.biz;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提交并确认媒体处理任务。
 *
 * @author liaochengjie
 */
public interface RagMediaService {
    Map<String,Object> start(String id);
    Map<String,Object> status(String id);
    Map<String,Object> retry(String id);
    void poll();
}
