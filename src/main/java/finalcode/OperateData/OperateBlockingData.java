package finalcode.OperateData;

import com.sun.tools.javac.main.Option;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao_b on 15/8/12.
 */
public class OperateBlockingData {
    public final static Queue<Option> RUL = new LinkedBlockingQueue<>();
    public final static Queue<Option> HTML = new LinkedBlockingQueue<>();
    public final static Queue<Option> DATA = new LinkedBlockingQueue<>();
}
