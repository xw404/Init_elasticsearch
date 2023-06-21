package com.example.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 小吴
 * @Date 2023/04/12 11:39
 * @Version 1.0
 */
@Configuration      //相当于xml开发的配置文件

public class ElasticsearchConfig {

    //注入bean对象
    @Bean
    public RestHighLevelClient  restHighLevelClient()  {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.101.143",9200,"http"))
        );
        return client;
    }
}
