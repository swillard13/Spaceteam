package spaceteamDatabase;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseDriver {
	ReentrantLock scoreLock = new ReentrantLock();
	ReentrantLock controlLock = new ReentrantLock();
	
	public ArrayList<HighScore> getHighScores() {
		scoreLock.lock();
		GetHighScores ghs = new GetHighScores(scoreLock);
		ghs.run();
		scoreLock.unlock();
		return ghs.getList();
	}
	
	public boolean addHighScore(HighScore hs) {
		if (hs == null) {
			return false;
		}
		scoreLock.lock();
		AddHighScore ahs = new AddHighScore(scoreLock, hs);
		ahs.run();
		scoreLock.unlock();
		
		return ahs.getAddedHighScore();
	}
	
	public ArrayList<String> getRandomControl() {
		controlLock.lock();
		GetRandomControl grc = new GetRandomControl(controlLock);
		grc.run();
		controlLock.unlock();
		
		return grc.getList();
	}
}
