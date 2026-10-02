package com.lfy.kcat.content.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Internal calls use RagIdentityFilter; public/admin routes still require gateway identity.
 * @author liaochengjie
 */
@Configuration
public class RagGatewayIdentityConfiguration {
    @Bean
    public static BeanPostProcessor ragInternalGatewayExclusion() {
        return new BeanPostProcessor() {
            @Override public Object postProcessAfterInitialization(Object bean,String name) {
                // This shared filter is optional in content-service's dependency graph.
                if ("getSaServletFilter".equals(name)) {
                    try { bean.getClass().getMethod("addExclude",String[].class).invoke(bean,(Object)new String[]{"/internal/rag/**"}); }
                    catch (ReflectiveOperationException e) { throw new org.springframework.beans.factory.BeanInitializationException("RAG_INTERNAL_GATEWAY_EXCLUSION_FAILED",e); }
                }
                return bean;
            }
        };
    }
}
