package controller;

import patterns.Database;
import json.utils.Instruction;
import index.CollectionManager;
import json.utils.SerialUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Optional;

public class PrimaryServer {
  @SuppressWarnings("InfiniteLoopStatement")
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
@SuppressWarnings("InfiniteLoopStatement")
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
      Database database = getDatabaseFromCredentials(bufferReader, controllerSession);
      if (notifyThisIsController(controllerSession, database)) return;

      while (true) {
        String[] command = bufferReader.readLine().split(" ");
        switch (command[0]) {
          case "create":
            send(database.createCollection(command[1],bufferReader.readLine()), socket);
            break;
          case "update":
            send(database.update(makeInstruction(command),bufferReader.readLine()), socket);
            break;
          case "add":
            send(database.add(command[1], bufferReader.readLine()), socket);
            break;
          case "indexOn":
            send(database.makeIndexOn(command[1], command[2]), socket);
            break;
          case "findAll":
            SerialUtils.sendObject(new ArrayList<>(database.findAll(command[1])), socket);
            break;
          case "find":
            SerialUtils.sendObject(
                new ArrayList<>(database.find(makeInstruction(command))), socket);
            break;
          case "delete":
            send(database.delete(makeInstruction(command)), socket);
            break;
          case "delete collection":
            send(database.deleteCollection(command[1]), socket);
            break;
          case "show":
            SerialUtils.sendObject(new ArrayList<>(database.showCollections()), socket);
            break;
        }
      }

    } catch (SocketException e) {
      System.out.println("Client disconnected");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean notifyThisIsController(ControllerSession controllerSession, Database database)
      throws IOException {
    send(database.getPorts().toString(), socket);
    if (controllerSession.getUserRole().equals("admin")) {
      send("you can continue here or connect to any one of these above", socket);
    } else if (controllerSession.getUserRole().equals("user")) {
      send("you are a user please connect to reading node", socket);
      socket.close();
      return true;
    }
    return false;
  }

  private Database getDatabaseFromCredentials(
      BufferedReader bufferReader, ControllerSession controllerSession) throws IOException {
    String id = bufferReader.readLine();
    String password = bufferReader.readLine();
    Optional<Database> optional = controllerSession.checkCredentials(id, password);
    Database database = null;
    if (optional.isPresent()) {
      database = optional.get();
    } else {
      send("Wrong credentials", socket);
    }
    return database;
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
