package finalcode.operatedata;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao_b on 15/8/12.
 */
public class ConcurrentData {
    public final static LinkedBlockingQueue<String> HTML;
    public final static ConcurrentLinkedQueue<String> URL;
    public final static BloomFilter<String> REPEAT;

    static {
        HTML = new LinkedBlockingQueue<>();
        URL = new ConcurrentLinkedQueue<>();
        REPEAT = new BloomFilter<>(10000000, 0.03f);
    }

}
