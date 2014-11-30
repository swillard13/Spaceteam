package spaceteam.database;

import java.util.ArrayList;

public class TestDatabase {
	public static void main (String[] args) {
//		dd.addHighScore(new HighScore(500, "Bonnie", "Clyde"));
//		dd.addHighScore(new HighScore(400, "Batman", "Robin"));
//		dd.addHighScore(new HighScore (250, "Elastagirl", "Mr. Incredible"));
//		dd.addHighScore(new HighScore (200, "Player 1", "Player 2"));
//		dd.addHighScore(new HighScore(300, "Belle", "Beast"));

		
		for (HighScore s: DatabaseDriver.getHighScores()) {
			System.out.println("Score: " + s.getScore() + " " + s.getPlayer1() + " " + s.getPlayer2());
		}

		//try to add high score that isn't high score
//		dd.addHighScore(new HighScore(100, "Alpha", "Beta"));
//		dd.addHighScore(new HighScore(30, "homer", "marge"));
//
//		for (HighScore s: dd.getHighScores()) {
//			System.out.println("Score: " + s.getScore() + " " + s.getPlayer1() + " " + s.getPlayer2());
//		}
		
		//get random controls
		for (int i = 0; i < 5; i++) {
			ArrayList<GetRandomControl.Control> controlData = DatabaseDriver.getRandomControl();
			for (GetRandomControl.Control c : controlData) {
				System.out.print(c.getControlName());
				System.out.println(c.getCommandVerb());
			}
			System.out.println();
		}
	}
}
