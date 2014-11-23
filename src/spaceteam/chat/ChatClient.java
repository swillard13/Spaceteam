package spaceteam.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Thread
{
	private PrintWriter socketWriter;
	private BufferedReader socketReader;
	private String username;
	private int avatar;
	
	public ChatClient(String hostname, int port, String username, int avatar)
	{
		try 
		{
			Socket s = new Socket(hostname, port);
			socketWriter = new PrintWriter(s.getOutputStream());
			socketReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.username = username;
			
			//SEND USERNAME TO CHAT THREAD IN MESSAGE 
			
			this.start();
			
			//infinite while loop waiting for message from GUI???
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				String message = socketReader.readLine();
				//SEND THE MESSAGE TO THE GUI
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
