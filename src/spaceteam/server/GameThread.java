package spaceteam.server;

/**
 * Created by Ananth on 11/22/2014.
 */
public class GameThread extends Thread
{
  private Player player1;
  private Player player2;
  private GameThread otherGame;

  public GameThread(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setOtherGame(GameThread otherGame) {
    this.otherGame = otherGame;
  }

  public void run() {

  }

  public class PlayerThread extends Thread
  {
    private Player player;
    private Player teammate;

    public PlayerThread(Player player, Player teammate) {
      this.player = player;
      this.teammate = teammate;
    }

    public void run() {

    }
  }
}
