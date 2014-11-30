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
  public static final int SERVER_PORT = 8888;
  public static final int CHAT_PORT = 6789;
  
  private int port;
  private ServerSocket serverSocket;
  private ServerSocket chatServerSocket;
  private List<Player> playerList;
  private GameThread game1;
  private GameThread game2;
  private Vector<ChatThread> chatThreads;

  /**
   * Instantiates Server object, which waits for players to join and initializes the game threads.
   * @param port the port number to start the server on
   */
  public Server(int port) {
    try {
      this.port = port;
      playerList = new ArrayList<>();
      serverSocket = new ServerSocket(port);
      chatServerSocket = new ServerSocket(CHAT_PORT);
      chatThreads = new Vector<>();
      System.out.printf("Connect clients to: %s\n", InetAddress.getLocalHost().getHostAddress());
      waitForPlayers();
      initializeGame();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Waits for four players to join the game. Players with the same name are rejected.
   * Also instantiates the chat threads for the chat server.
   */
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
            
            out.close();
            in.close();
            socket.close();
            chatThreads.remove(ct);
            continue outer;
          }
        }
        Player p = new Player(playerInfo.getName(), socket, out, in, i);
        p.sendMessage(new AcceptedPlayer());
        playerList.add(p);
      }
      catch(IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Initializes the game threads and sends messages for the chat threads.
   * Pairs Player 0 with Player 1, and Player 2 with Player 3.
   * This is taken from the order they join the game.
   */
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

  /**
   * Finds and sends a message to another chat thread based on the parsed field RECIPIENTS.
   * Matches the client thread based on the associated username, and sends the message.
   * @param message the message to be sent to another client
   * @param sender the chat thread sending the message
   */
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
			  if (c.getUsername() != null && c.getUsername().equals(rec))
			  {
				  c.send(words);
			  }
		  }
	  }
  }

  public static void main(String[] args) {
    Server s = new Server(SERVER_PORT);
  }
}
