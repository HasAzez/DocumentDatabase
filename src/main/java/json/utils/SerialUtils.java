package json.utils;

import java.io.*;
import java.net.Socket;

public class SerialUtils {


    private SerialUtils() {
    }

    public static <T extends Serializable> void save(T object, String name) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(name);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> E load(File file) throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (E) objectInputStream.readObject();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T receiveObject(Socket socket) {
        T receivedObject = null;
        try {
            InputStream inputStream = socket.getInputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            receivedObject = (T)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return receivedObject;
    }
    public static <T> void sendObject(T object, Socket socket) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending object to the node " + socket.getPort());
        objectOutputStream.writeObject(object);
    }

}
