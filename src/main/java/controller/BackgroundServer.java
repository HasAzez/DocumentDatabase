package controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public enum BackgroundServer {
    INSTANCE(new ArrayList<>());

    private final List<Node> users;

    BackgroundServer(List<Node> users) {
        this.users = users;
    }


    public <T extends Serializable>  void connect(ServerSocket serverSocket,T object) throws IOException {

        Socket node =  serverSocket.accept();
        Node connection = new Node(node);
        connection.setPort(9000+ users.size()+"");
        System.out.println("New node connected");
        subscribe(connection);
        connection.send(object);

    }
    public void subscribe(Node listener) {
        users.add(listener);
    }

    public void unsubscribe(Node listener) {
        users.remove(listener);
    }

    public <T extends Serializable> void broadcast(T object) throws IOException {

        for (Node user : users) {
            user.send(object);
        }
    }

    public <T extends Serializable>  void runServer(T object) throws IOException {
        ServerSocket nodesController = new ServerSocket(8989);
        Runnable r = () -> {
            try {
                while (true)
                    connect(nodesController,object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r).start();
    }

    public List<String> getPorts(){
        List<String> ports = new ArrayList<>();
        for(Node node:users){
            ports.add(node.getPort());
        }
        return ports;
    }

    }


