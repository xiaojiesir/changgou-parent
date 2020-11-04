package com.itheima.httpclient;

import entity.HttpClient;
import org.junit.Test;

import java.io.IOException;

public class HttpclientTest {
    @Test
    public void testHttpClient() throws IOException {
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        HttpClient httpClient = new HttpClient(url);

        String xml = "<xml><name>wx2421b1c4370ec43b</name></xml>";

        httpClient.setXmlParam(xml);

        httpClient.post();
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
