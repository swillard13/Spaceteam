package spaceteam.server;

import spaceteam.server.messages.initialization.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ananth on 11/22/2014.
 */
public class Server
{
  private int port;
  private ServerSocket serverSocket;
  private List<Player> playerList;
  private GameThread game1;
  private GameThread game2;

  public Server(int port) {
    try {
      this.port = port;
      playerList = new ArrayList<>();
      serverSocket = new ServerSocket(port);
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

    game1.setOtherGame(game2);
    game2.setOtherGame(game1);

    game1.start();
    game2.start();
  }

  public static void main(String[] args) {
    Server s = new Server(8888);
  }
}
