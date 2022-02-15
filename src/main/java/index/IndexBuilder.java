package index;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.*;

public class IndexBuilder implements Serializable {
  private final   List<JsonNode> documents;
  private final   Map<String, List<JsonNode>> invertedIndex;
  private final Map<String, Map<String, List<JsonNode>>> indexes;

  private IndexBuilder(Builder builder){
    this.documents = builder.documents;
    this.invertedIndex = builder.invertedIndex;
    this.indexes = builder.indexes;
  }


  public void makeIndexOn(String property, Collection<JsonNode> values) {
    indexes.putIfAbsent(property,invertedIndex);
    for (JsonNode jsonNode : values) {
      addToIndexedMap(property, jsonNode);
    }
  }

  protected void addToIndexedMap(String property, JsonNode jsonNode) {
    String value = jsonNode.get(property).asText();
    getIndex(property).putIfAbsent(value, documents);
    getIndex(property).get(value).add(jsonNode);
  }

  protected void addToAllIndexes(JsonNode jsonNode) {
    for (String property : getIndexedProperties()) {
      if (!getIndex(property).isEmpty())
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

  public static class Builder implements Serializable {
  private  final List<JsonNode> documents;
  private    Map<String, List<JsonNode>> invertedIndex;
  private    Map<String, Map<String, List<JsonNode>>> indexes;

    public Builder(List<JsonNode> documents) {
      this.documents = documents;
    }

    public Builder indexType(Map<String, List<JsonNode>> invertedIndex) {
      this.invertedIndex = invertedIndex;
      return this;
    }

    public Builder indexesSelector(Map<String, Map<String, List<JsonNode>>> indexes) {
      this.indexes = indexes;
      return this;
    }


    public IndexBuilder build() {
      return new IndexBuilder(this);
    }}
}