package facade;

import cache.Cache;
import index.JsonCollection;
import com.fasterxml.jackson.databind.JsonNode;
import index.CollectionManager;

import java.util.List;

public class ReadingFacade implements ReadingPrivileges{


    private final CollectionManager collectionManager;
    private final Cache<String, List<JsonNode>> cache;
    private JsonCollection currentCollection;

    public ReadingFacade( Cache<String, List<JsonNode>> cache) {
        this.collectionManager = CollectionManager.INSTANCE;
        this.cache = cache;
    }

    public  List<JsonNode> find(String property, String searched) {
        List<JsonNode> result;
        selectCollection(currentCollection.getName());
        String collectionName = currentCollection.getName();
        if (cache.get(collectionName + property + searched) == null) {
            result = currentCollection.get(property, searched);
            cache.put(collectionName + property + searched, result);
        } else {
            result = cache.get(collectionName + property + searched);
        }
        return result;
    }

    public  List<JsonNode> findAll() {
        selectCollection(currentCollection.getName());
        List<JsonNode> result;
        if ((cache.get(currentCollection.getName() + "all") == null)) {

            result = currentCollection.getAll();
            cache.put(currentCollection.getName() + "all", result);
        } else {
            result = cache.get(currentCollection.getName() + "all");
        }

        return result;
    }


    public List<String> showCollections() {
        return collectionManager.showCollectionNames();
    }

    public String selectCollection(String collectionName) {
        currentCollection =  collectionManager.selectCollection(collectionName);
        return "Selected";
    }

}
