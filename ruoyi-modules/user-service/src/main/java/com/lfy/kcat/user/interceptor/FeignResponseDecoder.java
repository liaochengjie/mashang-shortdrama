package com.lfy.kcat.user.interceptor;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 自定义Feign响应解码器
 */
@Component
@Slf4j
public class FeignResponseDecoder extends SpringDecoder {
    public FeignResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }


    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        log.info("Feign响应解码器开始解码,response:{}", response);
        Object decode = super.decode(response, type);
        if (decode instanceof R<?>){
            //将decode转换为R<?>对象
            R<?> r = (R<?>) decode;
            if(r.getCode()!=200){
                throw new ServiceException(r.getMsg(),r.getCode());
            }

        }
        log.info("Feign响应解码器解码完成,结果:{}", decode);
        return decode;

    }
}
