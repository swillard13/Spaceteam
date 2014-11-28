package spaceteam.server;

import spaceteam.server.messages.game.GameData;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.server.messages.game.HealthMessage;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Ananth on 11/22/2014.
 */
public class GameThread extends Thread
{
  private final int INITIAL_HEALTH = 10;
  private final int INITIAL_COMMANDS = 20;

  private Player player1;
  private Player player2;
  private PlayerThread player1Thread;
  private PlayerThread player2Thread;
  private GameThread otherGame;

  private int score = 0;
  private int level = 0;
  private int health;
  private int commandsRemaining;

  public GameThread(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setOtherGame(GameThread otherGame) {
    this.otherGame = otherGame;
  }

  public void generateLevel() {
    health = INITIAL_HEALTH;
    commandsRemaining = INITIAL_COMMANDS;

  }

  public void run() {
    player1Thread = new PlayerThread(player1, player2);
    player2Thread = new PlayerThread(player2, player1);

    player1Thread.start();
    player2Thread.start();

    while(true) {
      level++;
      generateLevel();

    }
  }

  public synchronized void decrementHealth() {
    health--;
    if(health == 0) {
      triggerGameOver(false);
      otherGame.triggerGameOver(true);
      return;
    }
    sendAllMessage(new HealthMessage(health));
  }

  public void triggerGameOver(boolean winner) {
    GameData gameData = new GameData(score, level, player1.getName(), player2.getName());
    sendAllMessage(new GameOverMessage(winner, gameData, null)); // TODO: Get list of high scores
    player1.terminate();
    player2.terminate();
    player1Thread.interrupt();
    player2Thread.interrupt();
  }

  public void sendAllMessage(Serializable s) {
    player1.sendMessage(s);
    player2.sendMessage(s);
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
      while(true) {
        try {
          executeMessage(player.getIn().readObject());
        }
        catch(IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }

    private void executeMessage(Object obj) {

    }
  }
}
