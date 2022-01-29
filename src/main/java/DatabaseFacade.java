import Cache.Cache;
import Cache.FIFOCache;
import Indexing.JsonCollection;
import com.fasterxml.jackson.databind.JsonNode;
import json.utils.JsonSchemaValidator;
import json.utils.SaveManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum DatabaseFacade {
    INSTANCE;
    private final CollectionManager collectionManager;
    private final Cache<String, ArrayList<JsonNode>> cache;
    private final Map<String, JsonSchemaValidator> validators;

    DatabaseFacade() {
        cache = new FIFOCache<String, ArrayList<JsonNode>>(100);
        collectionManager = CollectionManager.newInstance(new ArrayList<>());
        validators = new HashMap<>();
    }

    public void createCollection(String collectionName, String jsonSchema) throws IOException {
        JsonCollection jsonCollection = new JsonCollection(collectionName);
        collectionManager.addCollection(jsonCollection);
        validators.put(collectionName, new JsonSchemaValidator(jsonSchema));
        commit();
    }

    public void deleteCollection(String collectionName) throws IOException {
        collectionManager.deleteCollection(collectionName);
        commit();
    }


    public ArrayList<JsonNode> find(String collectionName, String property, String searched) throws IOException {
        ArrayList<JsonNode> result;
        if (cache.get(collectionName + property + searched) == null) {
            collectionManager.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManager.getCurrentCollection();
            result = jsonCollection.get(property, searched);
            cache.put(collectionName + property + searched, result);
        } else {
            result = cache.get(collectionName + property + searched);
        }


        return result;
    }

    public ArrayList<JsonNode> findAll(String collectionName) throws IOException {
        ArrayList<JsonNode> result;
        if ((cache.get(collectionName + "all") == null)) {
            collectionManager.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManager.getCurrentCollection();
            result = jsonCollection.getAll();
            cache.put(collectionName + "all", result);
        } else {
            result = cache.get(collectionName + "all");
        }


        return result;
    }

    public void add(String collectionName, String jsonString) throws IOException {
        JsonSchemaValidator validator = validators.get(collectionName);
        if (validator.isValid(jsonString)) {
            collectionManager.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManager.getCurrentCollection();
            jsonCollection.insert(jsonString);
        } else {
            System.out.println(validator.errorList(jsonString));
        }
        commit();

    }

    public void delete(String collectionName, String property, String value) throws IOException {
        if (cache.containsKey(collectionName + property + value)) {
            cache.remove(collectionName + property + value);
        }
        collectionManager.selectCollection(collectionName);
        JsonCollection jsonCollection = collectionManager.getCurrentCollection();
        cache.remove(collectionName + "all");
        jsonCollection.delete(property, value);
        commit();
    }

    private void commit() throws IOException {
        SaveManager.save(INSTANCE, "database.db");
    }

    public void makeIndexOn(String collectionName, String propertyName) {
        collectionManager.selectCollection(collectionName);
        JsonCollection jsonCollection = collectionManager.getCurrentCollection();
        jsonCollection.makeIndexOn(propertyName, jsonCollection.getAll());
    }

    public void dumpCollection(String collectionName) throws IOException {
        collectionManager.selectCollection(collectionName);
        collectionManager.dumpCollection();
    }

    public void importCollection(File file) throws IOException, ClassNotFoundException {
        collectionManager.importCollection(file);
    }


}
