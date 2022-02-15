package facade;

import cache.Cache;
import index.IndexBuilder;
import index.JsonCollection;
import com.fasterxml.jackson.databind.JsonNode;
import json.utils.JsonSchemaValidator;
import controller.BackgroundServer;
import index.CollectionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WritingFacade implements WritingPrivileges {

  private final CollectionManager collectionManager;
  private BackgroundServer backgroundServer =BackgroundServer.INSTANCE;
  private JsonCollection currentCollection;
  private final IndexBuilder indexBuilder;
  private final Cache<String, List<JsonNode>> cache;

  public WritingFacade(IndexBuilder indexBuilder, Cache<String, List<JsonNode>> cache)
      throws IOException {
    this.cache = cache;
    this.collectionManager = CollectionManager.INSTANCE;
    this.indexBuilder = indexBuilder;
  }

  public String createCollection(String collectionName, String jsonSchema) throws IOException {
    JsonCollection jsonCollection = new JsonCollection(collectionName, jsonSchema, indexBuilder);
    boolean worked = collectionManager.addCollection(jsonCollection);
    commit();
    return worked ? "Collection created" : "Collection already exists";
  }

  public String deleteCollection() throws IOException {
    collectionManager.deleteCollection(currentCollection.getName());
    commit();
    return "Collection deleted";
  }

  public String add(String jsonString) throws IOException {
    selectCollection(currentCollection.getName());
    JsonSchemaValidator validator = currentCollection.getValidator();
    if (validator.isValid(jsonString)) {

      currentCollection.insert(jsonString);
    } else {
      return validator.errorList(jsonString);
    }
    commit();
    return "Added";
  }

  public String delete(String property, String value) throws IOException {
    selectCollection(currentCollection.getName());
    if (cache.containsKey(currentCollection.getName() + property + value)) {
      cache.remove(currentCollection.getName() + property + value);
    }
    cache.remove(currentCollection.getName() + "all");
    currentCollection.delete(property, value);
    commit();
    return "Deleted";
  }

  @Override
  public String update(String property, String value, String jsonString) throws IOException {

    return "fail";
  }

  private void commit() throws IOException {
    collectionManager.commit();
    backgroundServer.broadcast(new ArrayList<>(collectionManager.getJsonCollections()));
  }

  public String makeIndexOn(String propertyName) {
    selectCollection(currentCollection.getName());
    currentCollection.makeIndexOn(propertyName);
    return "Index created";
  }

  public String dumpCollection() throws IOException {
    selectCollection(currentCollection.getName());
    collectionManager.dumpCollection();
    return "Dumped";
  }

  public String importCollection(File file) throws IOException, ClassNotFoundException {
    collectionManager.importCollection(file);
    return "Imported";
  }

  public String selectCollection(String collectionName) {
    currentCollection = collectionManager.selectCollection(collectionName);
    return "Selected";
  }
}
