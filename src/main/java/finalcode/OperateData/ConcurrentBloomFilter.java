package finalcode.operateData;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by peng_chao on 15-8-23.
 */
public final class ConcurrentBloomFilter<T> implements Serializable {

    private final BloomFilter<T> bloomFilter;
    private final ReentrantLock lock = new ReentrantLock();
    private final static int DEFAULT_SIZE = 10000000;

    public ConcurrentBloomFilter(Funnel<? super T> funnel, int expectedInsertions /* n */, double fpp) {
        bloomFilter = BloomFilter.create(funnel, expectedInsertions, fpp);
    }

    public ConcurrentBloomFilter(Funnel<? super T> funnel, int expectedInsertions) {
        bloomFilter = BloomFilter.create(funnel, expectedInsertions);
    }


    public ConcurrentBloomFilter(Funnel<? super T> funnel) {
        bloomFilter = BloomFilter.create(funnel, DEFAULT_SIZE);
    }

    public boolean add(T t) {
        ReentrantLock addLock = this.lock;
        boolean c = false;
        try {
            addLock.lockInterruptibly();
            bloomFilter.put(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            addLock.unlock();
        }
        return c;
    }
}
