package com.example.controller;

import com.example.pojo.Content;
import com.example.service.ContentService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author 小吴
 * @Date 2023/04/13 17:26
 * @Version 1.0
 */
//请求编写
@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping(value = "/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keywords) throws IOException {
       return contentService.parseContent(keywords);
    }

    //无高亮
//    @GetMapping(value = "/search/{keyword}/{pageSize}/{pageNo}")
//    public List<Map<String , Object>> search(@PathVariable("pageNo") int pageNo,
//                                              @PathVariable("pageSize") int pageSize,
//                                              @PathVariable("keyword") String keyword) throws IOException {
//        List<Map<String, Object>> list = contentService.searchPage(keyword, pageNo, pageSize);
//        return list;
//    }

    //有高亮
    @GetMapping(value = "/search/{keyword}/{pageSize}/{pageNo}")
    public List<Map<String , Object>> search(@PathVariable("pageNo") int pageNo,
                                              @PathVariable("pageSize") int pageSize,
                                              @PathVariable("keyword") String keyword) throws IOException {
        List<Map<String, Object>> list = contentService.searchPageHighlightBuilder(keyword, pageNo, pageSize);
        return list;
    }



}
