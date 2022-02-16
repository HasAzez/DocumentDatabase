package patterns;

import cache.use.SingletonCache;
import com.fasterxml.jackson.databind.JsonNode;
import index.DatabaseSchema;
import index.SchemaManager;
import json.utils.Instruction;

import java.util.List;

public class ReadingFacade implements ReadingPrivileges {

  private final SchemaManager collectionManager;
  private final SingletonCache cache;

  public ReadingFacade(SingletonCache cache,SchemaManager collectionManager) {
    this.collectionManager = collectionManager;
    this.cache = cache;
  }
  @Override
  public List<JsonNode> find(Instruction instruction) {
    List<JsonNode> result;
    DatabaseSchema currentCollection =
        collectionManager.selectCollection(instruction.getCollectionName());
    if (cache.get(instruction.toString()) == null) {
      result = currentCollection.get(instruction.getPropertyName(), instruction.getValue());
      cache.put(instruction.toString(), result);
    } else {
      result = cache.get(instruction.toString());
    }
    return result;
  }
  @Override
  public List<JsonNode> findAll(String collectionName) {
    DatabaseSchema currentCollection = collectionManager.selectCollection(collectionName);
    List<JsonNode> result;
    if ((cache.get(currentCollection.getName() + "all") == null)) {

      result = currentCollection.getAll();
      cache.put(currentCollection.getName() + "all", result);
    } else {
      result = cache.get(currentCollection.getName() + "all");
    }

    return result;
  }
  @Override
  public List<String> showCollections() {
    return collectionManager.showCollectionNames();
  }

}
