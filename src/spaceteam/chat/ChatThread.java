package spaceteam.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import spaceteam.server.Server;

public class ChatThread extends Thread
{
	private Socket socket;
	private Server server;
	private PrintWriter printWriter;
	private String username;
	private String teammateUsername;
	
	public ChatThread(Socket socket, Server server)
	{
		try 
		{
			this.socket = socket;
			this.server = server;
			printWriter = new PrintWriter(socket.getOutputStream());
			this.start();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void send(String message)
	{
		printWriter.println(message);
		printWriter.flush();
	}
	
	public void run()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true)
			{
				String message = br.readLine();
				if (message.contains("USERNAME"))
				{
					String[] parts = message.split(": ");
					username = parts[1];
					continue;
				}
				else if (message.contains("TEAMMATE"))
				{
					String[] parts = message.split(": ");
					teammateUsername = parts[1];
					continue;
				}
				else
				{
					message = "MESSAGE: " + message + " RECIPIENTS: " + teammateUsername;
					server.sendMessage(message, this);
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public String getUsername()
	{
		return username;
	}
}
