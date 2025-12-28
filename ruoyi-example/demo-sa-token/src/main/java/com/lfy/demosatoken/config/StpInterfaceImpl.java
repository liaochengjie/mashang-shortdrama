package com.lfy.demosatoken.config;

import cn.dev33.satoken.stp.StpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    /**
     * 获取该用户的所有权限
     * @param o
     * @param s
     * @return
     */
    @Override
    public List<String> getPermissionList(Object o, String s) {
        log.info("正在进行权限的列表的查询，改查询为懒查询，调用时才会查，查完存入缓存区");
        List<String> list = new ArrayList<>();
        return list;
    }

    /**
     * 用户列表查询
     * @param o
     * @param s
     * @return
     */
    @Override
    public List<String> getRoleList(Object o, String s) {
        log.info("正在进行用户列表查询");
        List<String> list = new ArrayList<>();
        list.add("*");
        return list;
    }
}
