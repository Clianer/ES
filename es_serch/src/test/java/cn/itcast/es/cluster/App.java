package cn.itcast.es.cluster;

import cn.itcast.domain.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class App {

    private TransportClient tc;

    // 执行junit方法之前会先执行@Before注解修饰的方法。一般用于初始化
    @Before
    public void init() throws UnknownHostException {
        // 配置对象，指定集群名称
        Settings settings = Settings.builder().put("cluster.name","cluster-es").build();
        // 创建客户端传输对象
        tc = new PreBuiltTransportClient(settings);
        // 设置连接的es集群服务器地址、对应的通讯端口
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9301));
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9302));
    }
    // 执行junit方法之后会执行@After注解修饰的方法。一般用于释放回收资源
    @After
    public void release()  {
        tc.close();
    }


    // 集群环境： 创建索引
    @Test
    public void createIndex() throws UnknownHostException {
        // 创建索引
        tc.admin().indices().prepareCreate("blog3").get();
    }

    // 集群环境： 添加文档
    @Test
    public void createDocument() throws Exception {
        // 对象
        Book book = new Book();
        book.setId(1);
        book.setBookName("JAVA大全");
        book.setPrice(125F);
        book.setPic("1.jpg");
        book.setBookDesc("很屌的一本书");

        // 对象转json
        String jsonString = new ObjectMapper().writeValueAsString(book);

        // 创建文档
        tc.prepareIndex("blog3","article").setSource(jsonString, XContentType.JSON).get();
    }

    // 集群环境： 查询数据
    @Test
    public void search(){
        SearchRequestBuilder searchRequestBuilder = tc.prepareSearch("blog3").setTypes("article");
        searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            System.out.println(hit.getSourceAsString());
        }
    }
}
