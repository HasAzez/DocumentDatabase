package Indexing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.utils.JsonSchemaValidator;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class JsonCollection extends IndexingFunctionality implements Serializable {

    private final Map<String, JsonNode> uniqueIndexedMap;
    private final String name;
    private final ObjectMapper mapper;
    private final String validator;

    public JsonCollection(String name, String schemaFileLocation) {
        this.validator = schemaFileLocation;
        mapper = new ObjectMapper();
        this.uniqueIndexedMap = new HashMap<>();
        this.name = name;
    }


    public void insert(String jsonSentence) throws JsonProcessingException {
        JsonNode unWrappedJson = mapper.readTree(jsonSentence);
        if (!uniqueIndexedMap.containsKey(unWrappedJson.hashCode()+"")) {
            String wrapped = wrapID(jsonSentence);
            JsonNode uniqueIndexedJson = mapper.readTree(wrapped);
            uniqueIndexedMap.putIfAbsent(DigestUtils.sha1Hex(unWrappedJson.hashCode() + ""), uniqueIndexedJson);
            if (!getIndexedProperties().isEmpty())
                addToAllIndexes(uniqueIndexedJson);
        }
    }
    private String wrapID(String jsonSentence) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(jsonSentence);
        String hashedIndex = DigestUtils.sha1Hex(jsonNode.hashCode() + "");
        return "{\"_id\":\"" + hashedIndex + "\"," + jsonSentence.substring(1);
    }


    public void delete(String propertyName, String key) {

        if (propertyName.equals("_id")) {
            deleteFromIndexed(uniqueIndexedMap.get(key), key);
            uniqueIndexedMap.remove(key);
        } else if (getIndexedProperties().contains(propertyName)) {
            deleteFromNonIndexed(propertyName, key);
            deleteFromIndexed(propertyName, key);
        } else {
            deleteFromNonIndexed(propertyName, key);
        }

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




    public String getName() {
        return name;
    }

    public JsonSchemaValidator getValidator() throws FileNotFoundException {
        return new JsonSchemaValidator(validator);
    }

}
