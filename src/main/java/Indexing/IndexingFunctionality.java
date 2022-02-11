package Indexing;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class IndexingFunctionality {
    private Map<String, Map<String, ArrayList<JsonNode>>> indexes;

    public IndexingFunctionality() {
        indexes = new HashMap<>();
    }


    public void makeIndexOn(String property, Collection<JsonNode> values) {
            indexes.putIfAbsent(property, new HashMap<>());
        for (JsonNode jsonNode :
                values) {
            addToIndexedMap(property, jsonNode);
        }

    }


    protected void addToIndexedMap(String property, JsonNode jsonNode) {
        String value = jsonNode.get(property).asText();
        getIndex(property).putIfAbsent(value, new ArrayList<>());
        getIndex(property).get(value).add(jsonNode);

    }

    protected void addToAllIndexes(JsonNode jsonNode) {
        for (String property :
                getIndexedProperties()) {
            addToIndexedMap(property, jsonNode);
        }
    }



    protected void deleteFromIndexed(JsonNode specificDocument, String key) {

        for (String property :
                getIndexedProperties()) {
            String specificProperty = specificDocument.get(property).asText();
            ArrayList<JsonNode> wantedBucket = getIndex(property).get(specificProperty);
            wantedBucket.removeIf(jo -> jo.get("_id").asText().equals(key));
        }
    }
    protected void deleteFromIndexed(String property,String key) {
        ArrayList<JsonNode> jsonNodes = getIndex(property).get(key);
        ArrayList<JsonNode> temp = new ArrayList<>(jsonNodes);
        for (JsonNode jsonNode :
                temp) {
            deleteFromIndexed(jsonNode,jsonNode.get("_id").asText());
        }
    }

    protected Set<String> getIndexedProperties() {

        return indexes.keySet();
    }

    protected Map<String, ArrayList<JsonNode>> getIndex(String property) {

        return indexes.get(property);

    }

}
