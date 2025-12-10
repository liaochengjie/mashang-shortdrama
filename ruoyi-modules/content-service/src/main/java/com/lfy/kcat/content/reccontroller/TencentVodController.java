package com.lfy.kcat.content.reccontroller;

import com.lfy.kcat.content.biz.TencentVodService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tencent")
public class TencentVodController {

    @Autowired
    TencentVodService tencentVodService;

    @PostMapping("/vod/translate")
    public R tencentVodTranslator(@RequestBody DramaAuthCompleteDTO dramaAuthCompleteDTO) {
        tencentVodService.uploadDrama(dramaAuthCompleteDTO.getDramaId());


        return R.ok();
    }
}
