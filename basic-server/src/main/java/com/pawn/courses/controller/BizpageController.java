package com.pawn.courses.controller;

import com.pawn.courses.model.PersonDemo;
import com.pawn.courses.service.MethodELService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller()
public class BizpageController {

    @Resource
    private MethodELService methodELService;
    // 登录
    /*@PostMapping("/login")
    public String index(String username,String password) {
        return "index";
    }*/
    // 登录成功之后的首页
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // 日志管理
    @GetMapping("/syslog")
    public String showOrder() {
        return "syslog";
    }

    // 用户管理
    @GetMapping("/sysuser")
    public String addOrder() {
        return "sysuser";
    }

    // 具体业务一
    @GetMapping("/biz1")
    public String updateOrder() {
//        methodELService.findAll();
//        methodELService.findOne();
/*        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        methodELService.delete(list,null);*/

        List<PersonDemo> personDemos = methodELService.findAllPD();
        System.out.println(personDemos);

        return "biz1";
    }

    // 具体业务二
    @GetMapping("/biz2")
    public String deleteOrder() {
        return "biz2";
    }


}