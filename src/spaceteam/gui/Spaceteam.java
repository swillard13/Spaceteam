package spaceteam.gui;

/*Team Members:
 * Michelle Agcamaran
 * Matthew Burke
 * Ellen Emerson
 * Ananth Mohan
 * Rochelle Willard
 * Nathan Yanaga
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import spaceteam.chat.ChatClient;
import spaceteam.client.ClientThread;
import spaceteam.server.GameThread;
import spaceteam.server.Server;
import spaceteam.server.messages.game.GameOverMessage;
import spaceteam.shared.InteractionListener;
import spaceteam.shared.Widget;
import spaceteam.database.HighScore;

public class Spaceteam extends JFrame implements ActionListener, MouseListener{
	
	JPanel chatPane, healthPanel, controlPanel, timePanel, commandPanel, mainPane, cardsGeneral, gamePane,
	waitForPlayersPane, waitForTeamPane, gameCard, endCard, iconSelect;
	JPanelWithBackground startCard;
	ArrayList<String> controls;
	JTextArea userMessage;
	JButton continueButton;
	Dimension contDimensions, wpDimensions;
	JTextField username;
	ArrayList<JButton> icons;
	Vector<String> messages;
	JList<String> chatMessages;
	JScrollPane jsp;
	JLabel enterUsername, commandText, messagesLabel, waitPlayers, outcomeLabel, scoreLabel;
    JTable highScoresTable;
	JButton sendMessageBtn;
	ClientThread client;
	ChatClient chat;
	String hostname;
	int avatar;
	JLabel avatarLabel;
	
	static final String START = "Start Screen";
	static final String GAMEPLAY = "Gameplay";
	static final String END = "End Screen";
	static final String GAME = "Game";
	static final String WAIT_TEAM = "Waiting for Other Team";
	
	public Spaceteam(){
		//setup main window
		super("Spaceteam");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 620);
		setMinimumSize(new Dimension(800, 620));
		setMaximumSize(new Dimension(800, 620));
		setLocationRelativeTo(null);
		
		//create labels/text in order to set the font
		enterUsername = new JLabel("Enter a username:");
		username = new JTextField(15);
		commandText = new JLabel("");
		messagesLabel = new JLabel("Messages");
		userMessage = new JTextArea(3, 14);
		sendMessageBtn = new JButton("Send");
		
		//load font, later edit to keep the try/catch at the top and set the fonts of the necessary labels only
		Font font;
		try {
			GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
			font = Font.createFont(Font.TRUETYPE_FONT, new File("src/spaceteam/gui/armata-regular-webfont.ttf"));
			ge.registerFont(font);
			font = font.deriveFont(Font.PLAIN,20);
			enterUsername.setFont(font);
			username.setFont(font);
			commandText.setFont(font);
			messagesLabel.setFont(font);
			Font btnFont = font.deriveFont(Font.PLAIN,12);
			sendMessageBtn.setFont(btnFont);
		} catch (IOException|FontFormatException e) {
		     //Handle exception
		}
		
		//load icons
		icons = new ArrayList<JButton>();
		for (int i = 1; i <= 6; i++)
		{
			ImageIcon imgicon = new ImageIcon("src/spaceteam/gui/avatar" + Integer.toString(i) + ".png");
			Image img = imgicon.getImage();
			img = img.getScaledInstance(65, 65, 0);
			JButton button = new JButton(new ImageIcon(img));
			button.setPreferredSize(new Dimension(65, 65));
			int temp = i;
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)
				{
					avatar = temp;
					button.getModel().setPressed(true);
					continueButton.setEnabled(true);
				}
			});
			icons.add(button);
		}
		
		cardsGeneral = new JPanel(new CardLayout());
		
		//Set up starting screen panel
		startCard = new JPanelWithBackground("src/spaceteam/gui/spaceteamsplash1.jpg");
		startCard.setLayout(null);
		enterUsername.setForeground(Color.white);
		Dimension eunDimensions = enterUsername.getPreferredSize();
		enterUsername.setBounds(80, 440, eunDimensions.width, eunDimensions.height);
		startCard.add(enterUsername);
		username.setPreferredSize(new Dimension(username.getWidth(), 36));
		Dimension un = username.getPreferredSize();
		username.setBounds(77, 470, un.width, un.height);
		startCard.add(username);
		iconSelect = new JPanel(new GridLayout(2,3));
		for(int i = 0; i < 6; i++){
			iconSelect.add(icons.get(i));
		}
		Dimension iconSelectDimension = iconSelect.getPreferredSize();
		iconSelect.setBounds(480, 420, iconSelectDimension.width, iconSelectDimension.height);
		startCard.add(iconSelect);
		
		continueButton = new JButton("Continue");
		continueButton.addActionListener(this);
		contDimensions = continueButton.getPreferredSize();
		continueButton.setBounds(360, 540, contDimensions.width, contDimensions.height);
		startCard.add(continueButton);
		continueButton.setEnabled(false);
		
		//Set general panel layout for main pane
		gameCard = new JPanel();
		gameCard.setLayout(new BoxLayout(gameCard, BoxLayout.X_AXIS));
		
		mainPane = new JPanel(new CardLayout());
		mainPane.setSize(550, 600);
		
		//set up "Waiting for other players card" using JPanel w/ ImageIcon rather than JPanelWithBackground to enable animation
		waitForPlayersPane = new JPanel(null);
		JLabel waitPlayersPic = new JLabel(new ImageIcon("src/spaceteam/gui/waitingforplayers.gif"));
		wpDimensions = waitPlayersPic.getPreferredSize();
		waitPlayersPic.setBounds(0, 0, wpDimensions.width, wpDimensions.height);
		waitForPlayersPane.add(waitPlayersPic);
		
		gamePane = new JPanel();
		gamePane.setLayout(new BoxLayout(gamePane, BoxLayout.Y_AXIS));
		
		//Set up "waiting for other team" card when Level is completed by one team.
		waitForTeamPane = new JPanel(null);
		JLabel waitTeamPic = new JLabel(new ImageIcon("src/spaceteam/gui/waitingforteam.gif"));
		wpDimensions = waitTeamPic.getPreferredSize();
		waitTeamPic.setBounds(0, 0, wpDimensions.width, wpDimensions.height);
		waitForTeamPane.add(waitTeamPic);
		
		//Set up end screen panel
		endCard = new JPanel();
        endCard.setBackground(Color.BLACK);
        endCard.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        outcomeLabel = new JLabel("You win!");
        outcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(Box.createRigidArea(new Dimension(10,10)));
        topPanel.add(outcomeLabel);
        topPanel.add(Box.createRigidArea(new Dimension(40,40)));
        scoreLabel = new JLabel("Score: 23");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(scoreLabel);
        endCard.add(topPanel, BorderLayout.NORTH);
        
        JLabel highScoresLabel = new JLabel("HIGH SCORES", JLabel.CENTER);
        highScoresLabel.setFont(new Font("Arial", Font.BOLD, 24));
        highScoresLabel.setForeground(Color.CYAN);
        endCard.add(highScoresLabel, BorderLayout.CENTER);
        
        highScoresTable = new JTable (new DefaultTableModel(new Object[] {"Score", "Player 1", "Player 2"},0) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        });
        highScoresTable.setBackground(Color.BLACK);
        highScoresTable.setForeground(Color.CYAN);
        JScrollPane jsp = new JScrollPane(highScoresTable);
        endCard.add(jsp, BorderLayout.SOUTH);
		
		
		//set up health bar
		healthPanel = new HealthBar();
		healthPanel.setPreferredSize(new Dimension(540, 100));
		
		//set up commands
		commandPanel = new JPanel();
		commandPanel.setPreferredSize(new Dimension(540, 50));
		commandPanel.setBackground(Color.GRAY);
		commandText.setOpaque(true);
        //tempCommand.setBorder(new EmptyBorder(10,10,10,10));
		commandText.setPreferredSize(new Dimension(530, 40));
		commandText.setBackground(Color.BLACK);
		commandText.setHorizontalAlignment(SwingConstants.CENTER);
		commandText.setForeground(Color.GREEN);
		commandPanel.add(commandText);
		
		//set up time remaining bar
		timePanel = new TimeBar();
		timePanel.setPreferredSize(new Dimension(540, 50));
		
		//set up controls
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(540, 400));
		controlPanel.setLayout(new GridLayout(2,3));
		
		//set up chat pane
		chatPane = new JPanel();
		chatPane.setPreferredSize(new Dimension(260,600));
		chatPane.setMinimumSize(new Dimension(260,600));
		chatPane.setMaximumSize(new Dimension(260,600));
		chatPane.setBackground(new Color(10,47,105));
		
		JPanel topChatPanel = new JPanel();
		topChatPanel.add(messagesLabel, BorderLayout.CENTER);
		avatarLabel = new JLabel();
		avatarLabel.setPreferredSize(new Dimension(65, 65));
		topChatPanel.setBackground(new Color(10, 47, 105));
		topChatPanel.add(avatarLabel, BorderLayout.SOUTH);
		topChatPanel.setPreferredSize(new Dimension(260, 65));
		messagesLabel.setForeground(Color.white);
		chatPane.add(topChatPanel, BorderLayout.NORTH);
		messages = new Vector<String>();
		chatMessages = new JList<String>();
		jsp = new JScrollPane(chatMessages);
		jsp.setPreferredSize(new Dimension(240, 430));
		chatPane.add(jsp, BorderLayout.CENTER);
		//Change up the lookandfeel. temporarily looks hideous
		JPanel sendMessagePanel = new JPanel();
		sendMessagePanel.setBackground(new Color(10, 47, 130)); //a5c7d9
		userMessage.setLineWrap(true);
		userMessage.setWrapStyleWord(true);
		JScrollPane userMessageJSP = new JScrollPane(userMessage);
		userMessageJSP.setViewportView(userMessage);
		userMessage.setEditable(false);
		sendMessagePanel.add(userMessageJSP);
		sendMessageBtn.setPreferredSize(new Dimension(80, 30));
		sendMessageBtn.setForeground(Color.WHITE);
		sendMessageBtn.setBackground(Color.BLACK);
		sendMessageBtn.setOpaque(true);
		sendMessageBtn.setBorderPainted(false);
		sendMessageBtn.setEnabled(false);
		sendMessagePanel.add(sendMessageBtn);
		chatPane.add(sendMessagePanel, BorderLayout.SOUTH);
		
		//add action listener to send button
		sendMessageBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//make sure list updates
				messages.add(username.getText() + ": " + userMessage.getText());
				chatMessages.setListData(messages);
				//chat.sendFromGUI(message);
				chat.sendFromGUI(userMessage.getText());
				userMessage.setText("");
			}
		});
		
		//add components to the window
		cardsGeneral.add(startCard, START);
		
		gamePane.add(healthPanel);
		gamePane.add(commandPanel);
		gamePane.add(timePanel);
		gamePane.add(controlPanel);
		mainPane.add(waitForPlayersPane, "Waiting for Other Players");
		mainPane.add(gamePane, "Game");
		mainPane.add(waitForTeamPane, "Waiting for Other Team");
		CardLayout cl1 = (CardLayout)(mainPane.getLayout());
		cl1.show(mainPane, "Waiting for Other Players");
		gameCard.add(mainPane);
		gameCard.add(chatPane);
		
		cardsGeneral.add(gameCard, GAMEPLAY);
		
		cardsGeneral.add(endCard, END);
		
		add(cardsGeneral);
		
		CardLayout cl = (CardLayout)(cardsGeneral.getLayout());
		cl.show(cardsGeneral, START);
		
		mainPane.getRootPane().setDefaultButton(sendMessageBtn);
		startCard.getRootPane().setDefaultButton(continueButton);
	}
	
	public static void main(String [] args){
		Spaceteam st = new Spaceteam();
		if (args.length == 1) {
			st.setHostname(args[0]);
		} else {
			System.out.print("Host Name: ");
			Scanner scan = new Scanner(System.in);
			st.setHostname(scan.next());
			scan.close();
		}
		st.setVisible(true);
	}
	
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == continueButton){
			//Send user information to the client!!!
			if (username.getText().trim().length() == 0)
			{
				blankNameError();
				return;
			}
			createClient();
			ImageIcon imgicon = new ImageIcon("src/spaceteam/gui/avatar" + Integer.toString(avatar) + ".png");
			Image img = imgicon.getImage();
			img = img.getScaledInstance(65, 65, 0);
			imgicon.setImage(img);
			avatarLabel.setIcon(imgicon);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Updates the Health bar to the current health.
	 */
	public void updateHealth(int health) {
		((HealthBar) healthPanel).updateHealthBar(health);
	}
	
	/**
	 * Update the time to show how much time is left.
	 * @param current the time remaining
	 * @param total the total time
	 */
	public void updateTime(int current, int total) {
		((TimeBar)timePanel).currentTimeRemaining(current, total);
	}
	
	/**
	 * Changes the screen to say game over and displays high scores.
	 */
	public void endGame(GameOverMessage over) {
		// TODO (nathan)
        if (over.isWinner()) {
            outcomeLabel.setText("YOU WIN!");
        } else {
            outcomeLabel.setText("You Lose");
        }
        
        int score = over.getHighScore().getScore();
        scoreLabel.setText("Score: " + score);
        
        List<HighScore> highScores = over.getHighScoreList();
        DefaultTableModel dtm = (DefaultTableModel) highScoresTable.getModel();
        
        for (HighScore hs: highScores) {
            Object[] rowData = {hs.getScore(), hs.getPlayer1(), hs.getPlayer2()};
            dtm.addRow(rowData);
        }
        
		CardLayout cl = (CardLayout)(cardsGeneral.getLayout());
		cl.show(cardsGeneral, END);
	}
	
	/**
	 * Tells the player that the level was completed and waits for the 
	 * signal to start a new level.
	 */
	public void completeLevel() {
		// TODO Auto-generated method stub
		CardLayout cl = (CardLayout)(mainPane.getLayout());
		cl.show(mainPane, WAIT_TEAM);
	}
	
	/**
	 * Creates the new level.
	 */
	public void createLevel(List<Widget> widgetList, boolean first) {
		CardLayout cl = (CardLayout)(mainPane.getLayout());
		cl.show(mainPane, GAME);
		controlPanel.removeAll();
		for (int i = 0; i < widgetList.size(); i++) {
			Widget w = widgetList.get(i);
			final int id;
			if(first) {
				id = i;
			} else {
				id = i + GameThread.DASH_PIECES_PER_PLAYER;
			}
			w.addInteractionListener(new InteractionListener() {
				@Override
				public void interactionOccurred(int value) {
					client.piecePressed(id, value);
				}
			});
			controlPanel.add(w.getComponent());
			
		}
	}
	
	/**
	 * Tells the user to choose a different name.
	 */
	public void sameNameError() {
		username.setForeground(Color.RED);
		username.setText("Error: That username is already taken!");
	}
	
	/**
	 * Tells the user the name is blank
	 */
	public void blankNameError() {
		username.setForeground(Color.RED);
		username.setText("Error: The username cannot be blank!");
	}
	
	/**
	 * Changes the current command displayed.
	 */
	public void displayCommand(String s) {
		commandText.setText(s);
	}
	
	/**
	 * Changes the card to the waiting room.
	 */
	public void acceptedPlayer() {
		CardLayout cl = (CardLayout)(cardsGeneral.getLayout());
		cl.show(cardsGeneral, GAMEPLAY);	
	}
	
	/**
	 * Begins the game.
	 */
	public void gameStarted() {
		//TODO: Implement countdown and teammate notification
		//For now do nothing

		userMessage.setEditable(true);
		sendMessageBtn.setEnabled(true);
	}
	
	/**
	 * Creates client and chat.
	 */
	public void createClient() {
		//TODO figure out port/hostname/ip/whatever to connect to server
		chat = new ChatClient(hostname, Server.CHAT_PORT, username.getText(), 0, messages, chatMessages);
		client = new ClientThread(this, hostname, 8888, username.getText());
		client.start();
	}

	

}
