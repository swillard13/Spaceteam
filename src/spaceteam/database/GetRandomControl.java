package spaceteam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GetRandomControl extends SQLCommand{
	 private ArrayList<String> controlInfoList;
		
		
		public GetRandomControl(ReentrantLock queryLock) {
			super(queryLock);
		}
		
		public ArrayList<String> getList() {
			return controlInfoList;
		}

		@SuppressWarnings("static-access")
		@Override
		public boolean execute() {
			try {
				Class.forName(super.DRIVER).newInstance();
				Connection conn = DriverManager.getConnection(super.DB_ADDRESS + super.DB_NAME, super.USER, super.PASSWORD);
				
				String numRacesSQL = "SELECT * FROM controls ORDER BY RAND() LIMIT 1";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(numRacesSQL);
				
				controlInfoList = new ArrayList<String>();
				
				if (rs.next()) {
					String control = rs.getString("control");
					String verb = rs.getString("command_verb");
					controlInfoList.add(control);
					controlInfoList.add(verb);
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
