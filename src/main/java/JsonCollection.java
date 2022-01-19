import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.*;

public class JsonCollection extends IndexingFunctionality implements Serializable {

    private final Map<String, JsonNode> uniqueIndexedMap;
    private final String name;
    private int count;
    private ObjectMapper mapper;

    public JsonCollection(String name) {
        mapper = new ObjectMapper();
        this.uniqueIndexedMap = new HashMap<>();
        this.name = name;
        count = 0;
    }


    public synchronized void insert(String jsonSentence) throws JsonProcessingException {


        String wrapped = wrapID(jsonSentence);
        JsonNode uniqueIndexedJson = mapper.readTree(wrapped);
        uniqueIndexedMap.putIfAbsent(DigestUtils.sha1Hex(count + ""), uniqueIndexedJson);
        if (!getIndexedProperties().isEmpty())
            addToIndexedMap(uniqueIndexedJson);
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
        deleteFromIndexed(uniqueIndexedMap.get(key),key);
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


    public ArrayList<JsonNode> get(String propertyName, String searched) {
        ArrayList<JsonNode> jsonNodes = new ArrayList<>();

        if (getIndexedProperties().contains(propertyName)) {
            jsonNodes = getIndex(propertyName).get(searched);
        } else {
            searchForNonIndexedValues(propertyName, searched, jsonNodes);
        }

        return jsonNodes;
    }

    private void searchForNonIndexedValues(String propertyName, String searched, ArrayList<JsonNode> jsonNodes) {
        for (JsonNode js :
                uniqueIndexedMap.values()) {
            if (js.get(propertyName).asText().equals(searched)) {
                jsonNodes.add(js);
            }
        }
    }


    public boolean isEmpty() {
        return uniqueIndexedMap.isEmpty();
    }


    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }


}
