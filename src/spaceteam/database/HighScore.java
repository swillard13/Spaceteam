package spaceteam.database;

import java.io.Serializable;

public class HighScore implements Serializable
{
	private int score;
	private String player1, player2;
	
	public HighScore (int score, String player1, String player2) {
		this.score = score;
		this.player1 = player1;
		this.player2 = player2;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getPlayer1() {
		return player1;
	}
	
	public String getPlayer2() {
		return player2;
	}
}
