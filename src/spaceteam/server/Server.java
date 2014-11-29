package spaceteam.server;

import spaceteam.chat.ChatThread;
import spaceteam.database.DatabaseDriver;
import spaceteam.server.messages.initialization.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Ananth on 11/22/2014.
 */
public class Server
{
  public static final int CHAT_PORT = 6789;
  
  private int port;
  private ServerSocket serverSocket;
  private ServerSocket chatServerSocket;
  private List<Player> playerList;
  private GameThread game1;
  private GameThread game2;
  private Vector<ChatThread> chatThreads;

  public Server(int port) {
    try {
      this.port = port;
      playerList = new ArrayList<>();
      serverSocket = new ServerSocket(port);
      chatServerSocket = new ServerSocket(CHAT_PORT);
      chatThreads = new Vector<ChatThread>();
      System.out.printf("Connect clients to: %s\n", InetAddress.getLocalHost().getHostAddress());
      waitForPlayers();
      initializeGame();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  private void waitForPlayers() {

    outer:
    for(int i = 0; i < 4; ++i) {
      try {

          ChatThread ct = new ChatThread(chatServerSocket.accept(), this);
          chatThreads.add(ct);
        Socket socket = serverSocket.accept();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        PlayerInfo playerInfo = (PlayerInfo) in.readObject();
        for(Player p : playerList) {
          if(playerInfo.getName().equals(p.getName())) {
            --i;
            out.writeObject(new SameNameError());
            out.flush();
            in.close();
            out.close();
            continue outer;
          }
        }
        for(Player p : playerList) {
          p.sendMessage(new PlayerJoined());
        }
        playerList.add(new Player(playerInfo.getName(), socket, out, in));
        out.writeObject(new AcceptedPlayer());
        out.flush();
      }
      catch(IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
  private void initializeGame() {
    for(Player p : playerList) {
      p.sendMessage(new GameStarted());
    }
    game1 = new GameThread(playerList.get(0), playerList.get(1));
    game2 = new GameThread(playerList.get(2), playerList.get(3));

  //send teammate usernames to the chat clients
	  chatThreads.get(0).send("USERNAME: " + playerList.get(0).getName());
	  chatThreads.get(0).send("TEAMMATE: " + playerList.get(1).getName());
	
	  chatThreads.get(1).send("USERNAME: " + playerList.get(1).getName());
	  chatThreads.get(1).send("TEAMMATE: " + playerList.get(0).getName());

	  chatThreads.get(2).send("USERNAME: " + playerList.get(2).getName());
	  chatThreads.get(2).send("TEAMMATE: " + playerList.get(3).getName());

	  chatThreads.get(3).send("USERNAME: " + playerList.get(3).getName());
	  chatThreads.get(3).send("TEAMMATE: " + playerList.get(2).getName());
	  
    game1.setOtherGame(game2);
    game2.setOtherGame(game1);

    game1.start();
    game2.start();
  }
  
  public void sendMessage(String message, ChatThread sender)
  {
	  int messageIndex = 9;
	  int recipientsIndex = message.lastIndexOf("RECIPIENTS: ");
	  String words = message.substring(messageIndex, recipientsIndex);
	  String rec = message.substring(recipientsIndex + 12);
	  String[] usernames = rec.split(", ");
	  
	  for (ChatThread c : chatThreads)
	  {
		  for (int i = 0; i < usernames.length; i++)
		  {
			  if (c.getUsername().equals(rec))
			  {
				  c.send(words);
			  }
		  }
	  }
  }
  
  public static void main(String[] args) {
    Server s = new Server(8888);
  }
}
