import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.imageio.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * This class is a client side class for Poker Game which has a Poker Game GUI.
 * @author Rabi Thapa
 * @version 1
 */
public class PokerClient extends JFrame implements Runnable, PokerConstant {

	private boolean myTurn = false;
	private boolean waiting = true;
	private boolean continueToPlay = true;
	
	private int playerAction;
	private double raisedAmount;
	private int turnTakenThisRound = 0; 
	
	//Streams 
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	
	//buttons
	private JButton check;
	private JButton fold;
	private JButton raise;
	private JTextField raisedInputBox;
	
	
	//player chips
	double p1Chips = 1000;
	double p2Chips = 1000;
	
	Card[] myHand;
	Card[] flopHand;
	
	//this is used after game ends to show opponents hand cards
	Card[] p1Hand;
	Card[] p2Hand;
	TablePanel s;
	private JTextArea textArea = new JTextArea();
	private JLabel totalChips = new JLabel();
	private JLabel labelStatus = new JLabel();
	private JLabel labelTitle = new JLabel();
	/**
	 * PokerClient constructor which adds other panels and starts connectToServer() method
	 * @param title title of the GUI
	 */
	public PokerClient(String title){
		super(title);
		myHand = new Card[2];
		flopHand = new Card[5];
		JPanel panel = new JPanel();
		s = new TablePanel(this);

		setLayout(new BorderLayout());
		add(s, BorderLayout.CENTER);
		add(totalChips, BorderLayout.NORTH);
		add(labelTitle,BorderLayout.NORTH);
		
		add(gameControlPanel(), BorderLayout.SOUTH);
		textArea.setRows(7);
		textArea.setEditable(false);
		labelTitle.setText("title here");
		labelStatus.setText("status here");
	
		connectToServer();
	}
	
	/**
	 * JPanel gameControlPanel() which includes game control buttons for a player.
	 * @return This Panel
	 */
	public JPanel gameControlPanel(){
		JPanel gameControlPanel = new JPanel();
		
		check = new JButton("Check");
		check.setPreferredSize(new Dimension(80,25));
	   	check.addActionListener(new checkListener());
	   		
        fold = new JButton("Fold");
        fold.setPreferredSize(new Dimension(80,25));
   		fold.addActionListener(new foldListener());
   		
        raise = new JButton("Raise");
        raise.setPreferredSize(new Dimension(80,25));
   		raise.addActionListener(new raiseListener());
		
   		raisedInputBox = new JTextField();
   		raisedInputBox.setPreferredSize(new Dimension(80,25));
   		
   		gameControlPanel.setBackground(Color.GRAY);
	 	gameControlPanel.setLayout(new GridBagLayout());
	 	GridBagConstraints gbc = new GridBagConstraints();
	 	gbc.insets = new Insets(15,15,15,15); //15px
	 	
	 	gbc.gridx = 0;
		gbc.gridy = 0;
		gameControlPanel.add(check, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gameControlPanel.add(fold, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gameControlPanel.add(raisedInputBox, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gameControlPanel.add(raise, gbc);
		
		return gameControlPanel;
	}
	/**
	 * Connects the client to Game server.
	 * Creates new thread for the client and starts the thread.
	 */
	private void connectToServer(){
		//Socket socket;
		try {
			
			Socket socket = new Socket(InetAddress.getLocalHost(), 8000);
			
			//inputstream from server
			fromServer = new ObjectInputStream(socket.getInputStream());
			
			//outputstream to server
			toServer = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//separte thread to control the game
		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	/**
	 * run() method runs after the thread.start() is initialised in connectToServer().
	 */
	public void run(){
		
		try{
			
			//get notification from server
			int player = fromServer.readInt();
			
			if(player == PLAYER1){
				
				setTitleMessage("Player 1");
				s.setP1TotalChips(p1Chips);
				s.setCurrentPlayerIs(PLAYER1);
				disableButtons();
				s.setCurrentGameRound(WAITINGROUND);
				String tempPlayer1Name = JOptionPane.showInputDialog("Input player 1 name: ");
				s.setPlayerName(tempPlayer1Name);
				toServer.writeObject(s.getPlayerName());
				toServer.flush();
				
				//player2 name recieved from server
				String tempP2Name = (String)fromServer.readObject();
				s.setPlayerName2(tempP2Name);
				s.setBubbleStatus(s.getPlayerName() + " Turn!");
				//myHand cards recieved
				Card object[] = (Card[])fromServer.readObject(); 
				myHand = (Card[])object;
				p1Hand = (Card[])object;
				
				//recieve p2 hand card 
				Card object2[] = (Card[])fromServer.readObject();
				p2Hand= (Card[])object2;
				
				s.setCurrentGameRound(PREFLOPROUND);
				//s.setPlayerName2("player-2");
				s.setP2TotalChips(p2Chips);
				enableButtons();
				s.setP1Card1(myHand[0]);
				s.setP1Card2(myHand[1]);
				s.setP2Card1(p2Hand[0]);
				s.setP2Card2(p2Hand[1]);
				displayAll(myHand);
				
				/**
				s.setP2TotalChipsBid(100);
				s.setCurrentPot(100);
				s.setP2TotalChips(s.getP2TotalChips() - s.getP2TotalChipsBid());
				s.setCurrentBidToMatch(100);
				setStatusMessage("My turn");
				s.setBubbleStatus("Player-1 Turn!");
				*/
				
				//display flopHand
				
				Card object1[] = (Card[])fromServer.readObject(); 
				flopHand = (Card[])object1;
				//setStatusMessage("flop displayed");
				
				s.setFlop1(flopHand[0]);
				s.setFlop2(flopHand[1]);
				s.setFlop3(flopHand[2]);
				s.setFlop4(flopHand[3]);
				s.setFlop5(flopHand[4]);
				
				displayAll(flopHand);
				
				//start up notification from here
				
				//fromServer.readInt();
				//setStatusMessage("Player 2 has joined");
				
				setMyTurn(true);
			}else if(player == PLAYER2){
				
				setTitleMessage("Player 2");
				s.setP2TotalChips(p2Chips);
				s.setCurrentPlayerIs(PLAYER2);
				s.setPlayerName2("player-2");
				s.setP1TotalChips(p1Chips);
				
				
				String tempPlayer2Name = JOptionPane.showInputDialog("Input player 2 name: ");
				s.setPlayerName2(tempPlayer2Name);
				toServer.writeObject(s.getPlayerName2());
				toServer.flush();
				
				//player1 name recieved from server
				String tempP1Name = (String)fromServer.readObject();
				s.setPlayerName(tempP1Name);
				s.setBubbleStatus(s.getPlayerName() + " Turn!");
				s.setCurrentGameRound(PREFLOPROUND);
				//hand card recieved
				Card object[] = (Card[])fromServer.readObject(); 
				myHand = (Card[])object;
				p2Hand = (Card[])object;
				//recieve p1 hand card 
				Card object2[] = (Card[])fromServer.readObject();
				p1Hand= (Card[])object2;
				
				s.setP2Card1(myHand[0]);
				s.setP2Card2(myHand[1]);
				s.setP1Card1(p1Hand[0]);
				s.setP1Card2(p1Hand[1]);
				displayAll(myHand);
				
				/**
				String startingBid = JOptionPane.showInputDialog(s.getPlayerName2() + " Please insert your starting bet ");
				s.setP2TotalChipsBid(Double.parseDouble(startingBid));
				s.setCurrentPot(s.getP2TotalChipsBid());
				s.setP2TotalChips(s.getP2TotalChips() - s.getP2TotalChipsBid());
				s.setCurrentBidToMatch(startingBid);
				
				s.setP2TotalChipsBid(100);
				s.setCurrentPot(100);
				s.setP2TotalChips(s.getP2TotalChips() - s.getP2TotalChipsBid());
				s.setCurrentBidToMatch(100);
				*/
				//flopHand recieved
				Card object1[] = (Card[])fromServer.readObject(); 
				flopHand = (Card[])object1;
				
				
				s.setFlop1(flopHand[0]);
				s.setFlop2(flopHand[1]);
				s.setFlop3(flopHand[2]);
				s.setFlop4(flopHand[3]);
				s.setFlop5(flopHand[4]);
				
				displayAll(flopHand);
				
			}
			
			while(continueToPlay){
			
				
				if(player == PLAYER1){
						s.setPlayerToken(1);
						checkSetButtonName();
						
						waitForPlayerAction(); // Wait for player 1 to move
				        sendMove(); // Send the move to the server
				        sendChipsDetail();
				        
				        receiveInfoFromServer(); // Receive info from the server
				}else if(player == PLAYER2){
						s.setPlayerToken(2);
						checkSetButtonName();
						disableButtons();
						receiveInfoFromServer(); // Receive info from the server
						checkSetButtonName();
				        waitForPlayerAction(); // Wait for player 2 to move
				        sendMove(); // Send player 2's move to the server
				        sendChipsDetail();
				        
					}
				}
			
			
		}catch(Exception ex){
			
		}
	}
	
	/**
	 * Puts the thread into sleeping state unless the isWaiting() is false.
	 * @throws InterruptedException
	 */
   private void waitForPlayerAction() throws InterruptedException {

	   while (isWaiting()) { 
		      Thread.sleep(100);
	   }
	    setWaiting(true);
   }
   
   /**
    * Sends the player move to the server which includes:
    * - current game round
    * - player action
    * - current bid to match
    * - raised amount
    * - turn taken this round
    * @throws IOException
    */
   private void sendMove() throws IOException {
	   	   toServer.writeInt(s.getCurrentGameRound());
	   	   toServer.flush();
		   toServer.writeInt(getPlayerAction()); 
		   toServer.flush();
		   toServer.writeDouble(s.getCurrentBidToMatch());
		   toServer.flush();
		   toServer.writeDouble(getRaisedAmount());
		   toServer.flush();
		   toServer.writeInt(getTurnTakenThisRound());
		   toServer.flush();
	}
   /**
    * Sends the player chips detail to the server which includes:
    * - current pot
    * - Player 1 total chips bid
    * - Player 2 total chips bid
    * - Player 1 total chips
    * - Player 2 total chips
    * @throws IOException
    */
   private void sendChipsDetail()throws IOException{
	   toServer.writeDouble(s.getCurrentPot());
	   toServer.flush();
	   toServer.writeDouble(s.getP1TotalChipsBid());
	   toServer.flush();
	   toServer.writeDouble(s.getP2TotalChipsBid());
	   toServer.flush();
	   toServer.writeDouble(s.getP1TotalChips());
	   toServer.flush();
	   toServer.writeDouble(s.getP2TotalChips());
	   toServer.flush();
   }
	/**
	 * Receives information from server which includes:
	 * - Game status
	 * - Player move
	 * - Chips update
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
   private void receiveInfoFromServer() throws IOException, ClassNotFoundException {
	   
	    // Receive game status - this will either be a win message, a draw message or a continue message
	    int status = fromServer.readInt();
	    
	    if (status == PLAYER1_WON) {
	    
	    	int p1ValueInIntRecieved = fromServer.readInt();
	    	s.setP1ValueInInt(p1ValueInIntRecieved);
	    	
	    	Card wonWithHighCardRecieved = (Card)fromServer.readObject();
	    	s.setP1WonWithHighCardOf(wonWithHighCardRecieved);
	    	
	    	s.setWinnerIs(s.getPlayerName() + " Wins! "+ getHandValueInString(s.getP1ValueInInt())+ ", high card of: "+s.getP1WonWithHighCardOf().toString());
	    	
	    	receiveMove();
		    recieveChipsUpdate();
		    s.setP1TotalChips(s.getP1TotalChips() + s.getCurrentPot());
		    s.setP1TotalChipsBid(0);
		    s.setP2TotalChipsBid(0);
		    s.setCurrentPot(0);
		    s.setBubbleStatus(s.getPlayerName() + " Wins!!");
		    continueToPlay = false;
		   
		    
	    }
	    else if (status == PLAYER2_WON) {
	    	int p2ValueInIntRecieved = fromServer.readInt();
	    	s.setP2ValueInInt(p2ValueInIntRecieved);
	    	
	    	Card wonWithHighCardRecieved = (Card)fromServer.readObject();
	    	s.setP2WonWithHighCardOf(wonWithHighCardRecieved);
	    	
	    	s.setWinnerIs(s.getPlayerName2() + " Wins! "+ getHandValueInString(s.getP2ValueInInt())+ ", high card of: "+s.getP2WonWithHighCardOf().toString());
	    	
	    	  receiveMove();
		      recieveChipsUpdate();
		      s.setP2TotalChips(s.getP2TotalChips() + s.getCurrentPot());
		      s.setP1TotalChipsBid(0);
			  s.setP2TotalChipsBid(0);
			  s.setCurrentPot(0);
			  s.setBubbleStatus(s.getPlayerName2() + " Wins!!");
			  continueToPlay = false;
		     
	    }
	    else if (status == PLAYER1_FOLD) {
	      continueToPlay = false;
	      receiveMove();
	      recieveChipsUpdate();
	    }else if(status == PLAYER2_FOLD){
	      continueToPlay = false;
	      receiveMove();
	      recieveChipsUpdate();
	    }else {
	      receiveMove();
	      recieveChipsUpdate();
	      setMyTurn(true);
	      enableButtons();
	    }
    }
   
   /**
    * Receives other player's move from the server
    * @throws IOException
    */
    private void receiveMove() throws IOException {
	    // Get the other player's move
    		int currentRoundRecieved = fromServer.readInt();
    		s.setCurrentGameRound(currentRoundRecieved);
    		int action = fromServer.readInt();
    		playerAction = action;
    		
    		double currentBidToMatchRecieved = fromServer.readDouble();
    		s.setCurrentBidToMatch(currentBidToMatchRecieved);
    		int turnTakenRecieved = fromServer.readInt();
    		setTurnTakenThisRound(turnTakenRecieved);

    		//bubble status = folds
			if(getPlayerAction() == PLAYER1_FOLD){
	    		s.setBubbleStatus(s.getPlayerName() + " Folds!");
	    		s.setWinnerIs(s.getPlayerName2() + " wins the pot!");
	    	}else if(getPlayerAction() == PLAYER2_FOLD){
	    		s.setBubbleStatus(s.getPlayerName2() + " Folds!");
	    		s.setWinnerIs(s.getPlayerName() + " wins the pot!");
	    	}
    
    }
    
    /**
     * Recieves player chips update
     * @throws IOException
     */
    private void recieveChipsUpdate() throws IOException{
    	
    	double currPotRecieved = fromServer.readDouble();
    	s.setCurrentPot(currPotRecieved);
    	
    	double p1TotalChipsBidRecieved = fromServer.readDouble();
    	s.setP1TotalChipsBid(p1TotalChipsBidRecieved);
  
    	double p2TotalChipsBidRecieved = fromServer.readDouble();
    	s.setP2TotalChipsBid(p2TotalChipsBidRecieved);
    	
    
    	double p1TotalChipsRecieved = fromServer.readDouble();
    	s.setP1TotalChips(p1TotalChipsRecieved);
  
    	
    	double p2TotalChipsRecieved = fromServer.readDouble();
    	s.setP2TotalChips(p2TotalChipsRecieved);  
    	//bubble status outputs bets or raised 
    	if(s.getPlayerToken() == 1){
    		if(playerAction == RAISE && s.getP1TotalChipsBid() == 0){
	    		 s.setBubbleStatus(s.getPlayerName2() + " Bets!");
	    	 }else if(playerAction == RAISE){
	    		 s.setBubbleStatus(s.getPlayerName2() + " raised!");
	    	 }
    		
    		if(playerAction == CHECK && s.getCurrentBidToMatch() == 0){
	    		 s.setBubbleStatus(s.getPlayerName2() + " Checks!");
	    	}else if(playerAction == CHECK){
	    		 s.setBubbleStatus(s.getPlayerName2() + " Calls!");
	    	}
		}else if(s.getPlayerToken() == 2){
			if(playerAction == RAISE && s.getP2TotalChipsBid() == 0){
	    		 s.setBubbleStatus(s.getPlayerName() + " Bets!");
	    	 }else if(playerAction == RAISE){
	    		 s.setBubbleStatus(s.getPlayerName() + " raised!");
	    	 }
			
			if(playerAction == CHECK && s.getCurrentBidToMatch() == 0){
	    		 s.setBubbleStatus(s.getPlayerName() + " Checks!");
	    	}else if(playerAction == CHECK){
	    		 s.setBubbleStatus(s.getPlayerName() + " Calls!");
	        }
    	}
    	
    	//bubble status = folds
		if(playerAction == PLAYER1_FOLD){
    		s.setBubbleStatus(s.getPlayerName() + " Folds!");
    	}else if(playerAction == PLAYER2_FOLD){
    		s.setBubbleStatus(s.getPlayerName2() + " Folds!");
    	}
		
    	if(s.getPlayerToken() == 1){
    		if(s.getP2TotalChips() == 0){
   			 s.setBubbleStatus(s.getPlayerName2() + " is All in !");
   		 	}
    	}else if(s.getPlayerToken() == 2){
    	  	if(s.getP1TotalChips() == 0){
   			 s.setBubbleStatus(s.getPlayerName() + " is All in !");
    	  	}
    	}
    }
    	
   /**
    * Sets the turn taken by players
    * @param turnTaken turn taken in a round
    */
    public void setTurnTakenThisRound(int turnTaken){
    	turnTakenThisRound = turnTaken;
    }
    
    /**
     * Gets the turn taken by players
     * @return turnTakenThisRound variable
     */
    public int getTurnTakenThisRound(){
    	return turnTakenThisRound;
    }
    
    /**
     * sets the label text.
     * @param msg String to overwrite the label title
     */
	public void setTitleMessage(String msg){
		labelTitle.setText(msg);
	}
	
	
	/**
	 * sets the player action as CHECK, CALL, FOLD or RAISE.
	 * @param action player action in int
	 */
	public void setPlayerAction(int action){
		playerAction = action;
	}
	
	/**
	 * gets the player action
	 * @return integer variable playerAction
	 */
	public int getPlayerAction(){
		return playerAction;
	}
	
	/**
	 * check if waiting is true or false
	 * @return boolean variable waiting
	 */
	public boolean isWaiting() {
	    return waiting;
    }
	
	/**
	 * Sets waiting to true or false
	 * @param b boolean variable that will be set to waiting variable
	 */
	public void setWaiting(boolean b) {
		  waiting = b;
    }
	
	/**
	 * Main method for PokerClient GUI
	 * @param args
	 */
	public static void main(String []args){
		PokerClient client = new PokerClient("Poker client");
		client.setSize(700, 600);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.setVisible(true);
		client.setResizable(false);
	}
	
	/**
	 * Takes a player's hand (7 cards) value and returns it into String representation
	 * @param pValue player's full hand value
	 * @return String representation of the int value
	 */
	public String getHandValueInString(int pValue){
		String temp = null;
		switch(pValue){
			case 1:
				temp = "High Card";
			break;
			case 2:
				temp = "with a pair";
			break;
			case 3:
				temp = "with 2 pairs";
			break;
			case 4:
				temp = "with 3 of a kind";
			break;
			case 5:
				temp = "with a straight";
			break;
			case 6:
				temp = "with a flush";
			break;
			case 7:
				temp = "with a full house";
			break;
			case 8:
				temp = "with 4 of a kind";
			break;
			case 9:
				temp = "with a straight flush";
			break;
			case 10:
				temp = "with a Royal flush";
			break;
			default:
				temp = "none of the value matched.";
		}
		return temp;	
	}
	
	//delet this maybe =----------------------->>>>>>>>>>>>>>>>
	public void displayAll(Card[] cardArray){
		//sortByRank(myHand);
		for(int i=0; i<cardArray.length; i++){
			//textArea.append("index of " + i + " : " +myHand[i] +"\n");
			textArea.append(cardArray[i].toString()+"\n");
		}
		//valueHand(cards);
	}
	
	/**
	 * Disables player game control buttons.
	 */
	public void disableButtons(){
		check.setEnabled(false);
		fold.setEnabled(false);
		raise.setEnabled(false);
		raisedInputBox.setEditable(false);
	}
	
	/**
	 *This method enables player game control buttons.
	 */
	public void enableButtons(){
		check.setEnabled(true);
		fold.setEnabled(true);
		raise.setEnabled(true);
		raisedInputBox.setEditable(true);
	}
	
	/**
	 *This method gets a player raised amount
	 * @return
	 */
	public double getRaisedAmount() {
		return raisedAmount;
	}
	
	/**
	 *This method sets a player raised amount.
	 * @param raisedAmount
	 */
	public void setRaisedAmount(double raisedAmount) {
		this.raisedAmount = raisedAmount;
	}
	
	/**
	 * Checks if myTurn is true or false
	 * @return
	 */
	public boolean isMyTurn() {
		return myTurn;
	}
	
	/**
	 * Sets myTurn to true or false
	 * @param myTurn
	 */
	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
	}
	
	/**
	 * Checks button text and updates to different text according to the situation
	 */
	public void checkSetButtonName(){
			if(s.getCurrentPot() <= 0){ //s.getCurrentGameRound() == PREFLOPROUND &&
				raise.setText("Bet");
			}else{
				raise.setText("Raise");
			}
			if(s.getP1TotalChipsBid() != s.getP2TotalChipsBid()){
				check.setText("Call");
			}else{
				check.setText("Check");
			}

	}
	
	/**
	 * A action listener class for Check button
	 */
	private class checkListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setPlayerAction(CHECK);
			setMyTurn(false);
			setWaiting(false);
			disableButtons();
			if(s.getPlayerToken() == 1){
		    	  //updating the poker chips
		    	 s.setP1TotalChips(s.getP1TotalChips() - s.getCurrentBidToMatch());
		    	 s.setP1TotalChipsBid(s.getP1TotalChipsBid() + s.getCurrentBidToMatch());
		    	 s.setCurrentPot(s.getCurrentPot()+s.getCurrentBidToMatch());
		    	 s.setCurrentBidToMatch(0);
		    	 
		    	 //s.setCurrentGameRound(s.getCurrentGameRound());
		    	 //s.setCurrentGameRound(s.getCurrentGameRound() + 1);

		    	 if(check.getText() == "Check"){
		    		 s.setBubbleStatus(s.getPlayerName() + " Checks!");
		    	 }else{
		    		 s.setBubbleStatus(s.getPlayerName() + " Calls!");
		    	 }
		    	 //s.setBubbleStatus(s.getPlayerName2()+" turn!");
		      }else if(s.getPlayerToken() == 2){
		    	
		    	  //updating the poker chips
			     s.setP2TotalChips(s.getP2TotalChips() - s.getCurrentBidToMatch());     
			     s.setP2TotalChipsBid(s.getP2TotalChipsBid() + s.getCurrentBidToMatch());
			     s.setCurrentPot(s.getCurrentPot()+s.getCurrentBidToMatch());
			     s.setCurrentBidToMatch(0);
			     
			     //s.setCurrentGameRound(s.getCurrentGameRound());
			     //s.setBubbleStatus(s.getPlayerName()+" turn!");
			     if(check.getText() == "Check"){
		    		 s.setBubbleStatus(s.getPlayerName2() + " Checks!");
		    	 }else{
		    		 s.setBubbleStatus(s.getPlayerName2() + " Calls!");
		    	 }
		      }
			
			if(getTurnTakenThisRound() >= 1){
				if(s.getP1TotalChipsBid() == s.getP2TotalChipsBid()){
					 s.setCurrentGameRound(s.getCurrentGameRound() + 1);
					 setTurnTakenThisRound(0);
				}
			}else{
				 s.setCurrentGameRound(s.getCurrentGameRound());
				 setTurnTakenThisRound(getTurnTakenThisRound() + 1);
			}
			
			 if(s.getP1TotalChips() == 0 && s.getP2TotalChips() == 0){
				 s.setCurrentGameRound(FINDWINNERROUND);
			 }
		}
	}
	
	/**
	 * A action listener class for Fold button
	 */
	private class foldListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			if(s.getPlayerToken() == 1){
				setPlayerAction(PLAYER1_FOLD);
				s.setP2TotalChips(s.getP2TotalChips() + s.getCurrentPot());     
			    s.setP2TotalChipsBid(0);
			    //s.setP1TotalChips(s.getP1TotalChips() - s.getCurrentPot());
			    s.setP1TotalChipsBid(0);
			}else if(s.getPlayerToken() == 2){
				setPlayerAction(PLAYER2_FOLD);
				s.setP1TotalChips(s.getP1TotalChips() + s.getCurrentPot());     
			    s.setP1TotalChipsBid(0);
			    //s.setP2TotalChips(s.getP2TotalChips() - s.getCurrentPot());
			    s.setP2TotalChipsBid(0);
			}
			setMyTurn(false);
			setWaiting(false);
			disableButtons();
			//setMyTurn(false);
			//setWaiting(false);
			//disableButtons();
			s.setCurrentGameRound(FOLDROUND);
			setTurnTakenThisRound(0);
			s.setCurrentPot(0);
		    s.setCurrentBidToMatch(0);
		    setRaisedAmount(0);
		  //bubble status = folds
			if(getPlayerAction() == PLAYER1_FOLD){
	    		s.setBubbleStatus(s.getPlayerName() + " Folds!");
	    		s.setWinnerIs(s.getPlayerName2() + " wins the pot!");
	    	}else if(getPlayerAction() == PLAYER2_FOLD){
	    		s.setBubbleStatus(s.getPlayerName2() + " Folds!");
	    		s.setWinnerIs(s.getPlayerName() + " wins the pot!");
	    	}
		}
	}
	
	/**
	 * A action listener class for Raise button
	 */
	private class raiseListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			if(raisedInputBox.getText().equals("")){
				JOptionPane.showMessageDialog(null, "Input "+raise.getText()+" amount first!");
			}else if(s.getPlayerToken() == 1){
				
				double tempRaisedAmountCheck = Double.parseDouble(raisedInputBox.getText());
				if(tempRaisedAmountCheck > s.getP1TotalChips()){
					JOptionPane.showMessageDialog(null, "Player 1, You do not have enough chips");
					
				}else if(s.getP1TotalChipsBid() + tempRaisedAmountCheck <= s.getP2TotalChipsBid() && tempRaisedAmountCheck != s.getP1TotalChips()){		
					JOptionPane.showMessageDialog(null, "Player 1, You need to raise more to match the bids");					
				}else{
					
					double tempRaisedAmount = Double.parseDouble(raisedInputBox.getText());
					setRaisedAmount(getRaisedAmount() + tempRaisedAmount );
					setPlayerAction(RAISE);
					setMyTurn(false);
					setWaiting(false);
					disableButtons();
					
					 s.setP1TotalChips(s.getP1TotalChips() - getRaisedAmount());
			    	 s.setP1TotalChipsBid(s.getP1TotalChipsBid() + getRaisedAmount());
			    	 s.setCurrentPot(s.getCurrentPot()+getRaisedAmount());		   
			    	 s.setCurrentBidToMatch(s.getP1TotalChipsBid() - s.getP2TotalChipsBid());
			    	 if(raise.getText() == "Bet"){
			    		 s.setBubbleStatus(s.getPlayerName() + " Bets!");
			    	 }else{
			    		 s.setBubbleStatus(s.getPlayerName() + " raised!");
			    	 }
			    	 if(s.getP1TotalChips() == 0){
						 s.setBubbleStatus(s.getPlayerName() + " is All in !");
					 }
			    	 setTurnTakenThisRound(getTurnTakenThisRound() + 1);
				}
			}else if(s.getPlayerToken() == 2){
				double tempRaisedAmountCheck = Double.parseDouble(raisedInputBox.getText());
				if(tempRaisedAmountCheck > s.getP2TotalChips()){
					JOptionPane.showMessageDialog(null, "Player 2, You do not have enough chips");
				}else if(s.getP2TotalChipsBid() + tempRaisedAmountCheck <= s.getP1TotalChipsBid()&& tempRaisedAmountCheck != s.getP2TotalChips()){
					JOptionPane.showMessageDialog(null, "Player 2, You need to raise more to match the bids");
				}else{
					double tempRaisedAmount = Double.parseDouble(raisedInputBox.getText());
					setRaisedAmount(getRaisedAmount() + tempRaisedAmount );
					setPlayerAction(RAISE);
					setMyTurn(false);
					setWaiting(false);
					disableButtons();
					
					 s.setP2TotalChips(s.getP2TotalChips() - getRaisedAmount());     
				     s.setP2TotalChipsBid(s.getP2TotalChipsBid() + getRaisedAmount());
				     s.setCurrentPot(s.getCurrentPot()+ getRaisedAmount());	
					 s.setCurrentBidToMatch(s.getP2TotalChipsBid() - s.getP1TotalChipsBid());
					 if(raise.getText() == "Bet"){
			    		 s.setBubbleStatus(s.getPlayerName2() + " Bets!");
			    	 }else{
			    		 s.setBubbleStatus(s.getPlayerName2() + " raised!");
			    	 }
					 if(s.getP2TotalChips() == 0){
						 s.setBubbleStatus(s.getPlayerName2() + " is All in !");
					 }
					 setTurnTakenThisRound(getTurnTakenThisRound() + 1);
				}
			}
			 raisedInputBox.setText("");
			 setRaisedAmount(0);
			 if(s.getP1TotalChips() == 0 && s.getP2TotalChips() == 0){
				 s.setCurrentGameRound(FINDWINNERROUND);
			 }else{
				 s.setCurrentGameRound(s.getCurrentGameRound());
			 }
		     
			
			
		}
	}
	
}//end of poker client


