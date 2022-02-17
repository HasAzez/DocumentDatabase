package index;

import json.utils.SerialUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum CollectionManager implements ICollectionManager {
  INSTANCE();

  private List<DatabaseSchema> jsonCollections;

  CollectionManager() {
    this.jsonCollections = handle();
  }

  public ArrayList<DatabaseSchema> handle() {
    try {
      File f = new File("database.db");
      if (f.exists()) {
        return SerialUtils.load(f);
      } else return new ArrayList<>();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteCollection(String name) {

    if (jsonCollections.contains(selectCollection(name))) {
      jsonCollections.removeIf(j -> j.getName().equals(name));
    }
  }

  @Override
  public DatabaseSchema selectCollection(String name) {

    for (DatabaseSchema collection : jsonCollections) {
      if (collection.getName().equals(name)) {
        return collection;
      }
    }
    return null;
  }

  @Override
  public boolean addCollection(DatabaseSchema collection) {

    if (showCollectionNames().contains(collection.getName()))
    return false;
    return jsonCollections.add(collection);
  }

  @Override
  public ArrayList<String> showCollectionNames() {

    ArrayList<String> names = new ArrayList<>();
    for (DatabaseSchema coll : jsonCollections) {
      names.add(coll.getName());
    }
    return names;
  }



  @Override
  public void commit() throws IOException {

    SerialUtils.save(new ArrayList<>(jsonCollections), "database.db");
  }

  @Override
  public void importCollection(String collectionName, File location)
      throws IOException, ClassNotFoundException {

    jsonCollections.add(SerialUtils.load(location));
  }  @Override
  public void dumpCollection(String collectionName) throws IOException {

    SerialUtils.save(selectCollection(collectionName), collectionName + ".coll");
  }

  @Override
  public List<DatabaseSchema> getJsonCollections() {

    return jsonCollections;
  }

  @Override
  public void setJsonCollections(List<DatabaseSchema> jsonCollections) {
    this.jsonCollections = jsonCollections;
  }
}
