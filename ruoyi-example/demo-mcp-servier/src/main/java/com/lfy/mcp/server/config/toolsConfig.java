package com.lfy.mcp.server.config;



import com.lfy.mcp.server.tools.myTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.spi.ToolProvider;

@Configuration
public class toolsConfig {

    @Bean
    ToolCallbackProvider toolCallbackProvider() {
        MethodToolCallbackProvider build = MethodToolCallbackProvider.builder()
            .toolObjects(new myTools())
            .build();
        return build;
    }

}
