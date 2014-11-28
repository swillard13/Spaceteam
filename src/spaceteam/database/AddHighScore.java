package spaceteam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantLock;

public class AddHighScore extends SQLCommand {
	 HighScore newScore;
	 boolean addedHighScore;
	
	
	public AddHighScore(ReentrantLock queryLock, HighScore hs) {
		super(queryLock);
		newScore = hs;
		addedHighScore = false;
	}
	
	public boolean getAddedHighScore() {
		return addedHighScore;
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean execute() {
		try {
			Class.forName(super.DRIVER).newInstance();
			Connection conn = DriverManager.getConnection(super.DB_ADDRESS + super.DB_NAME, super.USER, super.PASSWORD);
			
			String getHighScoresSQL = "SELECT * FROM high_scores LIMIT 5";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getHighScoresSQL);
			
			int count = 0;
			while (rs.next()) {
				int score = rs.getInt("score");
				if (newScore.getScore() > score) {
					addedHighScore = true;
				}
			}
			
			if (count == 0) {
				addedHighScore = true;
			}
			
			if (addedHighScore) {
				String addHighScoreSQL = "INSERT INTO high_scores (score, player_1, player_2) VALUES (?,?,?)";
				java.sql.PreparedStatement newScoreStmt = conn.prepareStatement(addHighScoreSQL);
				newScoreStmt.setString(1, newScore.getScore()+"");
				newScoreStmt.setString(2, newScore.getPlayer1());
				newScoreStmt.setString(3, newScore.getPlayer2());
				newScoreStmt.execute();
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		return false;
	}
	
	

}

