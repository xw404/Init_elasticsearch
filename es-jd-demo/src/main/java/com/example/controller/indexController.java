package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author 小吴
 * @Date 2023/04/12 18:01
 * @Version 1.0
 */
@Controller
public class indexController {
    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
