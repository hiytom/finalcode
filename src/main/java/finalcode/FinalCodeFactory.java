package finalcode;

import finalcode.db.JdbcTemplate;
import finalcode.httpasynclient.HttpClientManager;
import finalcode.operatedata.ConcurrentData;
import finalcode.processhtml.HtmlDataParser;
import finalcode.processhtml.HtmlDataParserProxy;
import finalcode.utils.FinalCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class FinalCodeFactory {
    private static final Logger logger = LoggerFactory.getLogger(FinalCodeFactory.class);

    private final static int URL_SCH_INIT_TIME = 1;
    private final static int URL_SCH_WRITE_WAIT = 1;

    private final static int HTML_SCH_INIT_TIME = 1;
    private final static int HTML_SCH_WRITE_WAIT = 1;

    private final static int DATA_SCH_INIT_TIME = 2;
    private final static int DATA_SCH_WRITE_WAIT = 2;

    private final static int SCH_POOL_SIZE = 2;

    private final static int CORE_POOL_SIZE = 0;
    private final static int MAXIMUM_POOL_SIZE = 2;
    private final static int KEEP_ALIVE_TIME = 20;
    private final static String locationDiv = "<div class=\"finalcodelocation\">{url}</div>";

    private static FinalCodeFactory finalCodeFactory;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    private FinalCodeFactory() {

        executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        scheduledExecutorService = Executors.newScheduledThreadPool(SCH_POOL_SIZE);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Object a = ConcurrentData.DATA;
            Object b = ConcurrentData.HTML;
            Object c = ConcurrentData.REPEAT;
            Object d = ConcurrentData.URL;

            if (!ConcurrentData.URL.isEmpty()) {
                String handleURL = ConcurrentData.URL.poll();
                executorService.execute(() -> {
                    try {
                        String html = HttpClientManager.doGet(FinalCodeUtil.encode(handleURL), "UTF-8", false);
                        String location = locationDiv.replace("{url}", handleURL);
                        ConcurrentData.HTML.offer(html + location);
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                    }
                });
            }
        }, URL_SCH_INIT_TIME, URL_SCH_WRITE_WAIT, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!ConcurrentData.HTML.isEmpty()) {
                try {
                    String html = ConcurrentData.HTML.poll();
                    parseHtml(html);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }
        }, HTML_SCH_INIT_TIME, HTML_SCH_WRITE_WAIT, TimeUnit.NANOSECONDS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                JdbcTemplate.insertBatch(ConcurrentData.DATA);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }, DATA_SCH_INIT_TIME, DATA_SCH_WRITE_WAIT, TimeUnit.MINUTES);

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
