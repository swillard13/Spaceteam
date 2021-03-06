package spaceteam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GetHighScores extends SQLCommand {
	 ArrayList<HighScore> highScoresList;
	
	public ArrayList<HighScore> getList() {
		return highScoresList;
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean execute() {
		try {
			Class.forName(super.DRIVER).newInstance();
			Connection conn = DriverManager.getConnection(super.DB_ADDRESS + super.DB_NAME, super.USER, super.PASSWORD);
			
			String numRacesSQL = "SELECT * FROM high_scores GROUP BY score, player_1, player_2 ORDER BY score DESC LIMIT 5";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(numRacesSQL);
			
			highScoresList = new ArrayList<>();
			
			while (rs.next()) {
				int score = rs.getInt("score");
				String player1 = rs.getString("player_1");
				String player2 = rs.getString("player_2");
				highScoresList.add(new HighScore(score, player1, player2));
			}
		} catch (InstantiationException | IllegalAccessException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}


		return false;
	}
	
	

}
