package finalcode.httpasynclient;

import finalcode.operatedata.ConcurrentData;
import finalcode.processHtml.PurgeHtml;
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

    private final static int SCH_POOL_SIZE = 2;

    private final static int URL_SCH_INIT_TIME = 1;
    private final static int URL_SCH_WRITE_WAIT = 1;
    private final static int HTML_SCH_INIT_TIME = 1;
    private final static int HTML_SCH_WRITE_WAIT = 1;

    private final static int CORE_POOL_SIZE = 0;
    private final static int MAXIMUM_POOL_SIZE = 4;
    private final static int KEEP_ALIVE_TIME = 30;

    private static HttpPoolRequest httpPoolRequest;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    private HttpPoolRequest() {

        executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            if (!ConcurrentData.URL.isEmpty()) {
                String handleURL = ConcurrentData.URL.poll();
                executorService.execute(() -> {
                    try {
                        String html = HttpClientManager.doGet(handleURL, "UTF-8", false);
                        ConcurrentData.HTML.offer(html);
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                    }
                });
            }

        }, URL_SCH_INIT_TIME, URL_SCH_WRITE_WAIT, TimeUnit.NANOSECONDS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            if (!ConcurrentData.HTML.isEmpty()) {
                String html = ConcurrentData.HTML.poll();
                List<String> urlList = PurgeHtml.parse(html);
                ConcurrentData.URL.addAll(urlList);
            }

        }, HTML_SCH_INIT_TIME, HTML_SCH_WRITE_WAIT, TimeUnit.MILLISECONDS);

    }

    public void shutdown() {
        logger.info("HttpRequest threadPool shutdown begin...");
        scheduledExecutorService.shutdown();
        executorService.shutdown();
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
