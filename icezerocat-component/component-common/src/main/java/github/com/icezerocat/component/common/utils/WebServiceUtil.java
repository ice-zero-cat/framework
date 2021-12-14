package github.com.icezerocat.component.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Description: webService工具类
 * CreateDate:  2021/9/15 23:24
 *
 * @author zero
 * @version 1.0
 */
public class WebServiceUtil {

    private static Logger log = LoggerFactory.getLogger(WebServiceUtil.class);

    // 请求超时时间
    private static int socketTimeout = 10000;
    // 传输超时时间
    private static int connectTimeout = 10000;

    /**
     * 同步HttpPost请求发送SOAP格式的消息
     *
     * @param webServiceURL WebService接口地址
     * @param soapXml       消息体
     * @return
     */
    public static String doPostSoap(String webServiceURL, String soapXml) {
//        BufferedReader reader = null;
        // 创建HttpClient
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        // 创建Post请求
        HttpPost httpPost = new HttpPost(webServiceURL);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        // 设置Post请求报文头部
//        String soap11 = "application/soap+xml;charset=UTF-8";
        httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
        httpPost.setHeader("SOAPAction", "");
        // 添加报文内容
        StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
        httpPost.setEntity(data);
        try {
            // 执行请求获取返回报文
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                // 打印响应内容
                return EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            // 释放资源
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
