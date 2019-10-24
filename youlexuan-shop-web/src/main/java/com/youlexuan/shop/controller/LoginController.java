package com.youlexuan.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 王大亮
 * @date 2019/9/26 16:24
 */
@RestController
@RequestMapping("login")
public class LoginController {

    /**
     * 获取登录人的名字
     * @return
     */
    @RequestMapping("name")
    public Map name(){
        //获取登录人的姓名  从Spring-Security.xml配置文件中获取
        String name=SecurityContextHolder.getContext().getAuthentication().getName();
        //将登录人名字存入map中返回
        Map map = new HashMap();
        map.put("loginName",name);
        map.put("lastTime",new Date());
        return map;
    }
}
