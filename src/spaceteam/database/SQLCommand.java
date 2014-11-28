package spaceteam.database;

import java.util.concurrent.locks.ReentrantLock;

public abstract class SQLCommand implements Runnable {
	public static final String DB_ADDRESS = "jdbc:mysql://localhost:3306/";
	public static final String DB_NAME = "spaceteam";
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "";
	protected ReentrantLock queryLock;
	
	
	public SQLCommand(ReentrantLock queryLock)
	{
		this.queryLock = queryLock;
	}
	
	@Override
	public void run() {
		execute();
	}
	
	public abstract boolean execute();
}
