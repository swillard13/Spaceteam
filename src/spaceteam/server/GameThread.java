package spaceteam.server;

import spaceteam.database.DatabaseDriver;
import spaceteam.database.HighScore;
import spaceteam.server.messages.game.Command;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.server.messages.game.HealthMessage;
import spaceteam.server.messages.game.TimeRunOut;
import spaceteam.shared.AbstractWidget;
import spaceteam.shared.Widget;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ananth on 11/22/2014.
 */
public class GameThread extends Thread
{
  
  private static final int DASH_PIECES_PER_PLAYER = 6;
  private final int INITIAL_HEALTH = 10;
  private final int INITIAL_COMMANDS = 20;

  private Player player1;
  private Player player2;
  private PlayerThread player1Thread;
  private PlayerThread player2Thread;
  private GameThread otherGame;
  private Server server;

  private int score = 0;
  private int level = 0;
  private int health;
  private int commandsRemaining;

  private Lock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  private List<Widget> dashPieces;

  public GameThread(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setOtherGame(GameThread otherGame) {
    this.otherGame = otherGame;
  }

  public void generateLevel() {
    level++;
    health = INITIAL_HEALTH;
    commandsRemaining = INITIAL_COMMANDS;
    dashPieces = generateDashPieces();
  }
  
  private List<Widget> generateDashPieces() {
	  List<Widget> pieces = new ArrayList<Widget>();
	  for (int i = 0; i < 2 * DASH_PIECES_PER_PLAYER; ++i) {
		  ArrayList<String> words = DatabaseDriver.getRandomControl();
		  pieces.add(AbstractWidget.generateWidget(words.get(0), words.get(1)));
	  }
	  return pieces;
  }

  public void run() {
    player1Thread = new PlayerThread(player1, player2, this);
    player2Thread = new PlayerThread(player2, player1, this);

    player1Thread.start();
    player2Thread.start();

    generateLevel();
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
    HighScore highScore = new HighScore(score, player1.getName(), player2.getName());
    DatabaseDriver.addHighScore(highScore);
    sendAllMessage(new GameOverMessage(winner, highScore, DatabaseDriver.getHighScores()));
    player1.terminate();
    player2.terminate();
    player1Thread.interrupt();
    player2Thread.interrupt();
  }

  public void sendAllMessage(Serializable s) {
    player1.sendMessage(s);
    player2.sendMessage(s);
  }

  public Lock getLock() {
    return lock;
  }

  public Condition getCondition() {
    return condition;
  }

  public class PlayerThread extends Thread
  {
    private Player player;
    private Player teammate;
    private GameThread gameThread;
    private Command currCommand;

    public PlayerThread(Player player, Player teammate, GameThread gameThread) {
      this.player = player;
      this.teammate = teammate;
      this.gameThread = gameThread;
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
      if(obj instanceof TimeRunOut) {
        gameThread.decrementHealth();
      }
      else if(obj instanceof Command) {

      }
    }
  }
}
