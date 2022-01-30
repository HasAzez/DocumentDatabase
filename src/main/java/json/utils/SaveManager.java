package json.utils;

import java.io.*;

public class SaveManager {


    private SaveManager() {
    }

    public static <T extends Serializable> void save(T object, String name) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(name);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        }
    }


    public static <E> E load(File file) throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (E) objectInputStream.readObject();
        }
    }
}
