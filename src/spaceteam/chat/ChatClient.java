package spaceteam.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JList;

public class ChatClient extends Thread
{
	private PrintWriter socketWriter;
	private BufferedReader socketReader;
	private String username, teammateUsername;
	private int avatar;
	private Vector<String> messages;
	private JList<String> chatMessages;
	
	public ChatClient(String hostname, int port, String username, int avatar, Vector<String> messages, JList<String> chatMessages)
	{
		try 
		{
			Socket s = new Socket(hostname, port);
			socketWriter = new PrintWriter(s.getOutputStream());
			socketReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.username = username;
			this.avatar = avatar;
			this.messages = messages;
			this.chatMessages = chatMessages;
			this.start();
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
	
	public void sendFromGUI(String message)
	{
		socketWriter.println(message);
		socketWriter.flush();
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				String message = socketReader.readLine();
				//SEND THE MESSAGE TO THE GUI
				if (message.contains("USERNAME"))
				{
					String[] parts = message.split(": ");
					username = parts[1];
					socketWriter.println(message);
					socketWriter.flush();
					continue;
				}
				else if (message.contains("TEAMMATE"))
				{
					String[] parts = message.split(": ");
					teammateUsername = parts[1];
					socketWriter.println(message);
					socketWriter.flush();
					continue;
				}
				else
				{
					messages.add(teammateUsername + ": " + message);
					chatMessages.setListData(messages);
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
