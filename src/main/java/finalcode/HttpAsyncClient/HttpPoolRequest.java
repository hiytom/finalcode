package finalcode.HttpAsyncClient;

import finalcode.App;
import finalcode.OperateData.ConcurrentData;
import finalcode.ProcessHtml.PurgeHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class HttpPoolRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpPoolRequest.class);

    private final static int URL_SCH_POOL_SIZE = 1;
    private final static int URL_SCH_INIT_TIME = 2;
    private final static int URL_SCH_WRITE_WAIT = 1;

    private final static int HTML_SCH_POOL_SIZE = 1;
    private final static int HTML_SCH_INIT_TIME = 2;
    private final static int HTML_SCH_WRITE_WAIT = 1;

    private static HttpPoolRequest httpPoolRequest;
    private final ScheduledExecutorService urlScheduledExecutorService;
    private final ScheduledExecutorService htmlScheduledExecutorService;

    private HttpPoolRequest() {

        urlScheduledExecutorService = Executors.newScheduledThreadPool(URL_SCH_POOL_SIZE);
        urlScheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.URL.isEmpty()) {
                try {
                    String html = HttpClientManager.doGet(ConcurrentData.URL.poll(), "UTF-8", false);
                    ConcurrentData.HTML.offer(html);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, URL_SCH_INIT_TIME, URL_SCH_WRITE_WAIT, TimeUnit.SECONDS);


        htmlScheduledExecutorService = Executors.newScheduledThreadPool(HTML_SCH_POOL_SIZE);
        htmlScheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.HTML.isEmpty()) {
               List<String> urlList = PurgeHtml.parse(ConcurrentData.HTML.peek());
               ConcurrentData.URL.addAll(urlList);
            }
        }, HTML_SCH_INIT_TIME, HTML_SCH_WRITE_WAIT, TimeUnit.SECONDS);

    }

    public void shutdown() {
        logger.info("HttpRequest threadPool shutdown begin...");
        urlScheduledExecutorService.shutdown();
        htmlScheduledExecutorService.shutdown();
        logger.info("HttpRequest threadPool shutdown OK !");
    }

    public static HttpPoolRequest newHttpRequest() {
        if (null == httpPoolRequest) {
            synchronized (HttpPoolRequest.class) {
                if (null == httpPoolRequest) {
                    httpPoolRequest = new HttpPoolRequest();
                }
            }
        }
        return httpPoolRequest;
    }

}
