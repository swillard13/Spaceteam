package spaceteam.server;

import spaceteam.server.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ananth on 11/22/2014.
 */
public class Player
{
  private String name;
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private int playerNum;

  public Player(String name, Socket socket, ObjectOutputStream out, ObjectInputStream in, int playerNum) {
    this.name = name;
    this.socket = socket;
    this.out = out;
    this.in = in;
    this.playerNum = playerNum % 2;
  }

  /**
   * Sends an object that implements Message.
   * @param message the Message object to be sent
   */
  public synchronized void sendMessage(Message message) {
    try {
      if(!socket.isClosed()) {
        out.writeObject(message);
        out.flush();
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  public String getName() {
    return name;
  }

  public Socket getSocket() {
    return socket;
  }

  public ObjectOutputStream getOut() {
    return out;
  }

  public ObjectInputStream getIn() {
    return in;
  }

  public int getPlayerNum() {
    return playerNum;
  }

  /**
   * The method closes the input and output streams and the connection socket.
   * Used after the game has finished to close open connections.
   */
  public void terminate() {
    try {
      if(!socket.isClosed()) {
        socket.close();
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
