package spaceteam.server.messages.game;

import spaceteam.database.HighScore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ananth on 11/24/2014.
 */
public class GameOverMessage implements Serializable
{
  private boolean winner;
  private HighScore highScore;
  private List<HighScore> highScoreList;

  public GameOverMessage(boolean winner, HighScore highScore, List<HighScore> highScoreList) {
    this.winner = winner;
    this.highScore = highScore;
    this.highScoreList = highScoreList;
  }

  public boolean isWinner() {
    return winner;
  }

  public HighScore getHighScore() {
    return highScore;
  }

  public List<HighScore> getHighScoreList() {
    return highScoreList;
  }
}
