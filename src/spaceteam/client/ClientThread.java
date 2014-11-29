package spaceteam.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import spaceteam.server.messages.game.TimeRunOut;
import spaceteam.server.messages.initialization.PlayerInfo;
import spaceteam.shared.Widget;

public class ClientThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private PlayerInfo playerInfo;
	private int timeLimit;
	// TODO Add parent connection when GUI is added to repository
	//private Spaceteam parent;

	/*
	 * @constructor
	 */
	public ClientThread(/*Spaceteam sp,*/ String hostname, int port, String name) {
		//parent = sp;
		playerInfo = new PlayerInfo(name);
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
	public void createLevel(LevelStart start) {
		timeLimit = start.getSeconds();
		parent.createLevel(start);
	}
	
	public void completeLevel() {
		parent.completeLevel();
	}
	
	public void updateHealth(HealthMessage health) {
		parent.updateHealth(health.getCurrHealth());
	}
	
	public void endGame(GameOver over) {
		parent.endGame(over);
	}
	
	public void setCommand(Command c) {
		parent.displayCommand(c.getMessage());
		TimerThread tt = new TimerThread(this, timeLimit);
	}
	*/
	
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
