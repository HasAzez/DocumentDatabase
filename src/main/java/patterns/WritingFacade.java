package patterns;

import cache.use.SingletonCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.BackgroundServer;
import index.DatabaseSchema;
import index.IndexBuilder;
import index.JsonCollection;
import index.ICollectionManager;
import json.utils.Instruction;
import json.utils.JsonSchemaValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WritingFacade implements WritingPrivileges {

  private final ICollectionManager collectionManager;
  private final BackgroundServer backgroundServer = BackgroundServer.INSTANCE;
  private final IndexBuilder indexBuilder;
  private final SingletonCache cache;

  public WritingFacade(IndexBuilder indexBuilder, SingletonCache cache, ICollectionManager collectionManager) {
    this.cache = cache;
    this.collectionManager = collectionManager;
    this.indexBuilder = indexBuilder;
  }
  @Override
  public String createCollection(String collectionName, String jsonSchema) throws IOException {
    DatabaseSchema jsonCollection = new JsonCollection(collectionName,new ObjectMapper().readTree(jsonSchema), indexBuilder);
    boolean worked = collectionManager.addCollection(jsonCollection);
    commit();
    return worked ? "Collection created" : "Collection already exists";
  }
  @Override
  public String deleteCollection(String collectionName) throws IOException {
    collectionManager.deleteCollection(collectionName);
    commit();
    return "Collection deleted";
  }
  @Override
  public String add(String collectionName, String jsonString) throws IOException {
    DatabaseSchema currentCollection = collectionManager.selectCollection(collectionName);
    JsonSchemaValidator validator = currentCollection.getValidator();
    if (validator.isValid(jsonString)) {
      currentCollection.insert(jsonString);
    } else {
      return validator.errorList(jsonString);
    }
    cache.clear();
    commit();
    return "Added";
  }
  @Override
  public String delete(Instruction instruction) throws IOException {
    DatabaseSchema currentCollection =
        collectionManager.selectCollection(instruction.getCollectionName());
    cache.clear();
    currentCollection.delete(instruction.getPropertyName(), instruction.getValue());
    commit();
    return "Deleted";
  }

  @Override
  public String update(Instruction instruction, String jsonString) throws IOException {

   if (instruction.getPropertyName().equals("_id"))
   { DatabaseSchema currentCollection =
           collectionManager.selectCollection(instruction.getCollectionName());
     cache.clear();
     currentCollection.update(instruction.getValue(), jsonString);
     commit();
     return "Updated";
   }
   return "failed";
  }

  private void commit() throws IOException {
    collectionManager.commit();
    backgroundServer.broadcast(new ArrayList<>(collectionManager.getJsonCollections()));
  }
  @Override
  public String makeIndexOn(String collectionName, String propertyName) {
    DatabaseSchema currentCollection = collectionManager.selectCollection(collectionName);
    currentCollection.makeIndexOn(propertyName);
    return "Index created";
  }
  @Override
  public String dumpCollection(String collectionName) throws IOException {
    collectionManager.dumpCollection(collectionName);
    return "Dumped";
  }

  @Override
  public String importCollection(String collectionName, File file)
      throws IOException, ClassNotFoundException {
    collectionManager.importCollection(collectionName, file);
    return "Imported";
  }
}
