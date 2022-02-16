package index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.utils.JsonSchemaValidator;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonCollection implements Serializable, DatabaseSchema {

  private final Map<String, JsonNode> uniqueIndexedMap;
  private final String name;
  private final ObjectMapper mapper;
  private final String validator;
  private final IndexBuilder indexBuilder;
  private final ReentrantReadWriteLock reentrantReadWriteLock=new ReentrantReadWriteLock();
  private final Lock readLock=reentrantReadWriteLock.readLock();
  private final Lock writeLock=reentrantReadWriteLock.writeLock();

  public JsonCollection(String name, String schemaFileLocation, IndexBuilder indexBuilder){

    this.validator = schemaFileLocation;
    mapper = new ObjectMapper();
    this.uniqueIndexedMap = new HashMap<>();
    this.name = name;
    this.indexBuilder= indexBuilder;
  }

  @Override
  public void insert(String jsonSentence) throws JsonProcessingException {
    writeLock.lock();
    try {
      JsonNode unWrappedJson = mapper.readTree(jsonSentence);
      if (!uniqueIndexedMap.containsKey(unWrappedJson.hashCode() + "")) {
        String wrapped = wrapID(jsonSentence);
        JsonNode uniqueIndexedJson = mapper.readTree(wrapped);
        uniqueIndexedMap.putIfAbsent(
            DigestUtils.sha1Hex(unWrappedJson.hashCode() + ""), uniqueIndexedJson);
        if (!indexBuilder.getIndexedProperties().isEmpty())
          indexBuilder.addToAllIndexes(uniqueIndexedJson);
      }
    } finally {
      writeLock.unlock();
    }
  }

  private String wrapID(String jsonSentence) throws JsonProcessingException {
    JsonNode jsonNode = mapper.readTree(jsonSentence);
    String hashedIndex = DigestUtils.sha1Hex(jsonNode.hashCode() + "");
    return "{\"_id\":\"" + hashedIndex + "\"," + jsonSentence.substring(1);
  }

  @Override
  public void delete(String propertyName, String key) {
    writeLock.lock();
    try {
      if (propertyName.equals("_id")) {
        indexBuilder.deleteFromIndexed(uniqueIndexedMap.get(key), key);
        uniqueIndexedMap.remove(key);
      } else if (indexBuilder.getIndexedProperties().contains(propertyName)) {
        deleteFromNonIndexed(propertyName, key);
        indexBuilder.deleteFromIndexed(propertyName, key);
      } else {
        deleteFromNonIndexed(propertyName, key);
      }
    } finally {
      writeLock.unlock();
    }
  }

  private void deleteFromNonIndexed(String propertyName, String key) {

    uniqueIndexedMap.entrySet().removeIf(e -> e.getValue().get(propertyName).asText().equals(key));
  }

  @Override
  public List<JsonNode> getAll() {
    readLock.lock();
    try {
      Collection<JsonNode> values = uniqueIndexedMap.values();
      return new ArrayList<>(values);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void update(String id, String jsonSentence) throws JsonProcessingException {
    writeLock.lock();
    try {
      JsonNode oldJson = getDocument(id);
      if (uniqueIndexedMap.containsKey(id)) {
        String wrapped = wrapIDWithDesiredID(jsonSentence, id);
        JsonNode uniqueIndexedJson = mapper.readTree(wrapped);
        uniqueIndexedMap.put(id, uniqueIndexedJson);
        if (!indexBuilder.getIndexedProperties().isEmpty()) {
          indexBuilder.deleteFromIndexed(oldJson, id);
          indexBuilder.addToAllIndexes(uniqueIndexedJson);
        }
      }
    } finally {
      writeLock.unlock();
    }
  }

  private String wrapIDWithDesiredID(String jsonSentence, String id) {
    return "{\"_id\":\"" + id + "\"," + jsonSentence.substring(1);
  }



  @Override
  public List<JsonNode> get(String propertyName, String searched) {
    readLock.lock();
    try {
      List<JsonNode> jsonNodes = new ArrayList<>();
      if (propertyName.equals("_id")) {
        jsonNodes.add(getDocument(searched));
      } else if (indexBuilder.getIndexedProperties().contains(propertyName)) {
        jsonNodes = indexBuilder.getIndex(propertyName).get(searched);
      } else {
        searchForNonIndexedValues(propertyName, searched, jsonNodes);
      }

      return jsonNodes;
    } finally {
      readLock.unlock();
    }
  }
  private JsonNode getDocument(String id) {
    return uniqueIndexedMap.get(id);
  }
  private void searchForNonIndexedValues(
      String propertyName, String searched, List<JsonNode> jsonNodes) {
    for (JsonNode js : uniqueIndexedMap.values()) {
      if (js.get(propertyName).asText().equals(searched)) {
        jsonNodes.add(js);
      }
    }
  }

  @Override
  public void makeIndexOn(String propertyName) {
    writeLock.lock();
    try {
      indexBuilder.makeIndexOn(propertyName, uniqueIndexedMap.values());
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public JsonSchemaValidator getValidator() throws FileNotFoundException {
    return new JsonSchemaValidator(validator);
  }
  public int getSize() {
    return uniqueIndexedMap.size();
  }

}
