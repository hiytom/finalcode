package finalcode.OperateData;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao_b on 15/8/12.
 */
public class ConcurrentData {
    public final static Queue<String> HTML = new LinkedBlockingQueue<>();
    //public final static Queue<Option> DATA = new LinkedBlockingQueue<>();
    public final static Queue<String> URL = new ConcurrentLinkedQueue<>();

}
