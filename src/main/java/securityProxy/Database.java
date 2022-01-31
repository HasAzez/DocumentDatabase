package securityProxy;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface Database {
    boolean createCollection(String collectionName, String jsonSchema) throws IOException;

    void deleteCollection(String collectionName) throws IOException;

    ArrayList<JsonNode> find(String collectionName, String property, String searched) throws IOException;

    ArrayList<JsonNode> findAll(String collectionName) throws IOException;

    void add(String collectionName, String jsonString) throws IOException;

    void delete(String collectionName, String property, String value) throws IOException;

    void makeIndexOn(String collectionName, String propertyName);

    void dumpCollection(String collectionName) throws IOException;

    void importCollection(File file) throws IOException, ClassNotFoundException;
}
