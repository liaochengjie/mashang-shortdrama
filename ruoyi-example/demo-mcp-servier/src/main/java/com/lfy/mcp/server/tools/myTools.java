package com.lfy.mcp.server.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.text.SimpleDateFormat;
import java.util.Date;

public class myTools {
    @Tool(description = "获取当前时间")
    String getCurrentTime(){
        Date date = new Date();
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return format;
    }

    @Tool(description = "获取城市天气")
    String getCityWeather(@ToolParam(required = false,description = "城市名字") String city){
        return "晴天";
    }
}
