import BTree.BTree;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    final private int M = 4;
    private Map<String, JsonCollection> collections;
    private static Database db = new Database();


    private Database() {
        collections = new ConcurrentHashMap<>();
    }


    public void createCollection(String name) {
         JsonCollection jsonCollection = new JsonCollection(name);
         collections.put(name, jsonCollection);
    }

    public JsonCollection selectCollection(String name) {

        return collections.get(name);

    }

    public boolean deleteCollection(String name) {
        if (collections.containsKey(name)) {
            collections.remove(name);
            return true;
        }
        return false;
    }


    public Object load(String filepath) {

        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void save(BTree bpt, String name) {


        try {

            FileOutputStream fileOut = new FileOutputStream(name + ".hsn");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(bpt);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Database getDb() {
        return db;
    }

}
