package spaceteam.database;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseDriver {

	public static ArrayList<HighScore> getHighScores() {
		GetHighScores ghs = new GetHighScores();
		ghs.run();
		return ghs.getList();
	}
	
	public static boolean addHighScore(HighScore hs) {
		if (hs == null) {
			return false;
		}
		AddHighScore ahs = new AddHighScore(hs);
		ahs.run();

		return ahs.getAddedHighScore();
	}
	
	public static ArrayList<GetRandomControl.Control> getRandomControl() {
		GetRandomControl grc = new GetRandomControl();
		grc.run();
		
		return grc.getList();
	}
}
