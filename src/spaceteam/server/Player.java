package spaceteam.server;

import spaceteam.server.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

  public Player(String name, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
    this.name = name;
    this.socket = socket;
    this.out = out;
    this.in = in;
  }

  public synchronized void sendMessage(Message message) {
    try {
      out.writeObject(message);
      out.flush();
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

  public void terminate() {
    try {
      out.close();
      in.close();
      socket.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
