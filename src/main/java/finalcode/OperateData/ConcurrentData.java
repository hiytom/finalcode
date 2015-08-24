package finalcode.operateData;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao_b on 15/8/12.
 */
public class ConcurrentData {
    public final static Queue<String> HTML;
    public final static Queue<String> URL;
    public final static BloomFilter<String> REPEAT;

    static {
        HTML = new LinkedBlockingQueue<>();
        URL = new ConcurrentLinkedQueue<>();
        REPEAT = new BloomFilter<>(10000000, 0.03f);
    }

}
