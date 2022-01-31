package securityProxy;

import com.fasterxml.jackson.databind.JsonNode;
import json.utils.ListOperations;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatabaseAccess {

    private final DatabaseFacade db;

    public DatabaseAccess() throws IOException, ClassNotFoundException {
        db = DatabaseFacade.INSTANCE;
        firstTimeWizard();
    }

    private void firstTimeWizard() throws IOException {
        File f = new File("database.db");
        if (!f.exists()) {
            System.out.println("please change your username and password");
            db.createCollection("credentials", "credentials.json");
            db.add("credentials", "{\"username\":\"default\",\"password\":\"default\",\"role\":\"admin\"}");
        }
    }

    public Optional<Database> checkCredentials(String username, String password) throws IOException {
        ArrayList<JsonNode> check = new ArrayList<>(searchForUserAndPass(username, password));
        if (username.equals("default")) {
            wipe();
        }
        if (check.size() > 0) {
            System.out.println("connected successfully");
        }
        if (check.get(0).get("role").asText().equals("admin"))
            return Optional.of(DatabaseFacade.INSTANCE);
        else if (check.get(0).get("role").asText().equals("user"))
            return Optional.of(new UserProxy());
        return Optional.empty();
    }

    private void wipe() throws IOException {
        db.delete("credentials", "username", "default");
        db.delete("credentials", "password", "default");
    }

    private List<JsonNode> searchForUserAndPass(String username, String password) throws IOException {
        db.makeIndexOn("credentials", "username");
        db.makeIndexOn("credentials", "password");
        ArrayList<JsonNode> userList = db.find("credentials", "username", username);
        ArrayList<JsonNode> passList = db.find("credentials", "password", password);
        return ListOperations.intersection(userList, passList);
    }

}
