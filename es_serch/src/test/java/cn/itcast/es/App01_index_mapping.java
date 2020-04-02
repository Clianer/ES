package cn.itcast.es;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;

public class App01_index_mapping {
    /**
     * 目标: 创建映射
     * 1、创建索引、映射。
     * 2、先创建索引，再创建映射。
     */
    @Test
    public void createIndexAndMapping() throws Exception {
        TransportClient tc = new PreBuiltTransportClient(Settings.builder().put("cluster.name","elasticsearch").build());
        tc.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        tc.admin().indices().prepareCreate("blog2").get();
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type","long")
                .field("store","true")
                .endObject()
                .startObject("title")
                .field("type","text")
                .field("store","true")
                .field("analyzer","ik_smart")
                .endObject()
                .startObject("content")
                .field("type","text")
                .field("store","true")
                .field("analyzer","ik_smart")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mappingRequest = new PutMappingRequest("blog2").source(builder).type("article");
        tc.admin().indices().putMapping(mappingRequest).get();
        tc.close();
    }
}