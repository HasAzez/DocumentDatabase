import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Database {

    private List<JsonCollection> jsonCollections;
    private JsonCollection currentCollection;
    private static  Database db = new Database();
    private Database() {
        jsonCollections = new ArrayList<>();
    }


    public void deleteCollection(String name){
        if (currentCollection.getName().equals(name))
            currentCollection = null;
        jsonCollections.removeIf(j->j.getName().equals(name));
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

    public JsonCollection createCollection(String name) {
        JsonCollection jsonCollection = new JsonCollection(name);
        jsonCollections.add(jsonCollection);
        return jsonCollection;
    }

    public void dumpCollection() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(currentCollection.getName() + ".ndb"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(currentCollection);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public void importCollection(File location) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(location);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        jsonCollections.add((JsonCollection) objectInputStream.readObject());
        objectInputStream.close();
        fileInputStream.close();
    }

    public void insert(String jsonDocument) throws JsonProcessingException {
        currentCollection.insert(jsonDocument);
    }

    public void delete(String id) {
        currentCollection.deleteUsingID(id);
    }

    public JsonNode get(String id) {

        return currentCollection.getDocument(id);
    }
    public ArrayList<JsonNode> get(String property, String searched) {

        return currentCollection.getCertain(property,searched);
    }

    public ArrayList<JsonNode> getPropertyDocuments( String searched) {
        return currentCollection.getCertainFromIndexed( searched);
    }

    public Collection<JsonNode> getAll() {

        return currentCollection.getAll();
    }

    public static Database getDb() {
        return db;
    }

    public void setCurrentCollection(JsonCollection currentCollection) {
        this.currentCollection = currentCollection;
    }

    public String getNameOfCurrentCollection() {
        return currentCollection.getName();
    }
    public int getCountOfCurrentCollection(){
        return currentCollection.getCount();
    }
}
