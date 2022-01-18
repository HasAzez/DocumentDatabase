import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonCollection implements Serializable {

    private final Map<String, JsonNode> uniqueIndexedMap;
    private final Map<String, ArrayList<JsonNode>> propertyIndexedMap;
    private final String name;
    private boolean isPropertyIndexed = false;
    private String indexProperty;
    private int count;
    private ObjectMapper mapper;


    public JsonCollection(String name) {
        mapper = new ObjectMapper();
        this.propertyIndexedMap = new HashMap<>();
        this.uniqueIndexedMap = new HashMap<>();
        this.name = name;
        count = 0;
    }


    public synchronized void insert(String jsonSentence) throws JsonProcessingException {


        String wrapped = wrapID(jsonSentence);
        JsonNode uniqueIndexedJson = mapper.readTree(wrapped);
        uniqueIndexedMap.putIfAbsent(DigestUtils.sha1Hex(count + ""), uniqueIndexedJson);
        if (isPropertyIndexed)
            insertToPropertyMap(uniqueIndexedJson);
        count++;


    }

    private String wrapID(String jsonSentence) {
        String hashedIndex = DigestUtils.sha1Hex(count + "");
        return "{\"_id\":\"" + hashedIndex + "\"," + jsonSentence.substring(1);
    }


    public synchronized boolean deleteUsingID(String key) {
        if (uniqueIndexedMap.isEmpty() || uniqueIndexedMap.get(key) == null) {
            return false;
        }
        if (isPropertyIndexed) {
            String specificProperty = uniqueIndexedMap.get(key).get(indexProperty).asText();

            ArrayList<JsonNode> wantedBucket = propertyIndexedMap.get(specificProperty);
            wantedBucket.removeIf(jo -> jo.get("_id").asText().equals(key));
        }
        uniqueIndexedMap.remove(key);


        count--;
        return true;
    }

    public Collection<JsonNode> getAll() {
        try {
            Collection<JsonNode> values = uniqueIndexedMap.values();

            return values;
        } finally {
        }

    }

    public JsonNode getDocument(String id) {
        return uniqueIndexedMap.get(id);

    }


    public ArrayList<JsonNode> getCertainFromIndexed(String searched) {
    return propertyIndexedMap.get(searched);
    }

    public ArrayList<JsonNode> getCertain( String propertyName,String searched) {

            ArrayList<JsonNode> jsonNodes = new ArrayList<>();
        for (JsonNode js :
                uniqueIndexedMap.values()) {
            if (js.get(propertyName).asText().equals(searched)) {
                jsonNodes.add(js);
            }
        }
        return jsonNodes;
    }

    public void makeIndexed(String indexProperty) {
        setIndexProperty(indexProperty);
        boolean haveValuesCondition = !isPropertyIndexed && !uniqueIndexedMap.isEmpty();
        if (haveValuesCondition) {
            for (JsonNode f :
                    uniqueIndexedMap.values()) {
                insertToPropertyMap(f);
            }
        }
        isPropertyIndexed = true;

    }

    public boolean isEmpty() {
        return uniqueIndexedMap.isEmpty();
    }

    private synchronized void insertToPropertyMap(JsonNode f) {

        String index = f.get(indexProperty).asText();
        propertyIndexedMap.putIfAbsent(index, new ArrayList<>());
        propertyIndexedMap.get(index).add(f);

    }

    public void setIndexProperty(String indexProperty) {
        this.indexProperty = indexProperty;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
