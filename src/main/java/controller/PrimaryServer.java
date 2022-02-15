package controller;

import facade.Database;
import index.CollectionManager;
import json.utils.SaveManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class PrimaryServer {

  public static void main(String[] args) {
    BackgroundServer backgroundServer = BackgroundServer.INSTANCE;
    try {
      backgroundServer.runServer(new ArrayList<>(CollectionManager.INSTANCE.getJsonCollections()));
      ServerSocket serverSocket = new ServerSocket(8000);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("New client connected");
        new ServerThread(socket).start();
      }

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

class ServerThread extends Thread {
  private final Socket socket;

  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
      BufferedReader bufferReader = new BufferedReader(inputStream);
      ControllerSession controllerSession = new ControllerSession();
      Database database = null;
      String id = bufferReader.readLine();
      String password = bufferReader.readLine();
      database = controllerSession.checkCredentials(id, password).get();
      send(database.getPorts().toString(), socket);
      if (controllerSession.getUserRole().equals("admin")) {
        send("you can continue here or connect to any one of these above", socket);
      } else if (controllerSession.getUserRole().equals("user")) {
        send("you are a user please connect to reading node", socket);
        socket.close();
        return;
      }
      while (true) {
        String[] command = bufferReader.readLine().split(" ");
        System.out.println(command[0]);
        switch (command[0]) {
          case "create":
            database.createCollection(command[1], command[2]);
            System.out.println("Collection created");
            break;
          case "select":
            database.selectCollection(command[1]);
            System.out.println(command[1] + " selected");
            break;
          case "add":
            System.out.println("please enter the Json");
            database.add(bufferReader.readLine());
            break;
          case "index on":
            database.makeIndexOn(command[1]);
            break;
          case "findAll":
            SaveManager.sendObject(new ArrayList<>(database.findAll()), socket);
            break;
          case "find":
            SaveManager.sendObject(new ArrayList<>(database.find(command[1], command[2])), socket);
            break;
          case "delete":
            database.delete(command[1], command[2]);
            break;
          case "delete collection":
            database.deleteCollection();
            break;
          case "show":
            SaveManager.sendObject(new ArrayList<>(database.showCollections()), socket);
            break;
        }
      }

    } catch (SocketException e) {
      System.out.println("Client disconnected");
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally{
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


  }

  public static void send(String message, Socket socket) throws IOException {
    PrintWriter pr = new PrintWriter(socket.getOutputStream());
    pr.println(message);
    pr.flush();
  }
}
