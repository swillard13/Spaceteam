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
  private Lock commandsLock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  private List<Widget> dashPieces;

  public GameThread(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  /**
   * Sets the concurrently running game thread for the other team.
   * This MUST be done before the run() method is called.
   * @param otherGame the game thread controlling the other team's game
   */
  public void setOtherGame(GameThread otherGame) {
    this.otherGame = otherGame;
  }

  /**
   * Generates a new level for the current game.
   * This will increment the level, reset health and commands, and generate new Widgets.
   * Sends two LevelStart objects to both players, and gets a command for each one.
   */
  public void generateLevel() {
    level++;
    health = INITIAL_HEALTH;
    commandsRemaining = INITIAL_COMMANDS;
    dashPieces = generateDashPieces();
    int commandTime = getCommandTime();
    ArrayList<Widget> player1Pieces = new ArrayList<>();
    ArrayList<Widget> player2Pieces = new ArrayList<>();
    player1Pieces.addAll(dashPieces);
    player2Pieces.addAll(dashPieces);
    player1.sendMessage(new LevelStart(player1Pieces, commandTime, true));
    player2.sendMessage(new LevelStart(player2Pieces, commandTime, false));
    sendAllMessage(new HealthMessage(health));
    getNewCommand(player1Thread);
    getNewCommand(player2Thread);
  }

  /**
   * Generates a random list of Widgets to be used.
   * Gets a list of controls from the database, and creates widgets based off of these.
   * @return a randomly generated list of Widgets to be used for the current level.
   * @see spaceteam.shared.Widget
   * @see spaceteam.database.DatabaseDriver
   */
  private List<Widget> generateDashPieces() {
    List<Widget> pieces = new ArrayList<>();
    ArrayList<GetRandomControl.Control> controls = DatabaseDriver.getRandomControl();
    for(GetRandomControl.Control control : controls) {
      pieces.add(AbstractWidget.generateWidget(control.getControlName(),
                                               control.getCommandVerb()));
    }
    return pieces;
  }

  /**
   * Used to check if the current level has been completed.
   * @return <code>true</code> if the level is finished, <code>false</code> otherwise
   */
  public boolean isLevelFinished() {
	try {
		commandsLock.lock();
		return commandsRemaining == 0;
	} finally {
		commandsLock.unlock();
	}
  }

  /**
   * Instantiates two player threads and starts them.
   * Runs until the game is completed (when one team loses).
   * Waits until both games have finished the level before generating a new one.
   */
  public void run() {
    player1Thread = new PlayerThread(player1, null, this);
    player2Thread = new PlayerThread(player2, player1Thread, this);
    player1Thread.teammate = player2Thread;

    player1Thread.start();
    player2Thread.start();

    while(true) {
      generateLevel();
      try {
        lock.lock();
        while (!isLevelFinished());
        condition.notifyAll();
        lock.unlock();
        if(!otherGame.isLevelFinished()) {
          otherGame.getCondition().await();
        }
      }
      catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Gets the number of seconds that should be allowed for players to execute a command.
   * @return the number of seconds allowed per command
   */
  public int getCommandTime() {
    return 10 - (int) Math.sqrt(level) + 6;
  }

  /**
   * Chooses a random widget and value for the widget.
   * Creates a Command object from these and sets the command for the player
   * @param playerThread the player thread that should have its command set
   * @see spaceteam.server.messages.game.Command
   */
  public void getNewCommand(PlayerThread playerThread) {
    int playerNum = playerThread.getPlayer().getPlayerNum();
    int widgetId;
    if(RANDOM.nextInt(4) == 0) {
      widgetId = RANDOM.nextInt(dashPieces.size());
    }
    else {
      widgetId = RANDOM.nextInt((dashPieces.size() / 2) * (1 + playerNum));
    }
    Widget widget = dashPieces.get(widgetId);
    int newValue = widget.getRandomValue();
    Command command = new Command(widgetId, newValue);
    playerThread.setCurrCommand(command);
  }

  /**
   * Decrements the number of commands left for the level by one.
   * If the number of commands is 0, then the level is finished.
   * It then notifies the current thread's lock condition to signal that the level has ended.
   * @return <code>true</code> if the level has ended, <code>false</code> otherwise
   */
  public synchronized boolean decrementCommands() {
	try {
		commandsLock.lock();
		commandsRemaining--;
		return commandsRemaining == 0;
	} finally {
		commandsLock.unlock();
	}
  }

  /**
   * Decrements the health allotted for the level by one.
   * If the team's health is 0, it triggers a game ending function.
   * Otherwise, it sends a message to each player with the remaining health for that level.
   */
  public synchronized void decrementHealth() {
    health--;
    if(health == 0) {
      triggerGameOver(false);
      otherGame.triggerGameOver(true);
      return;
    }
    sendAllMessage(new HealthMessage(health));
  }

  /**
   * Triggers a game over sequence for the current game thread.
   * Creates and adds a high score to the database with the current team's information.
   * Also gets the high score list from the database and sends this in a GameOverMessage to the players.
   * Interrupts both Player threads and terminates both players.
   * Notifies on the wait condition so that there cannot be a deadlock situation.
   * @param winner true if the game thread's team is the winner, false if they are the loser.
   */
  public void triggerGameOver(boolean winner) {
    HighScore highScore = new HighScore(score, player1.getName(), player2.getName());
    DatabaseDriver.addHighScore(highScore);
    sendAllMessage(new GameOverMessage(winner, highScore, DatabaseDriver.getHighScores()));
    player1Thread.interrupt();
    player2Thread.interrupt();
    player1.terminate();
    player2.terminate();

    lock.lock();
    condition.notifyAll();
    lock.unlock();
  }

  /**
   * Utility function to send a message to both players
   * @param s the message to send to both players
   */
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
    private PlayerThread teammate;
    private GameThread gameThread;
    private Command currCommand;

    public PlayerThread(Player player, PlayerThread teammate, GameThread gameThread) {
      this.player = player;
      this.teammate = teammate;
      this.gameThread = gameThread;
    }

    /**
     * Runs infinitely until the thread is interrupted.
     * Waits for messages from client and executes the message.
     */
    public void run() {
      while(true) {
        try {
          executeMessage(player.getIn().readObject());
        }
        catch(IOException | ClassNotFoundException e) {
//          e.printStackTrace();
          gameThread.triggerGameOver(false);
          otherGame.triggerGameOver(true);
          return;
        }
      }
    }

    /**
     * Sets the current command for the player, and notifies the player of this command.
     * @param c the command to be set and sent to the client
     */
    public void setCurrCommand(Command c) {
      this.currCommand = c;
      player.sendMessage(currCommand);
    }

    /**
     * Executes a message recieved from the client.
     * The two possible objects from the client are handled here:
     * <ol>
     * <li>
     * TimeRunOut: Decrement the health and get a new command.
     * </li>
     * <li>
     * Command: Check if the command is equal to the goal command.
     * Get a new command and decrement number of commands remaining if it is.
     * </li>
     * </ol>
     *
     * @param obj the message received from the client.
     * @see spaceteam.server.messages.game.Command
     */
    private void executeMessage(Object obj) {
      if(obj instanceof TimeRunOut) {
        gameThread.decrementHealth();
        gameThread.getNewCommand(this);
      }
      else if(obj instanceof Command) {
        Command command = (Command) obj;
        if(command.equals(currCommand)) {
          score += 10 * level;
          if(!gameThread.decrementCommands()) {
            gameThread.getNewCommand(this);
          }
        }
        else if(command.equals(teammate.getCurrCommand())) {
          score += 10 * level;
          if(!gameThread.decrementCommands()) {
            gameThread.getNewCommand(teammate);
          }
        }
      }
    }

    public Player getPlayer() {
      return player;
    }

    public Command getCurrCommand() {
      return currCommand;
    }
  }
}
