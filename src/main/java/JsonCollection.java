import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class JsonCollection {
    private final ConcurrentHashMap<String, JsonObject> uniqueIndexedMap;
    private final ConcurrentHashMap<String, ArrayList<JsonObject>> propertyIndexedMap;
    private final String name;
    private boolean isPropertyIndexed;
    private String indexProperty;
    private int count;


    public JsonCollection(String name) {
        this.propertyIndexedMap = new ConcurrentHashMap<>();
        this.uniqueIndexedMap = new ConcurrentHashMap<>();
        this.name = name;
        count = 0;
    }


    public void insert(String jsonSentence) {

        String wrapped = wrapID(jsonSentence);
        JsonParser jsonParser = new JsonParser();
        JsonObject uniqueIndexedJson = jsonParser.parse(wrapped).getAsJsonObject();
        uniqueIndexedMap.put(DigestUtils.sha1Hex(count + ""), uniqueIndexedJson);
        if (isPropertyIndexed)
            insertToPropertyMap(uniqueIndexedJson);
        count++;

    }

    private String wrapID(String jsonSentence) {
        String hashedIndex = DigestUtils.sha1Hex(count + "");
        return "{\"_id\":\"" + hashedIndex + "\"," + jsonSentence.substring(1);
    }


    public boolean deleteUsingID(String key) {
        if (uniqueIndexedMap.isEmpty() || uniqueIndexedMap.get(key) == null)
            return false;

        if (isPropertyIndexed) {
            String specificProperty = uniqueIndexedMap.get(key).get(indexProperty).getAsString();

            ArrayList<JsonObject> wantedBucket = propertyIndexedMap.get(specificProperty);
            wantedBucket.removeIf(jo -> jo.get("_id").getAsString().equals(key));
        }
        uniqueIndexedMap.remove(key);


        count--;

        return true;
    }

    public Collection<JsonObject> getAll() {
        return uniqueIndexedMap.values();
    }

    public ArrayList<JsonObject> getCertainFromIndexed(String searched) throws NoSuchFieldException {
        if (isPropertyIndexed) {
            return propertyIndexedMap.get(searched);
        } else {
            throw new NoSuchFieldException();
        }

    }

    public void makeIndexed(String indexProperty) {
        setIndexProperty(indexProperty);
        boolean haveValuesCondition = !isPropertyIndexed && !uniqueIndexedMap.isEmpty();
        if (haveValuesCondition) {
            for (JsonObject f :
                    uniqueIndexedMap.values()) {
                insertToPropertyMap(f);
            }
        }
        isPropertyIndexed = true;

    }

    public boolean isEmpty() {
        return uniqueIndexedMap.isEmpty();
    }

    private void insertToPropertyMap(JsonObject f) {
        String index = f.get(indexProperty).getAsString();
        boolean checkIfWereBefore = propertyIndexedMap.get(index) == null;
        if (checkIfWereBefore) {
            propertyIndexedMap.put(index, new ArrayList<>());
        }
        propertyIndexedMap.get(index).add(f);
    }

    public void setIndexProperty(String indexProperty) {
        this.indexProperty = indexProperty;
    }
}
