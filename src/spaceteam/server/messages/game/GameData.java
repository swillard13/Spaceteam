package spaceteam.server.messages.game;

import java.io.Serializable;

/**
 * Created by Ananth on 11/24/2014.
 */
public class GameData implements Serializable
{
  private int score;
  private int level;
  private String player1;
  private String player2;

  public GameData(int score, int level, String player1, String player2) {
    this.score = score;
    this.level = level;
    this.player1 = player1;
    this.player2 = player2;
  }
}
