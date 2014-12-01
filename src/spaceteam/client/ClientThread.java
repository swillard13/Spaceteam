package spaceteam.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import spaceteam.gui.Spaceteam;
import spaceteam.server.GameThread;
import spaceteam.server.messages.game.Command;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.server.messages.game.HealthMessage;
import spaceteam.server.messages.game.LevelFinish;
import spaceteam.server.messages.game.LevelStart;
import spaceteam.server.messages.game.TimeRunOut;
import spaceteam.server.messages.initialization.AcceptedPlayer;
import spaceteam.server.messages.initialization.GameStarted;
import spaceteam.server.messages.initialization.PlayerInfo;
import spaceteam.server.messages.initialization.SameNameError;
import spaceteam.shared.PushButtonWidget;
import spaceteam.shared.ToggleButtonWidget;
import spaceteam.shared.Widget;

public class ClientThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private PlayerInfo playerInfo;
	private Timer tt;
	private int timeLimit;
	private List<Widget> widgetList;
	private Spaceteam parent;

	/**
	 * Constructor for ClientThread
	 * @constructor
	 */
	public ClientThread(Spaceteam sp, String hostname, int port, String name) {
		parent = sp;
		playerInfo = new PlayerInfo(name);
		tt = null;
		timeLimit = 0;
		try {
			socket = new Socket(hostname, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject(playerInfo);
			oos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Passes the new widgets to the GUI method to change the level.
	 * Stores the new time limit for a command and the list of widgets.
	 */
	public void createLevel(LevelStart start) {
		timeLimit = start.getSeconds();
		widgetList = start.getWidgetList();
		if(start.getIfFirst()) {
			parent.createLevel(widgetList.subList(0, GameThread.DASH_PIECES_PER_PLAYER), true);
		} else {
			parent.createLevel(widgetList.subList(GameThread.DASH_PIECES_PER_PLAYER, 
					2*GameThread.DASH_PIECES_PER_PLAYER), false);
		}
	}
	
	/**
	 *  Calls the GUI method to display a level complete message to 
	 *  the player and wait for the next level to begin.
	 */
	public void completeLevel() {
		parent.completeLevel();
	}
	
	/**
	 * Calls the GUI method to update the health display to the user.
	 */
	public void updateHealth(HealthMessage health) {
		parent.updateHealth(health.getCurrHealth());
	}
	
	/**
	 * Calls the GUI method to end the game and display high scores.
	 */
	public void endGame(GameOverMessage over) {
		parent.endGame(over);
	}
	
	/**
	 * Calls the GUI method to display the command to the user and 
	 * creates a Timer thread.
	 */
	public void setCommand(Command c) {
		if(tt != null) {
			tt.turnOff();	
		}
		String s;
		if(widgetList.get(c.getWidgetId()) instanceof PushButtonWidget || 
				widgetList.get(c.getWidgetId()) instanceof ToggleButtonWidget) {
			s = widgetList.get(c.getWidgetId()).getVerb() + " " + widgetList.get(c.getWidgetId()).getName();
		}
		else {
			s = widgetList.get(c.getWidgetId()).getVerb() + " " + 
					widgetList.get(c.getWidgetId()).getName() + " to " + c.getValue();
		}
		parent.displayCommand(s);
		tt = new Timer(this, timeLimit);
		tt.start();
	}
	
	/**
	 * Tells the GUI that the player has joined successfully.
	 */
	public void acceptedPlayer() {
		parent.acceptedPlayer();
	}
	
	/**
	 * Tells the GUI that the player needs to choose a new username.
	 */
	public void sameNameError() {
		parent.sameNameError();
	}
	
	/**
	 * Tells the GUI that the game has started.
	 */
	private void gameStarted() {
		parent.gameStarted();
	}
	
	/**
	 * Sends a message to the server that the time limit was 
	 * exceeded on the command.
	 */
	public void timeEnd() {
		TimeRunOut t = new TimeRunOut();
		try {
			oos.writeObject(t);
			oos.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a command object and sends the command to the server for evaluation.
	 */
	public void piecePressed(int widgetId, int value) {
		try {
			Command c = new Command(widgetId, value);
			oos.writeObject(c);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void run() {
		try {
			while (true) {
				Object obj = ois.readObject();

				if (obj instanceof Command) {
					setCommand((Command) obj);
				} else if (obj instanceof LevelStart) {
					createLevel((LevelStart) obj);
				} else if (obj instanceof HealthMessage) {
					updateHealth((HealthMessage) obj);
				} else if (obj instanceof GameOverMessage) {
					endGame((GameOverMessage) obj);
					break;
				} else if (obj instanceof AcceptedPlayer) {
					acceptedPlayer();
				} else if (obj instanceof GameStarted) {
					gameStarted();
				} else if (obj instanceof SameNameError) {
					sameNameError();
					break;
				} else if (obj instanceof LevelFinish) {
					completeLevel();
				}
			}
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Error reading object from server. Exiting now.");
		}
	}

	public void updateTime(int current, int total) {
		parent.updateTime(current, total);
	}

}
