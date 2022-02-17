package patterns;

import com.fasterxml.jackson.databind.JsonNode;
import controller.BackgroundServer;
import json.utils.Instruction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class DatabaseStrategy implements Database {
  private ReadingPrivileges readingPrivileges;
  private WritingPrivileges writingPrivileges;
  private final Lock writeLock;
  private final Lock readLock;

  public DatabaseStrategy(SingletonReadWriteLock singletonReadWriteLock) {
    this.writeLock = singletonReadWriteLock.getWriteLock();
    this.readLock = singletonReadWriteLock.getReadLock();
  }

  @Override
  public String dumpCollection(String collectionName) throws IOException {

    writeLock.lock();
    try {
      return writingPrivileges.dumpCollection(collectionName);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String importCollection(String collectionName, File file)
      throws IOException, ClassNotFoundException {
    writeLock.lock();
    try {
      return writingPrivileges.importCollection(collectionName, file);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String makeIndexOn(String collectionName, String propertyName) {
    writeLock.lock();
    try {
      return writingPrivileges.makeIndexOn(collectionName, propertyName);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String createCollection(String collectionName, String jsonSchema) throws IOException {
    writeLock.lock();
    try {
      return writingPrivileges.createCollection(collectionName, jsonSchema);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String deleteCollection(String collectionName) throws IOException {
    writeLock.lock();
    try {
      return writingPrivileges.deleteCollection(collectionName);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String add(String collectionName, String jsonString) throws IOException {
    writeLock.lock();
    try {
      return writingPrivileges.add(collectionName, jsonString);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public String delete(Instruction instruction) throws IOException {
    writeLock.lock();
    try {
      return writingPrivileges.delete(instruction);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public List<JsonNode> find(Instruction instruction) {
    readLock.lock();
    try {
      return readingPrivileges.find(instruction);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<JsonNode> findAll(String collectionName) {
    readLock.lock();
    try {
      return readingPrivileges.findAll(collectionName);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public String update(Instruction instruction, String jsonString) {
    return "fail";
  }

  @Override
  public List<String> showCollections() {
    readLock.lock();
    try {
      return readingPrivileges.showCollections();
    } finally {
      readLock.unlock();
    }
  }

  public void setReadingPrivileges(ReadingPrivileges readingPrivileges) {
    this.readingPrivileges = readingPrivileges;
  }

  public void setWritingPrivileges(WritingPrivileges writingPrivileges) {
    this.writingPrivileges = writingPrivileges;
  }

  @Override
  public List<String> getPorts() {
    return BackgroundServer.INSTANCE.getPorts();
  }
}
