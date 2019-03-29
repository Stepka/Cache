import java.util.LinkedHashMap;

public class Cache<K, V>  {
    private boolean isOk = false;
    private int maxSize = 10;
    private EvictionStrategy strategy;

    private LinkedHashMap<K, CachedObjectContainer<V>> objects = new LinkedHashMap();

    public Cache(EvictionStrategy strategy) {
        this.strategy = strategy;
        isOk = true;
    }

    public boolean put(K key, V object) {
        if(objects.size() >= maxSize) {
            removeExcess();
        }
        objects.put(key, new CachedObjectContainer<>(object));
        return true;
    }

    public V get(K key) {
        if(objects.containsKey(key)) {
            objects.get(key).uses ++;
            objects.get(key).lastUsedTimeStamp = System.currentTimeMillis();
            return objects.get(key).object;
        }
        return null;
    }

    public boolean remove(K key) {
        return true;
    }

    public int getUses(K key) {
        if(objects.containsKey(key)) {
            return objects.get(key).uses;
        }
        return -1;
    }

    public long getLastUseTimeStamp(K key) {
        if(objects.containsKey(key)) {
            return objects.get(key).lastUsedTimeStamp;
        }
        return -1;
    }

    public boolean areYouOk() {
        return isOk;
    }

    // protected

    protected void removeExcess() {
        int l = objects.size();

        K excessKey = null;
        CachedObjectContainer checking = null;
        CachedObjectContainer excess = null;
        for (K k : objects.keySet()) {
            checking = objects.get(k);
            if(strategy == EvictionStrategy.LFU) {
                if (excess == null || checking.uses < excess.uses) {
                    excess = checking;
                    excessKey = k;
                }
            } else {
                // if checking object was created earlier (creation time less)
                if (excess == null || checking.lastUsedTimeStamp < excess.lastUsedTimeStamp) {
                    excess = checking;
                    excessKey = k;
                }
            }
        }

        if(excessKey != null) {
            objects.remove(excessKey);
        }
    }

    // getters and setters

    public int size() {
        return objects.size();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public EvictionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(EvictionStrategy strategy) {
        this.strategy = strategy;
    }

    // enums

    public enum EvictionStrategy {
        LRU,
        LFU
    }
}

class CachedObjectContainer <T>  {
    public T object;
    public int uses = 0;
    public long lastUsedTimeStamp = 0;

    public CachedObjectContainer(T object) {
        this.object = object;
    }
}