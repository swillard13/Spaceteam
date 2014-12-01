package spaceteam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.mysql.jdbc.PreparedStatement;

public class AddHighScore extends SQLCommand {
	 HighScore newScore;
	 boolean addedHighScore;
	
	
	public AddHighScore(HighScore hs) {
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
			
			String getHighScoresSQL = "SELECT * FROM high_scores GROUP BY score, player_1, player_2 ORDER BY score DESC LIMIT 5";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getHighScoresSQL);
			
			int count = 0;
			while (rs.next()) {
				int score = rs.getInt("score");
				if (newScore.getScore() > score) {
					addedHighScore = true;
				}
				count++;
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
			
//			// find duplicates
//			String findDuplicates = "SELECT * FROM high_scores";
//			Statement findStmt = conn.createStatement();
//			ResultSet fullSet = findStmt.executeQuery(findDuplicates);
//			ArrayList<HighScore> uniqueScores = new ArrayList<HighScore>();
//			ArrayList<Integer> duplicateIDs = new ArrayList<Integer>();
//			while (fullSet.next()) {
//				int score = fullSet.getInt("score");
//				String player1 = fullSet.getString("player_1");
//				String player2 = fullSet.getString("player_2");
//				System.out.println("score: " + score + "  p1: " + player1 + "  p2: " + player2);
//				if (uniqueScores.isEmpty()) {
//					uniqueScores.add(new HighScore(score, player1, player2));
//					System.out.println("added to unique score1");
//				} else {
//					int size = uniqueScores.size();
//					for (int i =0; i < size; i++ ) {
//						HighScore hs = uniqueScores.get(i);
//						if (hs.getScore() == score && hs.getPlayer1().equalsIgnoreCase(player1) && hs.getPlayer2().equalsIgnoreCase(player2)) {
//							Integer id = fullSet.getInt("high_scores_id");
//							duplicateIDs.add(id);
//							System.out.println("found duplicate");
//						} else {
//							uniqueScores.add(new HighScore(score, player1, player2));
//							System.out.println("added to unique score2");
//						}
//					}
//				}
//			}
//			
//			
//			
//			//delete duplicates
//			String deleteDuplicates = "DELETE FROM high_scores WHERE high_scores_id = ?";
//			for (Integer i: duplicateIDs) {
//				java.sql.PreparedStatement deleteRow = conn.prepareStatement(deleteDuplicates);
//				deleteRow.setString(1, i+"");
//				deleteRow.execute();
//			}
//			
			
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

