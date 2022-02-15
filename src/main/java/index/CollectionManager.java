package index;

import json.utils.SaveManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum CollectionManager {
    INSTANCE();

    private final List<JsonCollection> jsonCollections;
    private JsonCollection currentCollection;
    private final ReentrantReadWriteLock reentrantReadWriteLock=new ReentrantReadWriteLock();
    private final Lock readLock=reentrantReadWriteLock.readLock();
    private final Lock writeLock=reentrantReadWriteLock.writeLock();

     CollectionManager() {
        this.jsonCollections = handle() ;
    }

    public  ArrayList<JsonCollection> handle()  {
        try {
            File f = new File("database.db");
            if (f.exists()) {
                return SaveManager.load(f);
            } else return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteCollection(String name) {
    writeLock.lock();
    try {
      if (currentCollection.getName().equals(name)) currentCollection = null;
      jsonCollections.removeIf(j -> j.getName().equals(name));
    } finally {
      writeLock.unlock();
    }
    }

    public JsonCollection selectCollection(String name) {
    writeLock.lock();
    try {
      boolean isExist = false;
      for (JsonCollection collection : jsonCollections) {
        if (collection.getName().equals(name)) {
          currentCollection = collection;
          isExist = true;
        }
      }
      if (!isExist) currentCollection = null;

      return currentCollection;
    } finally {
      writeLock.unlock();
    }
    }

    public boolean addCollection(JsonCollection collection) {
    writeLock.lock();
    try {
      if (jsonCollections.contains(collection)) return false;
      return jsonCollections.add(collection);
    } finally {
      writeLock.unlock();
    }
    }

    public ArrayList<String> showCollectionNames() {
    readLock.lock();
    try {
      ArrayList<String> names = new ArrayList<>();
      for (JsonCollection coll : jsonCollections) {
        names.add(coll.getName());
      }
      return names;
    } finally {
      readLock.unlock();
    }
    }

    public void dumpCollection() throws IOException {
    writeLock.lock();
    try {
      SaveManager.save(currentCollection, currentCollection.getName() + ".coll");
    } finally {
      writeLock.unlock();
    }
    }
    public void commit() throws IOException {
    writeLock.lock();
    try {
      SaveManager.save(new ArrayList<>(jsonCollections), "database.db");
    } finally {
      writeLock.unlock();
    }
    }

    public void importCollection(File location) throws IOException, ClassNotFoundException {
    writeLock.lock();
    try {
      jsonCollections.add(SaveManager.load(location));
    } finally {
      writeLock.unlock();
    }
    }

    public List<JsonCollection> getJsonCollections() {
    readLock.lock();
    try {
      return jsonCollections;
    } finally {
      readLock.unlock();
    }
    }


    public JsonCollection getCurrentCollection() {
         return currentCollection;
    }
}
