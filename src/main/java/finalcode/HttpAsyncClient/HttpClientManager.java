package finalcode.HttpAsyncClient;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import java.io.IOException;
import java.util.Map;

/**
 * Created by peng_chao on 15-8-18.
 */
public class HttpClientManager {

    private static HttpClientFactory httpClientFactory = new HttpClientFactory(0, 0, 0);

    public static PoolingClientConnectionManager getConnectionManager() {
        return httpClientFactory.connectionManager;
    }

    //设置连接池最大连接数
    public static void setMaxConnections(int maxConnections) {
        if (maxConnections > 0 && maxConnections != httpClientFactory.getMaxConnections()) {
            if (httpClientFactory != null) {
                httpClientFactory.shutdownIdleConnectionMonitor();
            }
            httpClientFactory = new HttpClientFactory(0, 0, maxConnections);
        }
    }

    //设置超时时间
    public static void setTimeout(int timeout) {
        httpClientFactory.setTimeOut(timeout);
    }

    public static void setGzip() {
        httpClientFactory.setGzip();
    }

    //设置连接超时时间
    public static void setConnectTimeout(int connectTimeout) {
        httpClientFactory.setConnectTimeout(connectTimeout);
    }

    public static String doPost(String requestPath, Map<String, String> params, String encoding)
            throws IOException {
        return httpClientFactory.doPost(requestPath, params, encoding);
    }

    public static String doGet(String url, String encoding, boolean isPB) throws IOException {
        return httpClientFactory.doGet(url, encoding, isPB);
    }

}

