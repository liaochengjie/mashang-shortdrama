package org.dromara.common.core.enums;

import lombok.Getter;

public enum FeignEnum {

    /**
     * 数据返回异常
     */
    SERVICE_DATA_ERROR(50000, "数据返回异常");
    FeignEnum(Integer code ,String msg){
        this.code = code;
        this.msg = msg;
    }

    @Getter
    private Integer code;
    @Getter
    private String msg;
}
