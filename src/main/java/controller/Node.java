package controller;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;


public class Node {

    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private String port;

    public Node(Socket socket) throws IOException {
        this.socket = socket;
        OutputStream outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
    }

    public <T extends Serializable>  void send(T object) throws IOException {

        System.out.println("Sending object to the node " + socket.getPort());
        objectOutputStream.writeObject(object);
        objectOutputStream.reset();
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }
}


