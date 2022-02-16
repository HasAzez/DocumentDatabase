package index;

import json.utils.SaveManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum CollectionManager implements SchemaManager {
    INSTANCE();

    private  List<DatabaseSchema> jsonCollections;
    private final ReentrantReadWriteLock reentrantReadWriteLock=new ReentrantReadWriteLock();
    private final Lock readLock=reentrantReadWriteLock.readLock();
    private final Lock writeLock=reentrantReadWriteLock.writeLock();

     CollectionManager() {
        this.jsonCollections = handle() ;
    }

    public  ArrayList<DatabaseSchema> handle()  {
        try {
            File f = new File("database.db");
            if (f.exists()) {
                return SaveManager.load(f);
            } else return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteCollection(String name) {
    writeLock.lock();
    try {
        if (jsonCollections.contains(selectCollection(name))) {
      jsonCollections.removeIf(j -> j.getName().equals(name));
    }} finally {
      writeLock.unlock();
    }
    }

    @Override
    public DatabaseSchema selectCollection(String name) {
    readLock.lock();
    try {
      for (DatabaseSchema collection : jsonCollections) {
        if (collection.getName().equals(name)) {
            return collection;
        }
      }
      return null;
    } finally {
      readLock.unlock();
    }
    }

    @Override
    public boolean addCollection(DatabaseSchema collection) {
    writeLock.lock();
    try {
      if (jsonCollections.contains(collection)) return false;
      return jsonCollections.add(collection);
    } finally {
      writeLock.unlock();
    }
    }

    @Override
    public ArrayList<String> showCollectionNames() {
    readLock.lock();
    try {
      ArrayList<String> names = new ArrayList<>();
      for (DatabaseSchema coll : jsonCollections) {
        names.add(coll.getName());
      }
      return names;
    } finally {
      readLock.unlock();
    }
    }

    @Override
    public void dumpCollection(String collectionName) throws IOException {
    writeLock.lock();
    try {
      SaveManager.save(selectCollection(collectionName), collectionName + ".coll");
    } finally {
      writeLock.unlock();
    }
    }
    @Override
    public void commit() throws IOException {
    writeLock.lock();
    try {
      SaveManager.save(new ArrayList<>(jsonCollections), "database.db");
    } finally {
      writeLock.unlock();
    }
    }

    @Override
    public void importCollection(String collectionName, File location) throws IOException, ClassNotFoundException {
    writeLock.lock();
    try {
      jsonCollections.add(SaveManager.load(location));
    } finally {
      writeLock.unlock();
    }
    }

    @Override
    public List<DatabaseSchema> getJsonCollections() {
    readLock.lock();
    try {
      return jsonCollections;
    } finally {
      readLock.unlock();
    }
    }

    @Override
    public void setJsonCollections(List<DatabaseSchema> jsonCollections) {
        this.jsonCollections = jsonCollections;
    }
}
