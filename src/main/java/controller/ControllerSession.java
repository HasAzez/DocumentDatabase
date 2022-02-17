package controller;

import cache.use.SingletonFIFOCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.utils.Instruction;
import patterns.*;
import index.CollectionManager;
import index.HashMapIndex;
import index.IndexBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import index.ICollectionManager;
import json.utils.ListOperations;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ControllerSession {

  private final DatabaseStrategy db;
  private String userRole;
  private final SingletonFIFOCache cache = SingletonFIFOCache.INSTANCE;
  private final ICollectionManager collectionManager = CollectionManager.INSTANCE;
  IndexBuilder indexBuilder = new HashMapIndex(new HashMap<>());
  private final ReadingPrivileges rp = new ReadingFacade(cache,collectionManager);
  private final Instruction instruction = new Instruction() ;

  public ControllerSession() throws IOException {
    WritingPrivileges wp = new WritingFacade(indexBuilder, cache,collectionManager);
    db = new DatabaseStrategy(SingletonReadWriteLock.INSTANCE);
    setRole(db, rp, wp);
    defaultInstruction();

    firstTimeWizard();
  }

  private void defaultInstruction() {
    instruction.setCollectionName("credentials");
    instruction.setPropertyName("username");
    instruction.setValue("default");
  }

  private void setRole(DatabaseStrategy db, ReadingPrivileges rp, WritingPrivileges wp) {
    db.setReadingPrivileges(rp);
    db.setWritingPrivileges(wp);

  }

  private void firstTimeWizard() throws IOException {
    File f = new File("database.db");
    if (!f.exists()) {
      System.out.println("please change your username and password");
      JsonNode schema = new ObjectMapper().readTree(new File("Schema\\credentials.json") );
      db.createCollection("credentials", schema.toString());
      db.makeIndexOn("credentials","username");
      db.makeIndexOn("credentials","password");
      db.add("credentials"
              ,"{\"username\":\"default\",\"password\":\"default\",\"role\":\"admin\"}");
    }
  }

  public Optional<Database> checkCredentials(String username, String password) throws IOException {
    List<JsonNode> check = searchForUserAndPass(username, password);
    firstTimeWipe(username);
    userRole = getRole(check);
    return getOptionalDatabase();
  }

  private Optional<Database> getOptionalDatabase() {
    if (userRole.equals("admin")) {
      DatabaseStrategy adminPrivileges = getAdminPrivileges();
      return Optional.of(adminPrivileges);
    } else if (userRole.equals("user")) {
      DatabaseStrategy userPrivileges = getUserPrivileges();
      return Optional.of(userPrivileges);
    }
    return Optional.empty();
  }

  private DatabaseStrategy getUserPrivileges() {
    WritingPrivileges wp = new NoWritingPrivileges();
    DatabaseStrategy databaseStrategy = new DatabaseStrategy(SingletonReadWriteLock.INSTANCE);
    setRole(databaseStrategy, rp, wp);
    return databaseStrategy;
  }
  private DatabaseStrategy getAdminPrivileges() {
    IndexBuilder indexBuilder = new HashMapIndex(new HashMap<>());
    WritingPrivileges wp = new WritingFacade(indexBuilder, cache,collectionManager);
    DatabaseStrategy databaseStrategy = new DatabaseStrategy(SingletonReadWriteLock.INSTANCE);
    setRole(databaseStrategy, rp, wp);
    return databaseStrategy;
  }


  private void firstTimeWipe(String username) throws IOException {
    if (username.equals("default")) {
      wipe();
    }
  }

  private String getRole(List<JsonNode> check) {
    return check.get(0).get("role").asText();
  }

  private void wipe() throws IOException {
    db.delete(instruction);
  }

  private List<JsonNode> searchForUserAndPass(String username, String password) {
    List<JsonNode> userList = getUserList(username);
    List<JsonNode> passList = getPassList(password);
    return ListOperations.intersection(userList, passList);
  }

  private List<JsonNode> getUserList(String username) {
    instruction.setValue(username);
    return db.find(instruction);
  }

  private List<JsonNode> getPassList(String password) {
    instruction.setPropertyName("password");
    instruction.setValue(password);
    return db.find(instruction);
  }

  public String getUserRole() {
    return userRole;
  }
}
