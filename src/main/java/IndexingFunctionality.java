import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class IndexingFunctionality {
    protected Map<String, Map<String, ArrayList<JsonNode>>> indexes;

    public IndexingFunctionality() {
        indexes = new HashMap<>();
    }


    protected void makeIndexOn(String property, Collection<JsonNode> values) {
        if (!indexes.containsKey(property)) {
            makeIndexTable(property);
        } else if (values.isEmpty()) {
            return;
        }

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

    protected void addToIndexedMap(JsonNode jsonNode) {
        for (String property :
                getIndexedProperties()) {
            addToIndexedMap(property, jsonNode);
        }
    }

    private void makeIndexTable(String property) {
        if (!indexes.containsKey(property)) {
            indexes.putIfAbsent(property, new HashMap<>());
        } else {
            System.out.println("it is already indexed");
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

    protected Set<String> getIndexedProperties() {

        return indexes.keySet();
    }

    protected Map<String, ArrayList<JsonNode>> getIndex(String property) {

        return indexes.get(property);

    }

}
