package controller;

import patterns.Database;
import json.utils.Instruction;
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
import java.util.Optional;

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
      Optional<Database> optional = controllerSession.checkCredentials(id, password);
      if (optional.isPresent()) {
        database = optional.get();
      }
      else {
        send("Wrong credentials", socket);
      }
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
        switch (command[0]) {
          case "create":
            send(database.createCollection(command[1], command[2]), socket);
            break;
          case "add":
            send(database.add(command[1], bufferReader.readLine()), socket);
            break;
          case "indexOn":
            send(database.makeIndexOn(command[1], command[2]), socket);
            break;
          case "findAll":
            SaveManager.sendObject(new ArrayList<>(database.findAll(command[1])), socket);
            break;
          case "find":
            SaveManager.sendObject(
                new ArrayList<>(database.find(makeInstruction(command))), socket);
            break;
          case "delete":
            send(database.delete(makeInstruction(command)), socket);
            break;
          case "delete collection":
            send(database.deleteCollection(command[1]), socket);
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
  }

  private Instruction makeInstruction(String[] command) {
    Instruction instruction = new Instruction();
    instruction.setCollectionName(command[1]);
    instruction.setPropertyName(command[2]);
    instruction.setValue(command[3]);
    return instruction;
  }

  public static void send(String message, Socket socket) throws IOException {
    PrintWriter pr = new PrintWriter(socket.getOutputStream());
    pr.println(message);
    pr.flush();
  }
}
