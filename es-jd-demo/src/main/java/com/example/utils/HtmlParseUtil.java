package com.example.utils;

import com.example.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author 小吴
 * @Date 2023/04/12 18:41
 * @Version 1.0
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
    }
    //
    public List<Content> parseJD(String keywords) throws IOException {
        //获得请求  https://search.jd.com/Search?keyword=java
        String url ="https://search.jd.com/Search?keyword="+keywords;
        //解析网页(返回的document就是js页面)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有在js中可以使用的方法，这里都可以使用
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        //获取元素中的内容
        ArrayList<Content> goodsList = new ArrayList<>();
        for (Element el : elements) { //每个li标签
            //关于图特别多的网站，所有的图片都是延迟加载的
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
//            System.out.println(img);
//            System.out.println(price);
//            System.out.println(title);
            Content content = new Content();
            content.setImg(img);
            content.setTitle(title);
            content.setPrice(price);
            goodsList.add(content);
        }

        return goodsList;
    }
}
