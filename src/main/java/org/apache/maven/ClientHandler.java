package org.apache.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
  //
  public static int count = 0;
  private int id;
  //
  private Socket clientSocket;
  private BufferedReader in;
  private PrintWriter out;
  private GooseGame gooseGame;
  private Boolean isRegistered;

  public ClientHandler(Socket socket, GooseGame gooseGame) {
    this.id = count;
    count++;
    this.clientSocket = socket;
    this.gooseGame = gooseGame;
    this.isRegistered = false;
    try {
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      String clientMessage;
      while ((clientMessage = in.readLine()) != null) {
        // Register player
        if (!isRegistered) {
          registerPlayer(clientMessage);
        }
        System.out.println("Received from client: " + clientMessage);

        // TODO: Process player action and update game state using gooseGame instance

        String players = gooseGame.getPlayersName();
        // Broadcast the updated game state to all clients
        GooseGameServer.broadcast("players: " + players); // Replace with your game state
      }

      // Close resources when done
      in.close();
      out.close();
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Send a message to the client
  public void sendMessage(String message) {
    System.out.println("sending");
    out.println(message);
  }

  private void registerPlayer(String name) {
    gooseGame.addPlayer(name);
    this.isRegistered = true;
  }

  // private void processPlayerAction(String clientMessage) {
  // Parse the client message to determine the player action
  // For simplicity, let's assume the client sends a command like "MOVE 3" to move
  // forward 3 spaces.
  // String[] tokens = clientMessage.split(" ");
  // if (tokens.length == 2 && tokens[0].equals("MOVE")) {
  // int spaces = Integer.parseInt(tokens[1]);
  // gooseGame.movePlayer("playerName", spaces); // Replace with the player's name
  // }
  // }

}