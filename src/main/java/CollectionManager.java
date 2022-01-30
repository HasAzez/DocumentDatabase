import Indexing.JsonCollection;
import json.utils.SaveManager;

import java.io.*;
import java.util.List;

public class CollectionManager implements Serializable {

    private final List<JsonCollection> jsonCollections;
    private JsonCollection currentCollection;


    private CollectionManager(List<JsonCollection> jsonCollections) {
        this.jsonCollections = jsonCollections;
    }


    public void deleteCollection(String name) {
        if (currentCollection.getName().equals(name))
            currentCollection = null;
        jsonCollections.removeIf(j -> j.getName().equals(name));
    }

    public boolean selectCollection(String name) {
        for (JsonCollection collection :
                jsonCollections) {
            if (collection.getName().equals(name)) {
                currentCollection = collection;
                return true;
            }

        }
        return false;
    }

    public boolean addCollection(JsonCollection collection) {
        return jsonCollections.add(collection);
    }

    public void dumpCollection() throws IOException {
        SaveManager.save(currentCollection, currentCollection.getName() + ".coll");
    }

    public void importCollection(File location) throws IOException, ClassNotFoundException {
        jsonCollections.add(SaveManager.load(location));
    }

    public static CollectionManager newInstance(List<JsonCollection> list) {
        return new CollectionManager(list);
    }


    public void setCurrentCollection(JsonCollection currentCollection) {
        this.currentCollection = currentCollection;
    }

    public JsonCollection getCurrentCollection() {
        return currentCollection;
    }
}
