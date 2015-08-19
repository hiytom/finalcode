package finalcode.HttpAsyncClient;

import finalcode.OperateData.OperateBlockingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    private final static int CORE_POOL_SIZE = 1;
    private final static int MAX_POOL_SIZE = 2;
    private final static int KEEP_ALIVE_TIME = 5;
    private final static int WORK_QUEUE_SIZE = 20;

    private final static int SCH_POOL_SIZE = 1;
    private final static int SCH_INIT_TIME = 60;
    private final static int SCH_WRITE_WAIT = 120;


    private static HttpRequest httpRequest;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolExecutor threadPool;


    private HttpRequest() {
        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!OperateBlockingData.RUL.isEmpty()) {
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

    public static HttpRequest newHttpRequest() {
        if (null == httpRequest) {
            synchronized (HttpRequest.class) {
                if (null == httpRequest) {
                    httpRequest = new HttpRequest();
                }
            }
        }
        return httpRequest;
    }

}
