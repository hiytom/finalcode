package finalcode.httpasynclient;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by peng_chao on 15-8-18.
 */
public class HttpClientFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFactory.class);

    private static final int INIT_DELAY = 5 * 1000;
    private static final int CHECK_INTERVAL = 5 * 60 * 1000;
    private int lastConnections = 50;
    //默认请求超时时间：10秒
    private int timeout = (10 * 1000);
    //默认连接超时时间：5秒
    private int connectTimeout = (5 * 1000);
    private boolean gzip = false;

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String LANGUAGE = "en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4,zh-TW;q=0.2";

    private HttpClient httpClient;
    private HttpParams params;
    private ScheduledExecutorService scheduledExeService;
    public PoolingClientConnectionManager connectionManager;

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpClientFactory(int timeout, int connectTimeout, int connections) {
        if (timeout <= 0) {
            timeout = this.timeout;
        }

        if (connectTimeout <= 0) {
            connectTimeout = this.connectTimeout;
        }

        if (connections > 0 && lastConnections != connections) {
            lastConnections = connections;
        }

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(lastConnections);
        connectionManager.setDefaultMaxPerRoute(lastConnections);

        scheduledExeService = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("Http-client-ConenctionPool-Monitor"));
        scheduledExeService.scheduleAtFixedRate(new IdleConnectionMonitor(connectionManager), INIT_DELAY, CHECK_INTERVAL, TimeUnit.MILLISECONDS);

        this.httpClient = new DefaultHttpClient(connectionManager);
        this.params = httpClient.getParams();

        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
        HttpConnectionParams.setTcpNoDelay(params, Boolean.TRUE);
        HttpConnectionParams.setStaleCheckingEnabled(params, Boolean.FALSE);


    }

    public int getMaxConnections() {
        return this.lastConnections;
    }

    /**
     * 设置gzip传输方式
     */
    public void setGzip() {
        this.gzip = true;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 请求超时时间
     */
    public void setTimeOut(int timeout) {
        if (timeout > 0 && timeout != this.timeout) {
            this.timeout = timeout;
            HttpConnectionParams.setSoTimeout(params, this.timeout);
        }
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 连接超时时间
     */
    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout > 0 && connectTimeout != this.connectTimeout) {
            this.connectTimeout = connectTimeout;
            HttpConnectionParams.setConnectionTimeout(params, this.connectTimeout);
        }
    }

    /**
     * 通过POST的方法向服务器发送请求。
     *
     * @param reqURL   请求的url地址。
     * @param params   请求的参数列表。
     * @param encoding 请求的编码方式。
     * @return 返回response结果。
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String doPost(String reqURL, Map<String, String> params,
                         String encoding) throws ClientProtocolException, IOException {
        String responseContent = "";
        HttpPost httpPost = buildHttpPostRequest(reqURL, params, encoding);
        if (this.gzip == true) {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        HttpResponse response = httpClient.execute(httpPost);

        validateResponse(response, httpPost);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Header contentEncoding = entity.getContentEncoding();
            if (contentEncoding != null
                    && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(
                        entity);
                responseContent = EntityUtils.toString(gzipEntity, encoding);
            } else {
                responseContent = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        } else {
            LOG.warn("Http entity is null! request url is {},response status is {}", reqURL, response.getStatusLine());
        }
        return responseContent;

    }

    /**
     * 通过GET的方式向服务器发出请求。
     *
     * @param url      要请求的url。
     * @param encoding 指定的编码格式。
     * @return 获取response返回的结果。
     * @throws IOException
     */
    public String doGet(String url, String encoding, boolean isPB) throws IOException {
        String result = "";
        HttpGet httpget = new HttpGet(url);
        setHeader(httpget);

        if (this.gzip == true) {
            httpget.setHeader("Accept-Encoding", "gzip");
        }
        HttpResponse response = httpClient.execute(httpget);

        validateResponse(response, httpget);
        HttpEntity entity = response.getEntity();
        InputStream is;
        if (null != entity) {

            Header header = entity.getContentEncoding();

            if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
                GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(
                        entity);
                is = gzipEntity.getContent();
            } else {
                is = entity.getContent();
            }

            if (isPB) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, len);
                }
                byte[] temp = byteArrayOutputStream.toByteArray();
                result = new String(temp, "ISO8859-1");
            } else {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int count = -1;
                while ((count = is.read(data, 0, 4096)) != -1)
                    outStream.write(data, 0, count);
                data = null;
                return new String(outStream.toByteArray(), encoding);
            }

        }

        return result;
    }

    public void shutdownIdleConnectionMonitor() {
        if (scheduledExeService != null) {
            scheduledExeService.shutdown();
        }
    }

    private HttpPost buildHttpPostRequest(String url,
                                          Map<String, String> params, String encoding)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> paramEntrys = params.entrySet();
            for (Map.Entry<String, String> entry : paramEntrys) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
        }
        return httpPost;
    }

    private void validateResponse(HttpResponse response, HttpGet get)
            throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            LOG.warn(
                    "Did not receive successful HTTP response: status code = {}, status message = {}",
                    status.getStatusCode(), status.getReasonPhrase());
            get.abort();
            return;
        }
    }

    private void validateResponse(HttpResponse response, HttpPost post)
            throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            LOG.warn(
                    "Did not receive successful HTTP response: status code = {}, status message = {}",
                    status.getStatusCode(), status.getReasonPhrase());
            post.abort();
            return;
        }
    }

    private void setHeader(HttpGet httpget) {
        httpget.setHeader("User-Agent", USER_AGENT);
        httpget.setHeader("Accept", ACCEPT);
        httpget.setHeader("Accept-Language", LANGUAGE);
    }

}