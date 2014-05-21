import java.io.*;
import java.net.*;

import javax.swing.*;

import java.awt.*;
import java.util.Date;
/**
 * This class is a server side class which handles from Socket creation to game thread creation .
 * @author Rabi Thapa
 * @version 1
 */
public class PokerServer extends JFrame implements PokerConstant{

	 private ServerSocket serverSocket;
	 private Socket player1;
	 private Socket player2;
	 ObjectOutputStream toClient;
	 ObjectOutputStream toClient2;
	// ObjectInputStream fromClient;
	// ObjectInputStream fromClient2;
	 
	 Deck d = new Deck();
	 Card[] player1Hand = new Card[2];
	 Card[] player2Hand = new Card[2];
	 Card[] flopHand = new Card[5];
	 private GameSession game;
	 
	 /**
	  * PokerServer constructor accepts 2 players socket and initialises them in GameSession and starts it on a thread. 
	  */
	 public PokerServer(){
		 
		 
		 JTextArea serverLog = new JTextArea();
		 serverLog.setEditable(false);
		 JScrollPane scrollPane = new JScrollPane(serverLog);
		 
		 JTextArea playerHandLog = new JTextArea();
		 playerHandLog.setEditable(false);
		 playerHandLog.setRows(10);
		 playerHandLog.setText("Players Hands Log shown here: \n");
		 JScrollPane scrollPaneHand = new JScrollPane(playerHandLog);
		 
		 add(scrollPane, BorderLayout.CENTER);
		 add(scrollPaneHand, BorderLayout.PAGE_END);
		 
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 setSize(300, 400);
		 setResizable(false);
		 setTitle("My poker Server");
		 setVisible(true);
		 
		 try{
			 
			 int port = 8000;
			 //creating server socket
			 serverSocket = new ServerSocket(port);
			 
			 serverLog.append(new Date() + ": Server started at: " + port+ "\n");
			 
			 
			 //while(true){
				 serverLog.append(new Date() + ": Wait for players to join session \n");
				 
				 //connect to player1
				 player1 = serverSocket.accept();
				 serverLog.append(new Date() + ": Player1 joined. \n");
				 serverLog.append(new Date() + ": Player1 IP address is: " + player1.getInetAddress().getHostAddress()+ "\n");
				 toClient = new ObjectOutputStream(player1.getOutputStream());
				 toClient.writeInt(PLAYER1);
				 toClient.flush();
				 						 
				 //connect to player2
				 player2 = serverSocket.accept();
				 serverLog.append(new Date() +": Player2 joined \n");
				 serverLog.append(new Date() +": Player2 IP address is: "+ player2.getInetAddress().getHostAddress()+ "\n");
				 toClient2 = new ObjectOutputStream(player2.getOutputStream());
				 toClient2.writeInt(PLAYER2);
				 toClient2.flush();
				     		
                game = new GameSession(player1, player2, toClient, toClient2, playerHandLog);
                	
                // Start the new thread
	            new Thread(game).start();
	             
	            serverLog.append(new Date() + ": Game Session started. ");
			 //}
		 }catch(IOException ex){
			 System.err.println(ex);
		 }
	 }
	
	 /**
	  * PokerServer main class.
	  * @param args
	  */
	public static void main(String[] args) {
		PokerServer serverSide = new PokerServer();
		
	}

}
