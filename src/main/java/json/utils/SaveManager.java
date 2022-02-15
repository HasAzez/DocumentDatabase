package json.utils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
    public static ArrayList<String> receiveObject(Socket socket) {
        ArrayList<String> dd = null;
        try {
            InputStream inputStream = socket.getInputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            dd = (ArrayList<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return dd;
    }
    public static <T extends Serializable> void sendObject(T object, Socket socket) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending object to the node " + socket.getPort());
        objectOutputStream.writeObject(object);
    }

}
