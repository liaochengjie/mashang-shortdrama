package com.lfy.kcat.content.biz;

public interface TencentVodService {


    void vodTranslator(Long dramaId);
    /**
     * 转码指定短剧的所有信息（预告片，剧集数据）
     * @param dramaId
     */
    void vodInfoFlowTranslator(Long dramaId);

    /**
     * 画质流转码服务
     * @param dramaId
     */
    void vodQualityTranslator(Long dramaId);


}
