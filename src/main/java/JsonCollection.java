import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class JsonCollection {
    private final HashMap<String, JsonObject> uniqueIndexedMap;
    private final HashMap<String, ArrayList<JsonObject>> propertyIndexedMap;
    private final String name;
    private boolean isPropertyIndexed = false;
    private String indexProperty;
    private int count;



    public  JsonCollection(String name) {

        this.propertyIndexedMap = new HashMap<>() ;
        this.uniqueIndexedMap = new HashMap<>();
        this.name = name;
        count = 0;
    }


    public synchronized  void insert(String jsonSentence) {




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


    public synchronized boolean deleteUsingID(String key) {
        if (uniqueIndexedMap.isEmpty() || uniqueIndexedMap.get(key) == null) {
            return false;
        }
        if (isPropertyIndexed) {
            String specificProperty = uniqueIndexedMap.get(key).get(indexProperty).getAsString();

            ArrayList<JsonObject> wantedBucket = propertyIndexedMap.get(specificProperty);
            wantedBucket.removeIf(jo -> jo.get("_id").getAsString().equals(key));
        }
        uniqueIndexedMap.remove(key);


        count--;
        return true;
    }

    public   Collection<JsonObject> getAll() {
        try {
            Collection<JsonObject> values = uniqueIndexedMap.values();

            return values;
        } finally {
        }

    }


    public  ArrayList<JsonObject> getCertainFromIndexed(String searched) {
        try {
            if (isPropertyIndexed) {
                ArrayList<JsonObject> jsonObjects = propertyIndexedMap.get(searched);

                return jsonObjects;
            } else {
                return null;
            }
        } finally {
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

    private synchronized void insertToPropertyMap(JsonObject f) {
        try {
            String index = f.get(indexProperty).getAsString();
            boolean checkIfWereBefore = propertyIndexedMap.get(index) == null;
            if (checkIfWereBefore) {
                propertyIndexedMap.put(index, new ArrayList<>());
            }
            propertyIndexedMap.get(index).add(f);
        } finally {
        }
    }

    public void setIndexProperty(String indexProperty) {
        this.indexProperty = indexProperty;
    }

    public int getCount() {
        return count;
    }
}
