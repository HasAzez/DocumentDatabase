package Cache;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class FIFOCache <K, V> extends LinkedHashMap<K,V> implements Cache<K,V>, Serializable {

    private final int maxSize;

    public FIFOCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > maxSize;
    }





}
