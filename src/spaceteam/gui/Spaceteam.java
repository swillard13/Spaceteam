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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class Spaceteam extends JFrame implements ActionListener, MouseListener{

	JPanel chatPane, healthPanel, controlPanel, timePanel, commandPanel, mainPane, cardsGeneral, gamePane,
	waitForPlayersPane, waitForTeamPane, gameCard, endCard, iconSelect;
	JPanelWithBackground startCard;
	ArrayList<String> controls;
	JTextArea userMessage;
	JButton continueButton, waitPlayers;
	Dimension contDimensions, wpDimensions;
	JTextField username;
	ArrayList<JLabel> icons;
	Vector<String> messages;
	JList<String> chatMessages;
	JScrollPane jsp;
	JLabel enterUsername, commandText, messagesLabel;
	JButton sendMessageBtn;
	
	public Spaceteam(){
		//setup main window
		super("Spaceteam");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setMaximumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
	    
		//create labels/text in order to set the font
		enterUsername = new JLabel("Enter a username:");
		username = new JTextField(15);
		commandText = new JLabel("Command will go here. LALALALLALALA");
		messagesLabel = new JLabel("Messages");
		userMessage = new JTextArea(3, 14);
		sendMessageBtn = new JButton("Send");
		
		//load font, later edit to keep the try/catch at the top and set the fonts of the necessary labels only
		Font font;
		try {
			GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
			font = Font.createFont(Font.TRUETYPE_FONT, new File("src/armata-regular-webfont.ttf"));
			//font = Font.createFont(Font.TRUETYPE_FONT, new File("src/spaceteam/gui/armata-regular-webfont.ttf"));
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
		
		cardsGeneral = new JPanel(new CardLayout());
		
		//Set up starting screen panel
		startCard = new JPanelWithBackground("src/spaceteamsplash1.jpg");
		//startCard = new JPanelWithBackground("src/spaceteam/gui/spaceteamsplash1.jpg");
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
		icons = new ArrayList<JLabel>();
		for(int i = 0; i < 6; i++){
			JLabel temp = new JLabel("temp");
			temp.setBackground(Color.blue);
			icons.add(temp);
			iconSelect.add(temp);
		}
		Dimension iconSelectDimension = iconSelect.getPreferredSize();
		iconSelect.setBounds(480, 420, iconSelectDimension.width, iconSelectDimension.height);
		startCard.add(iconSelect);
		
		continueButton = new JButton("Spaceteam");
		continueButton.addActionListener(this);
		contDimensions = continueButton.getPreferredSize();
		continueButton.setBounds(360, 540, contDimensions.width, contDimensions.height);
		startCard.add(continueButton);
		
		//Set general panel layout for main pane
		gameCard = new JPanel();
		gameCard.setLayout(new BoxLayout(gameCard, BoxLayout.X_AXIS));
		
		mainPane = new JPanel(new CardLayout());
		mainPane.setSize(550, 600);
		
		waitForPlayersPane = new JPanel(null);
		waitPlayers = new JButton("Waiting for Other Players");
		waitPlayers.addActionListener(this);
		wpDimensions = waitPlayers.getPreferredSize();
		waitPlayers.setBounds(150, 25, wpDimensions.width, wpDimensions.height);
		waitForPlayersPane.add(waitPlayers);
		
		gamePane = new JPanel();
		gamePane.setLayout(new BoxLayout(gamePane, BoxLayout.Y_AXIS));
		
		waitForTeamPane = new JPanel(null);
		
		
		//Set up end screen panel
		endCard = new JPanel(null);
		
		
		//set up health bar
		healthPanel = new JPanelWithBackground("src/spacestrip.jpeg");
		//healthPanel = new JPanelWithBackground("src/spaceteam/gui/spacestrip.jpeg");
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
		timePanel = new JPanel();
		timePanel.setPreferredSize(new Dimension(540, 50));
		timePanel.setBackground(Color.YELLOW);
		
		//set up controls
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(540, 400));
		controlPanel.setBackground(Color.GREEN);
		controlPanel.setLayout(new GridLayout(2,3));
		for(int i = 1; i <= 6; i++){
			JButton temp = new JButton("" + i);
			controlPanel.add(temp);
		}
		
		//set up chat pane
		chatPane = new JPanel();
		chatPane.setPreferredSize(new Dimension(260,600));
		chatPane.setMinimumSize(new Dimension(260,600));
		chatPane.setMaximumSize(new Dimension(260,600));
		chatPane.setBackground(new Color(10,47,105));
		messagesLabel.setForeground(Color.white);
		chatPane.add(messagesLabel, BorderLayout.NORTH);
		messages = new Vector<String>();
		chatMessages = new JList<String>();
		jsp = new JScrollPane(chatMessages);
		jsp.setPreferredSize(new Dimension(240, 470));
		chatPane.add(jsp, BorderLayout.CENTER);
		//Change up the lookandfeel. temporarily looks hideous
		JPanel sendMessagePanel = new JPanel();
		sendMessagePanel.setBackground(new Color(10, 47, 130)); //a5c7d9
		userMessage.setLineWrap(true);
		userMessage.setWrapStyleWord(true);
		JScrollPane userMessageJSP = new JScrollPane(userMessage);
		userMessageJSP.setViewportView(userMessage);
		sendMessagePanel.add(userMessageJSP);
		sendMessageBtn.setPreferredSize(new Dimension(80, 30));
		sendMessageBtn.setForeground(Color.WHITE);
		sendMessageBtn.setBackground(Color.BLACK);
		sendMessageBtn.setOpaque(true);
		sendMessageBtn.setBorderPainted(false);
		sendMessagePanel.add(sendMessageBtn);
		chatPane.add(sendMessagePanel, BorderLayout.SOUTH);
		
		//add components to the window
		cardsGeneral.add(startCard, "Start Screen");
		
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
		
		cardsGeneral.add(gameCard, "Gameplay");
		
		cardsGeneral.add(endCard, "End Screen");
		
		add(cardsGeneral);
		
		CardLayout cl = (CardLayout)(cardsGeneral.getLayout());
		cl.show(cardsGeneral, "Start Screen");
	}
	
	public static void main(String [] args){
		Spaceteam st = new Spaceteam();
		st.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == continueButton){
			CardLayout cl = (CardLayout)(cardsGeneral.getLayout());
			cl.show(cardsGeneral, "Gameplay");
			//Send user information to the client!!!
		}
		else if(e.getSource() == waitPlayers){
			CardLayout cl = (CardLayout)(mainPane.getLayout());
			cl.show(mainPane, "Game");
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

}
