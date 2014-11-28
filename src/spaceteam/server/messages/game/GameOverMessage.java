package spaceteam.server.messages.game;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ananth on 11/24/2014.
 */
public class GameOverMessage implements Serializable
{
  private boolean winner;
  private GameData gameData;
  private List<GameData> highScoreList;

  public GameOverMessage(boolean winner, GameData gameData, List<GameData> highScoreList) {
    this.winner = winner;
    this.gameData = gameData;
    this.highScoreList = highScoreList;
  }

  public boolean isWinner() {
    return winner;
  }

  public GameData getGameData() {
    return gameData;
  }

  public List<GameData> getHighScoreList() {
    return highScoreList;
  }
}
