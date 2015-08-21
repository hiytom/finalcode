package finalcode.HttpAsyncClient;

import finalcode.OperateData.ConcurrentData;
import finalcode.ProcessHtml.PurgeHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class HttpPoolRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpPoolRequest.class);

    private final static int SCH_POOL_SIZE = 1000;

    private final static int URL_SCH_INIT_TIME = 1;
    private final static int URL_SCH_WRITE_WAIT = 1;
    private final static int HTML_SCH_INIT_TIME = 1;
    private final static int HTML_SCH_WRITE_WAIT = 1;

    private static HttpPoolRequest httpPoolRequest;
    private final ScheduledExecutorService scheduledExecutorService;

    private HttpPoolRequest() {

        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.URL.isEmpty()) {
                try {
                    Object temp = ConcurrentData.URL;
                    Object temp2 = ConcurrentData.HTML;
                    String handleURL = ConcurrentData.URL.poll();
                    long time1 = System.currentTimeMillis();
                    String html = HttpClientManager.doGet(handleURL, "UTF-8", false);
                    long time2 = System.currentTimeMillis();
                    logger.info(Thread.currentThread().getName() + " - URL time : " + (time2 - time1));
                    ConcurrentData.HTML.offer(html);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, URL_SCH_INIT_TIME, URL_SCH_WRITE_WAIT, TimeUnit.NANOSECONDS);


        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.HTML.isEmpty()) {
                long time1 = System.currentTimeMillis();
                List<String> urlList = PurgeHtml.parse(ConcurrentData.HTML.poll());
                long time2 = System.currentTimeMillis();
                logger.info(Thread.currentThread().getName() + " - Html time : " + (time2 - time1));
                ConcurrentData.URL.addAll(urlList);
            }
        }, HTML_SCH_INIT_TIME, HTML_SCH_WRITE_WAIT, TimeUnit.SECONDS);

    }

    public void shutdown() {
        logger.info("HttpRequest threadPool shutdown begin...");
        scheduledExecutorService.shutdown();
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
