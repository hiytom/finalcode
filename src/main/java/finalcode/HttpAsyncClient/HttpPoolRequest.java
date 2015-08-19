package finalcode.HttpAsyncClient;

import finalcode.OperateData.ConcurrentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class HttpPoolRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpPoolRequest.class);

    private final static int CORE_POOL_SIZE = 1;
    private final static int MAX_POOL_SIZE = 2;
    private final static int KEEP_ALIVE_TIME = 30;
    private final static int WORK_QUEUE_SIZE = 15;

    private final static int SCH_POOL_SIZE = 1;
    private final static int SCH_INIT_TIME = 60;
    private final static int SCH_WRITE_WAIT = 120;

    private static HttpPoolRequest httpPoolRequest;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolExecutor threadPool;

    private HttpPoolRequest() {
        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.URL.isEmpty()) {
                sendHttp();
            }
        }, SCH_INIT_TIME, SCH_WRITE_WAIT, TimeUnit.SECONDS);

        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(WORK_QUEUE_SIZE), (Runnable r, ThreadPoolExecutor executor) -> {
            logger.debug("The send http threadPool is pull !");
        });


    }

    private String sendHttp() {
        String html = null;
        return html;
    }


    public void shutdown() {
        logger.info("HttpRequest threadPool shutdown begin...");
        scheduledExecutorService.shutdown();
        threadPool.shutdown();
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
