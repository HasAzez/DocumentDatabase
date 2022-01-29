package Indexing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class JsonCollection extends IndexingFunctionality implements Serializable {

    private final Map<String, JsonNode> uniqueIndexedMap;
    private final String name;
    private int count;
    private final ObjectMapper mapper;

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


    public synchronized boolean delete(String propertyName, String key) {

        if (propertyName.equals("_id")) {
            deleteFromIndexed(uniqueIndexedMap.get(key), key);
            uniqueIndexedMap.remove(key);
            count--;
        } else if (getIndexedProperties().contains(propertyName)) {
            deleteFromNonIndexed(propertyName, key);
            deleteFromIndexed(propertyName,key);
        } else {
            deleteFromNonIndexed(propertyName, key);
        }
        return true;
    }

    private void deleteFromNonIndexed(String propertyName, String key) {

        uniqueIndexedMap.entrySet().removeIf(e -> e.getValue().get(propertyName).asText().equals(key));

    }

    public ArrayList<JsonNode> getAll() {
        Collection<JsonNode> values = uniqueIndexedMap.values();
        return new ArrayList<>(values);

    }

    private JsonNode getDocument(String id) {
        return uniqueIndexedMap.get(id);

    }


    public ArrayList<JsonNode> get(String propertyName, String searched) {
        ArrayList<JsonNode> jsonNodes = new ArrayList<>();
        if (propertyName.equals("_id")) {
            jsonNodes.add(getDocument(searched));
        } else if (getIndexedProperties().contains(propertyName)) {
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