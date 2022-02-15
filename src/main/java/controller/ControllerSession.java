package controller;

import cache.Cache;
import cache.FIFOCache;
import facade.*;
import index.IndexBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import json.utils.ListOperations;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ControllerSession {

  private DatabaseStrategy db;
  private String userRole;
  private Cache<String, List<JsonNode>> cache = new FIFOCache<>(100);
  IndexBuilder indexBuilder =
      new IndexBuilder.Builder(new ArrayList<>())
          .indexType(new HashMap<>())
          .indexesSelector(new HashMap<>())
          .build();
  private ReadingPrivileges rp = new ReadingFacade(cache);

  public ControllerSession() throws IOException {
    WritingPrivileges wp = new WritingFacade(indexBuilder, cache);
    db = new DatabaseStrategy();
    db.setReadingPrivileges(rp);
    db.setWritingPrivileges(wp);
    firstTimeWizard();
  }

  private void firstTimeWizard() throws IOException {
    File f = new File("database.db");
    if (!f.exists()) {
      System.out.println("please change your username and password");
      db.createCollection("credentials", "credentials.json");
      db.selectCollection("credentials");
      db.add("{\"username\":\"default\",\"password\":\"default\",\"role\":\"admin\"}");
    }
  }

  public Optional<Database> checkCredentials(String username, String password) throws IOException {
    List<JsonNode> check = searchForUserAndPass(username, password);
    if (username.equals("default")) {
      wipe();
    }
    if (check.size() > 0) {
      System.out.println("connected successfully");
    }
    userRole = getRole(check);

    if (userRole.equals("admin")) {
      return Optional.of(db);
    } else if (userRole.equals("user")) {
      WritingPrivileges wp = new NoWritingPrivileges();
      DatabaseStrategy databaseStrategy = new DatabaseStrategy();
      databaseStrategy.setReadingPrivileges(rp);
      databaseStrategy.setWritingPrivileges(wp);
      return Optional.of(databaseStrategy);
    }
    return Optional.empty();
  }

  private String getRole(List<JsonNode> check) {
    return check.get(0).get("role").asText();
  }

  private void wipe() throws IOException {
    db.delete("username", "default");
  }

  private List<JsonNode> searchForUserAndPass(String username, String password) throws IOException {
    db.selectCollection("credentials");
    db.makeIndexOn("username");
    db.makeIndexOn("password");
    List<JsonNode> userList = db.find("username", username);
    List<JsonNode> passList = db.find("password", password);
    return ListOperations.intersection(userList, passList);
  }

  public String getUserRole() {
    return userRole;
  }
}
