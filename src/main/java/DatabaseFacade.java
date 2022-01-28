import Cache.Cache;
import Cache.FIFOCache;
import Indexing.JsonCollection;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFacade implements Serializable {
    private final  CollectionManger collectionManger = CollectionManger.getCollectionManger();
    private  transient JsonSchemaValidator validator;
    private final Cache<String, ArrayList<JsonNode>> cache = new FIFOCache<String, ArrayList<JsonNode>>(100);
    private  static  DatabaseFacade db =load();
    private final List<NodesMaster> users;
    private final transient ServerSocket nodesController;

    private DatabaseFacade() throws IOException {
        users = new ArrayList<>();
        nodesController = new ServerSocket(8989);

    }
    public void createCollection(String collectionName) throws IOException {
        JsonCollection jsonCollection = new JsonCollection(collectionName);
        collectionManger.addCollection(jsonCollection);
        commit();
    }

    public void deleteCollection(String collectionName) throws IOException {
        collectionManger.deleteCollection(collectionName);
        commit();
    }


    public ArrayList<JsonNode> find(String collectionName, String property, String searched) throws IOException {
        ArrayList<JsonNode> result;
        if (cache.get(collectionName + property + searched) == null) {
            collectionManger.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManger.getCurrentCollection();
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
            collectionManger.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManger.getCurrentCollection();
            result = jsonCollection.getAll();
            cache.put(collectionName + "all", result);
        } else {
           result= cache.get(collectionName + "all");
        }


        return result;
    }

    public void add(String collectionName, String jsonString) throws IOException {
        if (validator.isValid(jsonString)) {
            collectionManger.selectCollection(collectionName);
            JsonCollection jsonCollection = collectionManger.getCurrentCollection();
            jsonCollection.insert(jsonString);
        } else {
            System.out.println(validator.errorList(jsonString));
        }
        commit();

    }

    public void delete(String collectionName, String property, String value) throws IOException {
        if (cache.containsKey(collectionName+property+ value)){
            cache.remove(collectionName+property+ value);
        }
        collectionManger.selectCollection(collectionName);
        JsonCollection jsonCollection = collectionManger.getCurrentCollection();
        cache.remove(collectionName+"all");
        jsonCollection.delete(property, value);
        commit();
    }

    private void commit() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("database.db");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(db);
        broadcast();
        objectOutputStream.close();
        fileOutputStream.close();
    }
    public static DatabaseFacade getInstance() throws IOException {
        return db;

    }
    private static DatabaseFacade load()  {

        try(FileInputStream fileInputStream = new FileInputStream("database.db");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            return (DatabaseFacade) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
        try {
            return  new DatabaseFacade();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setValidator(String schemaName) {
        this.validator = new JsonSchemaValidator(schemaName);
    }
    public void makeIndexOn(String collectionName, String propertyName) {
        collectionManger.selectCollection(collectionName);
        JsonCollection jsonCollection = collectionManger.getCurrentCollection();
        jsonCollection.makeIndexOn(propertyName,jsonCollection.getAll());
    }
    public void dumpCollection(String collectionName) throws IOException {
        collectionManger.selectCollection(collectionName);
        collectionManger.dumpCollection();
    }
    public void importCollection(File file) throws IOException, ClassNotFoundException {
      collectionManger.importCollection(file);
    }
    public  void connect() throws IOException {
        Socket node =  nodesController.accept();
        System.out.println("New Node connected");
        NodesMaster f =  new NodesMaster(node);
        f.run();
        subscribe(f);
    }
    public void subscribe(NodesMaster listener) {
        users.add(listener);
    }

    public void unsubscribe( NodesMaster listener) {
        users.remove(listener);
    }

    public void broadcast() {
        for (NodesMaster listener : users) {
            listener.run();
        }
    }

}
