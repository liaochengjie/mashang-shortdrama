package com.lfy.demosatoken.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perm")
public class PermissionTestController {

    @SaCheckLogin
    @GetMapping("/useradd")
    public String userAdd(){
        boolean b = StpUtil.hasPermission("user.add");
        if(b){
            return "用户添加成功";
        }else {
            return "您不具备改权限，无法进行添加操作";
        }
    }

    @GetMapping("/userdelete")
    public String userDelete(){
        boolean b = StpUtil.hasPermission("user.delete");
        if(b){
            return "用户删除成功";
        }else {
            return "您不具备该权限，无法进行删除操作";
        }
    }
}
