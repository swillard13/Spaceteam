package spaceteam.server.messages.game;

import java.util.List;

import spaceteam.database.HighScore;
import spaceteam.server.messages.Message;

/**
 * Created by Ananth on 11/24/2014.
 */
public class GameOverMessage implements Message
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
