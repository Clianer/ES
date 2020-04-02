package cn.itcast.es.index;

import cn.itcast.domain.Book;
import cn.itcast.mapper.BookMapper;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;


/**
 * 创建索引：blog1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-dao.xml")
public class App01_index_create {

    @Autowired
    private BookMapper bookMapper;

    @Test
    public void createIndex() throws Exception {
        TransportClient tc = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "elasticsearch").build());
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        tc.admin().indices().prepareCreate("blog1").get();
        tc.close();
    }

    @Test
    public void App02_doc_create() throws IOException {
        //采集数据
        //调用数据库
        List<Book> bookList = bookMapper.selectAll();
        //存储信息
        //连接ES
        TransportClient tc = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "elasticsearch").build());
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        /*XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id","1")
                .field("title","elasticsearch搜索服务")
                .field("content","ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎。")
                .endObject();*/
        /*HashMap<String, String> map = new HashMap<>();
        map.put("id","2");
        map.put("title","elasticsearch搜索服务");
        map.put("content","牛批");*/
        for (Book book : bookList) {
            String json = new ObjectMapper().writeValueAsString(book);
            tc.prepareIndex("blog1", "article", book.getId() + "").setSource(json, XContentType.JSON).get();
        }
        tc.close();
    }

    @Test
    public void App02_doc_serch() throws IOException {
        TransportClient tc = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "elasticsearch").build());
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        SearchRequestBuilder searchRequestBuilder = tc.prepareSearch("blog1");
        //全文搜索
        //searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        //全文搜索：带条件，会分词
        //searchRequestBuilder.setQuery(QueryBuilders.queryStringQuery("解决方案"));
        //全文搜索：带条件，不会分词
        searchRequestBuilder.setQuery(QueryBuilders.termQuery("bookDesc", "java"));
        //通过ID搜索
        //searchRequestBuilder.setQuery(QueryBuilders.idsQuery().addIds("1","2"));
        //范围查询
        //searchRequestBuilder.setQuery(QueryBuilders.rangeQuery("id").from("1").to("3"));
        //排序，分页
        //1、设置条件
        //这个搜索只有匹配才会有结果
        /*searchRequestBuilder.setQuery(QueryBuilders.matchQuery("bookDesc","java"));
        int pageNum = 1;  // 查询第1页
        int pageSize = 2; // 返回2条
        //设置第几页
        searchRequestBuilder.setFrom((pageNum - 1) * pageSize);
        //设置每页大小
        searchRequestBuilder.setSize(pageSize);
        //排序
        searchRequestBuilder.addSort("id", SortOrder.ASC);*/
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("bookDesc")
                .field("bookName")
                .preTags("<font color=red>")
                .postTags("</font>");
        searchRequestBuilder.highlighter(highlightBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("总共有" + searchHits.getTotalHits());
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
/*            System.out.println("============华丽的分割线============");
            String json = hit.getSourceAsString();
            System.out.println("获取文档数据，是一个json格式：" + json);*/
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String id = sourceAsMap.get("id").toString();
            String bookName = "";
            String price = sourceAsMap.get("price").toString();
            String pic = sourceAsMap.get("pic").toString();
            String bookDesc = "";
            System.out.println("============高亮====================");
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField bookNameField = highlightFields.get("bookName");
            if (bookNameField != null) {
                bookName = bookNameField.getFragments()[0].toString();
            } else {
                bookName = sourceAsMap.get("bookName").toString();
            }
            HighlightField bookDescField = highlightFields.get("bookDesc");
            if (bookDescField != null) {
                bookDesc = bookDescField.getFragments()[0].toString();
            } else {
                bookDesc = sourceAsMap.get("bookDesc").toString();
            }
            System.out.println("id = " + id);
            System.out.println("bookName = " + bookName);
            System.out.println("price = " + price);
            System.out.println("pic = " + pic);
            System.out.println("bookDesc = " + bookDesc);
        }
        tc.close();
    }
}
