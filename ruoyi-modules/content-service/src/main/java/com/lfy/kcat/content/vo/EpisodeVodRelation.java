package com.lfy.kcat.content.vo;

import lombok.Data;

@Data
public class EpisodeVodRelation {
    //剧集ID
    String epiId;

    //短剧上传文件的Id
    String fileId;

    //高清url
    String hdUrl;

    //标清url
    String sdUrl;

    //低清url
    String lowUrl;

}
