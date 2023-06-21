package com.example;
import com.alibaba.fastjson.JSON;
import com.example.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//Api  讲解（7.6.X高级客户端）（但是此处使用的是7.12.1）
@SpringBootTest
class EsApiApplicationTests {

    @Resource
    @Qualifier("restHighLevelClient")   //如果自动注入不是方法名（类名首字母小小写）。可以用此注解指定对应的方法名
    private RestHighLevelClient client;

    //测试索引的创建Request
    @Test
    void testCreateIndex() throws IOException {
        //1 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("jd_goods");
        //2 执行创建请求  获得响应
        CreateIndexResponse createIndexResponse = client
                .indices()
                .create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }
    //测试获取索引  （只能判断其存不存在）
    @Test
    void testExitIndex() throws IOException{
        //1 得到索引请求
        GetIndexRequest request = new GetIndexRequest("wu_index");
        boolean exists = client
                .indices()
                .exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException{
        //1 得到索引请求
        DeleteIndexRequest request = new DeleteIndexRequest("wu_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());   //是否删除成功
    }
    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("xiaowu", 10);
        //创建请求
        IndexRequest request = new IndexRequest("wu_index");
        //设置规则  put/wu_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        //将我们的数据放入请求   json
        request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求,获取响应的结果
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }
    //获取文档，判断是否存在get/index/_doc/1
    @Test
    void testIsExists() throws Exception {
        GetRequest getRequest = new GetRequest("wu_index" ,"1");
        //不获取返回的——source的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //获取文档的信息
    @Test
    void testGetDocument() throws Exception {
        GetRequest getRequest = new GetRequest("wu_index" ,"1");

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = getResponse.getSourceAsString();

        //返回的api和文档操作一样
        System.out.println(sourceAsString);
        System.out.println(getResponse);
    }
    //更新文档内容
    @Test
    void testUpdateRequest() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest("wu_index", "1");
        updateRequest.timeout("1s");
        User user = new User("校长", 33);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = client.update(updateRequest,RequestOptions.DEFAULT);
        System.out.println(update);
    }
    //删除文档记录
    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("wu_index", "1");
        deleteRequest.timeout("1s");
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    //批量插入数据
    @Test
    void testBulkRequest() throws IOException{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("5s");
        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(new User("小吴1",1));
        userArrayList.add(new User("小吴2",2));
        userArrayList.add(new User("小吴3",3));
        userArrayList.add(new User("小吴4",4));
        //批处理请求
        for (int i = 0; i < userArrayList.size(); i++) {
            //批量更新和批量删除，修改此处的请求
            bulkRequest.add(
                    new IndexRequest("wu_index")
                            .id(""+(i+1))
                            .source(JSON.toJSONString(userArrayList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkItemResponses.hasFailures());  //是否成功
    }

    //查询
    @Test
    void testSearch() throws IOException{
        SearchRequest searchRequest = new SearchRequest("wu_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件  我们可以使用QueryBuilders快速构建
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "小吴1");
        sourceBuilder.query(termQueryBuilder);
        //分页
//        sourceBuilder.from();
//        sourceBuilder.size();
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //数据封装在 response.getHits()中
        System.out.println(JSON.toJSONString(response.getHits()));
        System.out.println("================================");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
