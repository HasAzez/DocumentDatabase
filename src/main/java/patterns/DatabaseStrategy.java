package patterns;

import com.fasterxml.jackson.databind.JsonNode;
import controller.BackgroundServer;
import json.utils.Instruction;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatabaseStrategy implements Database {
  private ReadingPrivileges readingPrivileges;
  private WritingPrivileges writingPrivileges;

  @Override
  public String dumpCollection(String collectionName) throws IOException {

    return writingPrivileges.dumpCollection(collectionName);
  }

  @Override
  public String importCollection(String collectionName, File file)
      throws IOException, ClassNotFoundException {
    return writingPrivileges.importCollection(collectionName, file);
  }

  @Override
  public String makeIndexOn(String collectionName, String propertyName) {
    return writingPrivileges.makeIndexOn(collectionName, propertyName);
  }

  @Override
  public String createCollection(String collectionName, String jsonSchema) throws IOException {
    return writingPrivileges.createCollection(collectionName, jsonSchema);
  }

  @Override
  public String deleteCollection(String collectionName) throws IOException {
    return writingPrivileges.deleteCollection(collectionName);
  }

  @Override
  public String add(String collectionName, String jsonString) throws IOException {
    return writingPrivileges.add(collectionName, jsonString);
  }

  @Override
  public String delete(Instruction instruction) throws IOException {
    return writingPrivileges.delete(instruction);
  }

  @Override
  public List<JsonNode> find(Instruction instruction) {
    return readingPrivileges.find(instruction);
  }

  @Override
  public List<JsonNode> findAll(String collectionName) {
    return readingPrivileges.findAll(collectionName);
  }

  @Override
  public String update(Instruction instruction, String jsonString) {
    return "fail";
  }

  @Override
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
