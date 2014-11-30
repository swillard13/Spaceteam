package spaceteam.server;

import spaceteam.database.DatabaseDriver;
import spaceteam.database.GetRandomControl;
import spaceteam.database.HighScore;
import spaceteam.server.messages.Message;
import spaceteam.server.messages.game.Command;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.server.messages.game.HealthMessage;
import spaceteam.server.messages.game.LevelStart;
import spaceteam.server.messages.game.TimeRunOut;
import spaceteam.shared.AbstractWidget;
import spaceteam.shared.Widget;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ananth on 11/22/2014.
 */
public class GameThread extends Thread
{

  public static final int DASH_PIECES_PER_PLAYER = 6;
  private final int INITIAL_HEALTH = 10;
  private final int INITIAL_COMMANDS = 20;

  private final Random RANDOM = new Random();

  private Player player1;
  private Player player2;
  private PlayerThread player1Thread;
  private PlayerThread player2Thread;
  private GameThread otherGame;

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
    int commandTime = getCommandTime();
    player1.sendMessage(new LevelStart(dashPieces.subList(0, DASH_PIECES_PER_PLAYER), commandTime));
    player2.sendMessage(new LevelStart(dashPieces.subList(DASH_PIECES_PER_PLAYER, 2 * DASH_PIECES_PER_PLAYER), commandTime));
    getNewCommand(player1Thread);
    getNewCommand(player2Thread);
  }

  private List<Widget> generateDashPieces() {
    List<Widget> pieces = new ArrayList<>();
    ArrayList<GetRandomControl.Control> controls = DatabaseDriver.getRandomControl();
    for(GetRandomControl.Control control : controls) {
      pieces.add(AbstractWidget.generateWidget(control.getControlName(),
                                               control.getCommandVerb()));
    }
    return pieces;
  }

  public boolean isLevelFinished() {
    return commandsRemaining == 0;
  }

  public void run() {
    player1Thread = new PlayerThread(player1, player2, this);
    player2Thread = new PlayerThread(player2, player1, this);

    player1Thread.start();
    player2Thread.start();

    while(true) {
      generateLevel();
      try {
        lock.lock();
        condition.await();
        lock.unlock();
        if(!otherGame.isLevelFinished()) {
          otherGame.getCondition().await();
        }
      }
      catch(InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public int getCommandTime() {
    return 10 - (int) Math.sqrt(level) + 6;
  }

  public void getNewCommand(PlayerThread playerThread) {
    int widgetId = RANDOM.nextInt(dashPieces.size());
    Widget widget = dashPieces.get(widgetId);
    int newValue = widget.getRandomValue();
    Command command = new Command(widgetId, newValue);
    playerThread.setCurrCommand(command);
  }

  public synchronized boolean decrementCommands() {
    commandsRemaining--;
    if(commandsRemaining == 0) {
      condition.notifyAll();
      return true;
    }
    return false;
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
    player1Thread.interrupt();
    player2Thread.interrupt();
    player1.terminate();
    player2.terminate();
    condition.notifyAll();
  }

  public void sendAllMessage(Message s) {
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

    public void setCurrCommand(Command c) {
      this.currCommand = c;
      player.sendMessage(currCommand);
    }

    private void executeMessage(Object obj) {
      if(obj instanceof TimeRunOut) {
        gameThread.decrementHealth();
        gameThread.getNewCommand(this);
      }
      else if(obj instanceof Command) {
        Command command = (Command) obj;
        if(command.equals(currCommand)) {
          if(!gameThread.decrementCommands()) {
            gameThread.getNewCommand(this);
          }
        }
      }
    }
  }
}
