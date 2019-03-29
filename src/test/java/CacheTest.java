import org.junit.Assert;
import org.junit.Test;


public class CacheTest {

    @Test
    public void canCheckSelf() throws Exception {
        Cache cache = new Cache(Cache.EvictionStrategy.LRU);
        Assert.assertTrue(cache.areYouOk());
    }

    @Test
    public void canChangeStrategy() throws Exception {
        Cache cache = new Cache(Cache.EvictionStrategy.LRU);

        cache.setStrategy(Cache.EvictionStrategy.LRU);
        Assert.assertEquals(Cache.EvictionStrategy.LRU, cache.getStrategy());

        cache.setStrategy(Cache.EvictionStrategy.LFU);
        Assert.assertEquals(Cache.EvictionStrategy.LFU, cache.getStrategy());
    }

    @Test
    public void checkSize() throws Exception {
        Cache<Integer, Object> cache = new Cache(Cache.EvictionStrategy.LRU);

        cache.setMaxSize(10);
        Assert.assertEquals(10, cache.getMaxSize());

        cache.setMaxSize(20);
        Assert.assertEquals(20, cache.getMaxSize());

        // adding objects and checking size

        int MAX_SIZE = 10;
        cache.setMaxSize(MAX_SIZE);
        Assert.assertEquals(0, cache.size());

        for (Integer i = 0; i < MAX_SIZE; i++) {
            cache.put(i, new Object());
        }
        Assert.assertTrue(cache.size() <= MAX_SIZE);

        cache.put(MAX_SIZE + 1, new Object());
        Assert.assertTrue(cache.size() <= MAX_SIZE);
    }

    @Test
    public void checkUses() throws Exception {
        Cache<String, Object> cache = new Cache(Cache.EvictionStrategy.LRU);

        cache.put("first", new Object());
        Assert.assertEquals(0, cache.getUses("first"));

        Object o = cache.get("first");
        Assert.assertEquals(1, cache.getUses("first"));

        int USES = 10;
        cache.put("second", new Object());
        for (int i = 0; i < USES; i++) {
            cache.get("second");
        }
        Assert.assertEquals(USES, cache.getUses("second"));
    }

    @Test
    public void checkLifetime() throws Exception {
        Cache<Object, Object> cache = new Cache(Cache.EvictionStrategy.LRU);

        Object key_1 = new Object();
        cache.put(key_1, "object_1");
        Assert.assertEquals(0, cache.getLastUseTimeStamp(key_1));

        Object o = cache.get(key_1);
        long WAIT_TIME = 100;
        Thread.sleep(WAIT_TIME);
        Assert.assertTrue(Math.abs((System.currentTimeMillis() - cache.getLastUseTimeStamp(key_1)) - WAIT_TIME) < 5);
    }

    @Test
    public void checkLRU() throws Exception {
        Cache<Integer, Object> cache = new Cache(Cache.EvictionStrategy.LRU);

        // adding objects and checking size

        int MAX_SIZE = 10;
        cache.setMaxSize(MAX_SIZE);
        Assert.assertEquals(0, cache.size());

        for (Integer i = 0; i < MAX_SIZE; i++) {
            cache.put(i, new Object());
        }

        Assert.assertTrue(cache.size() == MAX_SIZE);

        int EXCLUDED_KEY = 5;
        long WAIT_TIME = 100;

        cache.get(EXCLUDED_KEY);
        for (Integer i = 0; i < MAX_SIZE; i++) {
            Thread.sleep(WAIT_TIME);
            if(i != EXCLUDED_KEY) {
                cache.get(i);
            }
        }

        cache.put(MAX_SIZE + 1, new Object());
        Assert.assertTrue(cache.size() == MAX_SIZE);

        Assert.assertNull(cache.get(EXCLUDED_KEY));
        Assert.assertNotNull(cache.get(MAX_SIZE + 1));
    }

    @Test
    public void checkLFU() throws Exception {
        Cache<Integer, Object> cache = new Cache(Cache.EvictionStrategy.LFU);

        // adding objects and checking size

        int MAX_SIZE = 10;
        cache.setMaxSize(MAX_SIZE);
        Assert.assertEquals(0, cache.size());

        for (Integer i = 0; i < MAX_SIZE; i++) {
            cache.put(i, new Object());
        }
        Assert.assertTrue(cache.size() == MAX_SIZE);

        int EXCLUDED_KEY = 5;

        for (Integer i = 0; i < MAX_SIZE; i++) {
            if(i != EXCLUDED_KEY) {
                cache.get(i);
            }
        }

        cache.put(MAX_SIZE + 1, new Object());
        Assert.assertTrue(cache.size() == MAX_SIZE);

        Assert.assertNull(cache.get(EXCLUDED_KEY));
        Assert.assertNotNull(cache.get(MAX_SIZE + 1));
    }
}
