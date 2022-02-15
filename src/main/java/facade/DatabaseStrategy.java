package facade;

import com.fasterxml.jackson.databind.JsonNode;
import controller.BackgroundServer;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class DatabaseStrategy implements Database {
  private ReadingPrivileges readingPrivileges;
  private WritingPrivileges writingPrivileges;


  public String selectCollection(String collectionName) {
    readingPrivileges.selectCollection(collectionName);
    return writingPrivileges.selectCollection(collectionName);
  }

  public String createCollection(String collectionName, String jsonSchema) throws IOException {
    return writingPrivileges.createCollection(collectionName, jsonSchema);
  }

  public String deleteCollection() throws IOException {
    return writingPrivileges.deleteCollection();
  }

  public List<JsonNode> find(String property, String searched) {
    return readingPrivileges.find(property, searched);
  }

  public List<JsonNode> findAll() {
    return readingPrivileges.findAll();
  }

  public String add(String jsonString) throws IOException {
    return writingPrivileges.add(jsonString);
  }

  public String delete(String property, String value) throws IOException {
    return writingPrivileges.delete(property, value);
  }

  @Override
  public String update(String property, String value, String jsonString)  {
    return "fail";
  }

  public String makeIndexOn(String propertyName) {
    return writingPrivileges.makeIndexOn(propertyName);
  }

  public String dumpCollection() throws IOException {
    return writingPrivileges.dumpCollection();
  }

  public String importCollection(File file) throws IOException, ClassNotFoundException {
    return writingPrivileges.importCollection(file);
  }

  public List<String> showCollections() {
    return readingPrivileges.showCollections();
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
