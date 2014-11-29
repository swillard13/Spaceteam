package spaceteam.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import spaceteam.gui.Spaceteam;
import spaceteam.server.messages.game.Command;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.server.messages.game.HealthMessage;
import spaceteam.server.messages.game.LevelStart;
import spaceteam.server.messages.game.TimeRunOut;
import spaceteam.server.messages.initialization.PlayerInfo;
import spaceteam.shared.AbstractWidget;
import spaceteam.shared.Widget;

public class ClientThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private PlayerInfo playerInfo;
	private Timer tt;
	private int timeLimit;
	private List<Widget> widgetList;
	private Spaceteam parent;

	/*
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
			oos.writeObject(playerInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Passes the new widgets to the GUI method to change the level.
	 * Stores the new time limit for a command and the list of widgets.
	 */
	public void createLevel(LevelStart start) {
		timeLimit = start.getSeconds();
		widgetList = start.getWidgetList();
		parent.createLevel(widgetList);
	}
	
	/*
	 *  Calls the GUI method to display a level complete message to 
	 *  the player and wait for the next level to begin.
	 */
	public void completeLevel() {
		parent.completeLevel();
	}
	
	/*
	 * Calls the GUI method to update the health display to the user.
	 */
	public void updateHealth(HealthMessage health) {
		parent.updateHealth(health.getCurrHealth());
	}
	
	/*
	 * Calls the GUI method to end the game and display high scores.
	 */
	public void endGame(GameOverMessage over) {
		parent.endGame(over);
	}
	
	/*
	 * Calls the GUI method to display the command to the user and 
	 * creates a Timer thread.
	 */
	public void setCommand(Command c) {
		if(tt != null) {
			tt.turnOff();	
		}
		String s = widgetList.get(c.getWidgetId()).getVerb() + " " + 
				widgetList.get(c.getWidgetId()).getName() + " to " + c.getValue();
		parent.displayCommand(s);
		tt = new Timer(this, timeLimit);
	}
	
	/*
	 * Tells the GUI that the player has joined successfully.
	 */
	public void playerJoined() {
		parent.playerJoined();
	}
	
	/*
	 * Tells the GUI that the player needs to choose a new username.
	 */
	public void sameNameError() {
		parent.sameNameError();
	}
	
	
	/*
	 * Sends a message to the server that the time limit was 
	 * exceeded on the command.
	 */
	public void timeEnd() {
		TimeRunOut t = new TimeRunOut();
		try {
			oos.writeObject(t);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	/*
	 * Sends the piece pressed to the server for evaluation.
	 */
	public void piecePressed(Widget widget) {
		try {
			oos.writeObject(widget);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// TODO Implement run
	}

}
