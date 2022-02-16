package cache.use;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface SingletonCache {
    void put(String key, List<JsonNode> value);

    List<JsonNode> get(String key);

    void remove(String key);

    void clear();

    boolean containsKey(String key);

    int size();
}
