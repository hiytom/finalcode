package finalcode;

import finalcode.httpasynclient.HttpClientManager;
import finalcode.operatedata.ConcurrentData;
import finalcode.processHtml.HtmlDataParser;
import finalcode.processHtml.HtmlDataParserProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class FinalCodeFactory {
    private static final Logger logger = LoggerFactory.getLogger(FinalCodeFactory.class);

    private final static int SCH_POOL_SIZE = 2;

    private final static int URL_SCH_INIT_TIME = 1;
    private final static int URL_SCH_WRITE_WAIT = 1;
    private final static int HTML_SCH_INIT_TIME = 1;
    private final static int HTML_SCH_WRITE_WAIT = 1;

    private final static int CORE_POOL_SIZE = 0;
    private final static int MAXIMUM_POOL_SIZE = 2;
    private final static int KEEP_ALIVE_TIME = 20;

    private static FinalCodeFactory finalCodeFactory;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    Object a = ConcurrentData.DATA;
    Object b = ConcurrentData.HTML;
    Object c = ConcurrentData.REPEAT;
    Object d = ConcurrentData.URL;

    private FinalCodeFactory() {

        executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.URL.isEmpty()) {
                String handleURL = ConcurrentData.URL.poll();
                executorService.execute(() -> {
                    try {
                        String html = HttpClientManager.doGet(handleURL, "UTF-8", false);
                        String location = "<div class=\"finalcodelocation\">" + handleURL + "</div>";
                        ConcurrentData.HTML.offer(html + location);
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                    }
                });
            }
        }, URL_SCH_INIT_TIME, URL_SCH_WRITE_WAIT, TimeUnit.NANOSECONDS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.HTML.isEmpty()) {
                String html = ConcurrentData.HTML.poll();
                parseHtml(html);
            }
        }, HTML_SCH_INIT_TIME, HTML_SCH_WRITE_WAIT, TimeUnit.SECONDS);

    }


    private void parseHtml(String html) {
        Object obj = null;
        try {
            obj = Class.forName(App.achieve).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HtmlDataParserProxy htmlDataParserProxy = new HtmlDataParserProxy();
        HtmlDataParser htmlDataParser = (HtmlDataParser) htmlDataParserProxy.bind(obj);
        htmlDataParser.getData(html);
    }

    public void shutdown() {
        logger.info("HttpRequest threadPool shutdown begin...");
        scheduledExecutorService.shutdown();
        executorService.shutdown();
        logger.info("HttpRequest threadPool shutdown OK !");
    }

    public static FinalCodeFactory newHttpRequest() {
        if (null == finalCodeFactory) {
            synchronized (FinalCodeFactory.class) {
                if (null == finalCodeFactory) {
                    finalCodeFactory = new FinalCodeFactory();
                }
            }
        }
        return finalCodeFactory;
    }

}
