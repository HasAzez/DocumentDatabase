package index;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.*;

public abstract class IndexBuilder implements Serializable {

  protected final Map<String, Map<String, List<JsonNode>>> indexes;

  public IndexBuilder(Map<String, Map<String, List<JsonNode>>> indexes) {
    this.indexes = indexes;
  }

  public abstract void makeIndexOn(String property, Collection<JsonNode> values);

  protected void addToIndexedMap(String property, JsonNode jsonNode) {
    String value = jsonNode.get(property).asText();
    getIndex(property).putIfAbsent(value, new ArrayList<>());
    getIndex(property).get(value).add(jsonNode);
  }

  protected void addToAllIndexes(JsonNode jsonNode) {
    for (String property : getIndexedProperties()) {
      addToIndexedMap(property, jsonNode);
    }
  }

  protected void deleteFromIndexed(JsonNode specificDocument, String key) {

    for (String property : getIndexedProperties()) {
      String specificProperty = specificDocument.get(property).asText();
      List<JsonNode> wantedBucket = getIndex(property).get(specificProperty);
      wantedBucket.removeIf(jo -> jo.get("_id").asText().equals(key));
    }
  }

  protected void deleteFromIndexed(String property, String key) {
    List<JsonNode> jsonNodes = getIndex(property).get(key);
    ArrayList<JsonNode> temp = new ArrayList<>(jsonNodes);
    for (JsonNode jsonNode : temp) {
      deleteFromIndexed(jsonNode, jsonNode.get("_id").asText());
    }
  }

  protected Set<String> getIndexedProperties() {

    return indexes.keySet();
  }

  protected Map<String, List<JsonNode>> getIndex(String property) {

    return indexes.get(property);
  }
}
