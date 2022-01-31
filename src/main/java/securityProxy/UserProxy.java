package securityProxy;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UserProxy implements Database {
    private final DatabaseFacade dbInstance = DatabaseFacade.INSTANCE;

    @Override
    public boolean createCollection(String collectionName, String jsonSchema) {
        noAccess();
        return false;
    }

    private void noAccess() {
        System.out.println("User doesn't have access to this command ");
    }

    @Override
    public void deleteCollection(String collectionName) {
        noAccess();

    }

    @Override
    public ArrayList<JsonNode> find(String collectionName, String property, String searched) throws IOException {
        return dbInstance.find(collectionName, property, searched);
    }

    @Override
    public ArrayList<JsonNode> findAll(String collectionName) throws IOException {
        return dbInstance.findAll(collectionName);
    }

    @Override
    public void add(String collectionName, String jsonString) {
        noAccess();
    }

    @Override
    public void delete(String collectionName, String property, String value) {
        noAccess();
    }

    @Override
    public void makeIndexOn(String collectionName, String propertyName) {
        dbInstance.makeIndexOn(collectionName, propertyName);
    }

    @Override
    public void dumpCollection(String collectionName) {
        noAccess();
    }

    @Override
    public void importCollection(File file) {
        noAccess();
    }
}
