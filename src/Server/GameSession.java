import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

/**
 * This class is a used in serverSide to handle the Game session of a Poker Game.
 * @author Rabi Thapa
 * @version 1
 */

public class GameSession implements Runnable, PokerConstant{
	//sockets
	private Socket player1;
	private Socket player2;
	//object streams
	private ObjectInputStream fromPlayer1;
	private ObjectOutputStream toPlayer1;
	private ObjectInputStream fromPlayer2;
	private ObjectOutputStream toPlayer2;
	
	//test hand to see if the hands are still sorted in if else 
	Card[] testHand = new Card[7];
	
	//game variables
	Card[] player1Hand = new Card[2];
	Card[] player2Hand = new Card[2];
	Card[] flopHand = new Card[5];
	Card[] player1FullHand = new Card[7];
	Card[] player2FullHand = new Card[7];
	//
	int playerAction;
	double raisedAmount;
	int turnTakenThisRound;
	double currentBidToMatch;
	int currentGameRound = PREFLOPROUND;
	double currentPot;
	double p1TotalChipsBid;
	double p2TotalChipsBid;
	double p1TotalChips;
	double p2TotalChips;
	//player hand value in int
	int pValueInInt;
	int p1ValueInInt;
	int p2ValueInInt;
	int winnerIs = 999999;
	//if players has same hand detrmine with high card.
	int p1HighCard, p2HighCard;
	//same win but find the high card in same win hands
	Card wonWithHighCardOf = null;
	Card p1WonWithHighCardOf = null;
	Card p2WonWithHighCardOf = null;
	//store player hand value in string for later use
	String p1ValueInString, p2ValueInString;
	//maybe delet this
	private int[] value;
	
	Deck d = new Deck();
	
	JTextArea output;
	
	/**
	 * GameSession Constructor which sets Client sockets and their object streams.
	 * @param player1 first socket
	 * @param player2 second socket
	 * @param toClient ObjectOutputStream 
	 * @param toClient2 ObjectInputStream
	 * @param serverLog server side log JTextArea for game session
	 */
	public GameSession(Socket player1, Socket player2, ObjectOutputStream toClient, ObjectOutputStream toClient2, JTextArea serverLog){
		
		value = new int[6];
		this.player1 = player1;
		this.player2 = player2;
		toPlayer1 = toClient;
		toPlayer2 = toClient2;
		output = serverLog;
	}
	
	/**
	 * Runs the GameSession Thread.
	 */
	public void run(){
		try{
			 fromPlayer1 = new ObjectInputStream(player1.getInputStream());
			 fromPlayer2 = new ObjectInputStream(player2.getInputStream());
			//draw 4 cards from deck and give 2 cards each
			d.shuffleDeck();
			for(int i=0; i<2; i++){
				player1Hand[i] = d.drawFromDeck();
				player2Hand[i] = d.drawFromDeck();
			}
			
			//add 5 cards to flop hand
			for(int i=0; i<5; i++){
				flopHand[i] = d.drawFromDeck();
			}
			
			try {
				String p1Name = (String)fromPlayer1.readObject();
				toPlayer2.writeObject(p1Name);
				toPlayer2.flush();
				String p2Name = (String)fromPlayer2.readObject();
				toPlayer1.writeObject(p2Name);
				toPlayer1.flush();
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sending players their hand cards
        	toPlayer1.writeObject(player1Hand); //p1 hand to p1
			toPlayer1.flush();
			toPlayer1.writeObject(player2Hand); //p2 hand to p1
			toPlayer1.flush();
			
			toPlayer2.writeObject(player2Hand); //p2 hand to p2
			toPlayer2.flush();
			toPlayer2.writeObject(player1Hand); //p1 hand to p2
			
			
			//sending players flopHand cards
			
			toPlayer1.writeObject(flopHand); //cards1
			toPlayer1.flush();
			toPlayer2.writeObject(flopHand); //cards1
			toPlayer2.flush();
			
			//toPlayer1.writeInt(CONTINUE);
			//toPlayer1.flush();
			while(true){
				 
				// Receive a move from player 1
				int currentGameRoundReceived = fromPlayer1.readInt();
				currentGameRound = currentGameRoundReceived;
		        int playerActionRecieved = fromPlayer1.readInt();
		        playerAction = playerActionRecieved;
		        double currentBidToMatchRecieved = fromPlayer1.readDouble();
		        currentBidToMatch = currentBidToMatchRecieved;
		        double raisedAmountRecieved = fromPlayer1.readDouble();
		        raisedAmount = raisedAmountRecieved;
		        int turnTakenThisRoundRecieved = fromPlayer1.readInt();
		        turnTakenThisRound = turnTakenThisRoundRecieved;
		        
		        //receiving chips detail to update in other players
		        double currentPotRecieved = fromPlayer1.readDouble();
		        currentPot = currentPotRecieved;
		        double p1TotalChipsBidRecieved = fromPlayer1.readDouble();
		        p1TotalChipsBid = p1TotalChipsBidRecieved;
		        double p2TotalChipsBidRecieved = fromPlayer1.readDouble();
		        p2TotalChipsBid = p2TotalChipsBidRecieved;
		        double p1TotalChipsRecieved = fromPlayer1.readDouble();
		        p1TotalChips = p1TotalChipsRecieved;
		        double p2TotalChipsRecieved = fromPlayer1.readDouble();
		        p2TotalChips = p2TotalChipsRecieved;
		        
		        if (currentGameRound == FOLDROUND) {
		        		if(playerAction == PLAYER1_FOLD){
				            toPlayer1.writeInt(PLAYER1_FOLD);
				            toPlayer1.flush();
				            toPlayer2.writeInt(PLAYER1_FOLD);
				            toPlayer2.flush();
		        		}else if(playerAction == PLAYER2_FOLD){
		        			toPlayer1.writeInt(PLAYER2_FOLD);
				            toPlayer1.flush();
				            toPlayer2.writeInt(PLAYER2_FOLD);
				            toPlayer2.flush();
		        		}
		        		updateCurrentBidToMatch();
			        	sendMove(toPlayer2, currentGameRound, playerAction, currentBidToMatch, turnTakenThisRound);
			        	sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		            break; // Break the loop
		        } else if (currentGameRound == FINDWINNERROUND) { 
		        	checkHandValueTotal(player1Hand, player1FullHand, 1);
		        	checkHandValueTotal(player2Hand, player2FullHand, 2);
		        	findTheWinner();
		        	toPlayer1.writeInt(winnerIs);
		            toPlayer1.flush();
		            toPlayer2.writeInt(winnerIs);
		            toPlayer2.flush();
		            if(winnerIs == PLAYER1_WON){
		            	toPlayer1.writeInt(p1ValueInInt);
		            	toPlayer1.flush();
		            	toPlayer1.writeObject(p1WonWithHighCardOf);
		            	toPlayer1.flush();
		            	
		            	toPlayer2.writeInt(p1ValueInInt);
		            	toPlayer2.flush();
		            	toPlayer2.writeObject(p1WonWithHighCardOf);
		            	toPlayer2.flush();
		            	
		            	//p1TotalChips = p1TotalChips + currentPot;
		    		 
;		            }else if(winnerIs == PLAYER2_WON){
		            	toPlayer1.writeInt(p2ValueInInt);
		            	toPlayer1.flush();
		            	toPlayer1.writeObject(p2WonWithHighCardOf);
		            	toPlayer1.flush();
		            	
		            	toPlayer2.writeInt(p2ValueInInt);
		            	toPlayer2.flush();
		            	toPlayer2.writeObject(p1WonWithHighCardOf);
		            	toPlayer2.flush();
		            	//p2TotalChips = p2TotalChips + currentPot;
		    		    
		            }
		            //currentPot = 0;
	    		    //currentBidToMatch = 0;
		        	 //toPlayer2.writeInt(CONTINUE);
		        	// toPlayer2.flush();
		        	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer2, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		        	 sendMove(toPlayer1, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		            break;
		         }else if(playerAction == CHECK && currentGameRound == FLOPROUND){
		        	 toPlayer2.writeInt(CONTINUE);
		        	 toPlayer2.flush();
		        	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer2, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
			     }else if(playerAction == CHECK && currentGameRound == TURNROUND){
		        	 toPlayer2.writeInt(CONTINUE);
		        	 toPlayer2.flush();
		        	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer2, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
			     }else if(playerAction == RAISE){
			    	 toPlayer2.writeInt(CONTINUE);
			    	 toPlayer2.flush();
			    	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer2, currentGameRound, RAISE, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
				 }else{
		            // Notify player 2 to take the turn - as this message is not '1' then
		            // this will swicth to the relevant player at the client side
		            toPlayer2.writeInt(CONTINUE);
		            toPlayer2.flush();
		            updateCurrentBidToMatch();	
		            // Send player 1's selected row and column to player 2
		            sendMove(toPlayer2, currentGameRound, CALL, currentBidToMatch, turnTakenThisRound);
		            sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		         }
		        
		     // Receive a move from Player 2
				int currentGameRoundReceivedFromP2 = fromPlayer2.readInt();
				currentGameRound = currentGameRoundReceivedFromP2;
		        int player2ActionRecieved = fromPlayer2.readInt();
		        playerAction = player2ActionRecieved;
		        double currentBidToMatchRecieved2 = fromPlayer2.readDouble();
		        currentBidToMatch = currentBidToMatchRecieved2;
		        double raisedAmountRecieved2 = fromPlayer2.readDouble();
		        raisedAmount = raisedAmountRecieved2;
		        int turnTakenThisRoundRecieved2 = fromPlayer2.readInt();
		        turnTakenThisRound = turnTakenThisRoundRecieved2;
		        
		      //receiving chips detail to update in other players
		        double currentPotRecieved2 = fromPlayer2.readDouble();
		        currentPot = currentPotRecieved2;
		        double p1TotalChipsBidRecieved2 = fromPlayer2.readDouble();
		        p1TotalChipsBid = p1TotalChipsBidRecieved2;
		        double p2TotalChipsBidRecieved2 = fromPlayer2.readDouble();
		        p2TotalChipsBid = p2TotalChipsBidRecieved2;
		        double p1TotalChipsRecieved2 = fromPlayer2.readDouble();
		        p1TotalChips = p1TotalChipsRecieved2;
		        double p2TotalChipsRecieved2 = fromPlayer2.readDouble();
		        p2TotalChips = p2TotalChipsRecieved2;
		        
		        if (currentGameRound == FOLDROUND) {
		        	//toPlayer1.writeInt(CONTINUE);
		        	//toPlayer1.flush();
	        		if(playerAction == PLAYER1_FOLD){
			            toPlayer1.writeInt(PLAYER1_FOLD);
			            toPlayer1.flush();
			            toPlayer2.writeInt(PLAYER1_FOLD);
			            toPlayer2.flush();
			            
	        		}else if(playerAction == PLAYER2_FOLD){
	        			toPlayer1.writeInt(PLAYER2_FOLD);
			            toPlayer1.flush();
			            toPlayer2.writeInt(PLAYER2_FOLD);
			            toPlayer2.flush();
	        		}
	        		updateCurrentBidToMatch();
		        	sendMove(toPlayer1, currentGameRound, playerAction, currentBidToMatch, turnTakenThisRound);
		        	sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		        	break; // Break the loop
		        } else if (currentGameRound == FINDWINNERROUND) { // Check if all cells are filled
		        	checkHandValueTotal(player1Hand, player1FullHand, 1);
		        	checkHandValueTotal(player2Hand, player2FullHand, 2);
		        	findTheWinner();
		        	toPlayer1.writeInt(winnerIs);
		            toPlayer1.flush();
		            toPlayer2.writeInt(winnerIs);
		            toPlayer2.flush();
		            
		            if(winnerIs == PLAYER1_WON){
		            	toPlayer1.writeInt(p1ValueInInt);
		            	toPlayer1.flush();
		            	toPlayer1.writeObject(p1WonWithHighCardOf);
		            	toPlayer1.flush();
		            	
		            	
		            	toPlayer2.writeInt(p1ValueInInt);
		            	toPlayer2.flush();
		            	toPlayer2.writeObject(p1WonWithHighCardOf);
		            	toPlayer2.flush();
		            	//p1TotalChips = p1TotalChips + currentPot;
		            }else if(winnerIs == PLAYER2_WON){
		            	toPlayer1.writeInt(p2ValueInInt);
		            	toPlayer1.flush();
		            	toPlayer1.writeObject(p2WonWithHighCardOf);
		            	toPlayer1.flush();
		            	
		            	toPlayer2.writeInt(p2ValueInInt);
		            	toPlayer2.flush();
		            	toPlayer2.writeObject(p2WonWithHighCardOf);
		            	toPlayer2.flush();
		            	//p2TotalChips = p2TotalChips + currentPot;
		            }
		            
		        	updateCurrentBidToMatch();
		        	sendMove(toPlayer1, currentGameRound, playerAction, currentBidToMatch, turnTakenThisRound);
		        	sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		        	sendMove(toPlayer2, currentGameRound, playerAction, currentBidToMatch, turnTakenThisRound);
		        	sendChipsUpdate(toPlayer2, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );	
		        	
		            break;
		         }else if(playerAction == CHECK && currentGameRound == TURNROUND){
		        	 toPlayer1.writeInt(CONTINUE);
		        	 toPlayer1.flush();
		        	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer1, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		        	
			     }else if(playerAction == CHECK && currentGameRound == FLOPROUND){
		        	 toPlayer1.writeInt(CONTINUE);
		        	 toPlayer1.flush();
		        	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer1, currentGameRound, CHECK, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		        	
			     }else if(playerAction == RAISE){
			    	 
			    	 toPlayer1.writeInt(CONTINUE);
			    	 toPlayer1.flush();
			    	 updateCurrentBidToMatch();
		        	 sendMove(toPlayer1, currentGameRound, RAISE, currentBidToMatch, turnTakenThisRound);
		        	 sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );

				 }else{
		            // Notify player 1 to take the turn - as this message is not '1' then
		            // this will swicth to the relevant player at the client side
		            toPlayer1.writeInt(CONTINUE);
		            toPlayer1.flush();
		            updateCurrentBidToMatch();
		            // Send player 1's selected row and column to player 2
		            sendMove(toPlayer1, currentGameRound, CALL, currentBidToMatch, turnTakenThisRound);
		            sendChipsUpdate(toPlayer1, currentPot, p1TotalChipsBid, p2TotalChipsBid, p1TotalChips, p2TotalChips );
		         }
			}
					
		}catch(IOException ex){
			
		}
	}
	
	 /**
	  * This method Sends a recieved player move through ObjectOutputStream.
	  * @param toPlayer object output stream
	  * @param currentRound current game round
	  * @param playerAction current player's action
	  * @param currentBidToMatch current bid to match
	  * @param turnTaken player's turn taken
	  * @throws IOException
	  */
	  private void sendMove(ObjectOutputStream toPlayer,int currentRound, int playerAction, double currentBidToMatch, int turnTaken) throws IOException {
		  	toPlayer.writeInt(currentRound);
		  	toPlayer.flush();
		    toPlayer.writeInt(playerAction); // 
		    toPlayer.flush();
		    toPlayer.writeDouble(currentBidToMatch); // 
		    toPlayer.flush();
		    toPlayer.writeInt(turnTaken);
		    toPlayer.flush();
		  }
	  
	  /**
	   * This method sends chips update via ObjectOutputStream. 
	   * @param toPlayer object output stream
	   * @param currPot current chips in pot 
	   * @param p1TotalBid Player 1 total bids this game
	   * @param p2TotalBid Player 2 total bids this game
	   * @param p1Total Player 1 total left chips
	   * @param p2Total Player 2 total left chips
	   * @throws IOException
	   */
	  private void sendChipsUpdate(ObjectOutputStream toPlayer, double currPot, double p1TotalBid, double p2TotalBid, double p1Total, double p2Total) throws IOException{
		   toPlayer.writeDouble(currPot);
		   toPlayer.flush();
		   toPlayer.writeDouble(p1TotalBid);
		   toPlayer.flush();
		   toPlayer.writeDouble(p2TotalBid);
		   toPlayer.flush();
		   toPlayer.writeDouble(p1Total);
		   toPlayer.flush();
		   toPlayer.writeDouble(p2Total);
		   toPlayer.flush();
	  }
	
	 /**
	  * This method updates current bid that needs to matched by other player
	  */
	public void updateCurrentBidToMatch(){
		currentBidToMatch = currentBidToMatch + raisedAmount;
	}
	
	/**
	 * This method Calculates the both player hands and sets the winning player to winnerIs variable.
	 * This method also checks weather both players have same hand value e.g a pair is common in 7 hand poker
	 * and both player could have a pair. In this situation it compares the high card within the pair of both players. 
	 */
	public void findTheWinner(){
		if(p1ValueInInt != p2ValueInInt){
			if(p1ValueInInt > p2ValueInInt){
				winnerIs = PLAYER1_WON;
				output.append("\n Player 1 \n");
			}else{
				winnerIs = PLAYER2_WON;
				output.append("\n Player 2 \n");
			}
		}else{
			if(p1WonWithHighCardOf.getRank() > p2WonWithHighCardOf.getRank()){
				winnerIs = PLAYER1_WON;
				output.append("\n Player 1, same value, but high card wins\n");
			}else{
				winnerIs = PLAYER2_WON;
				output.append("\n Player 2, same value,but high card wins\n");
			}
		}
	}
	
	// delet this maybe as not used-------------------------->>>>
	public void findHighCard(Card[] h, int pHighCard){
		sortByRank(h);
		
		if(h[0].getRank() == 0){
			pHighCard = 14;
		}else{
			pHighCard = h[6].getRank();
		}
	}
	
	/**
	 * This method checks a players hand value and stores it to a variable in int.
	 * At first it copies a player hand and flop hand cards array to a 7 hand cards array.
	 * Then it calculates the hand value using valueHand() method and stores the value in respected
	 * variables.
	 * @param pHand players hand which will consist of 2 cards
	 * @param pFullHand players full hand which will consist of 7 card (2 players card + 5 flop cards)
	 * @param playerNumber this is to know if its player 1 or player 2 hand value
	 */
	public void checkHandValueTotal(Card[] pHand, Card[] pFullHand, int playerNumber){
		//transfer player hand and flop cards to fullhand[]
		System.arraycopy( pHand, 0, pFullHand, 0, pHand.length );
	    System.arraycopy( flopHand, 0, pFullHand, pHand.length, flopHand.length );
		 //check fullHand array
		 for(int i=0; i<pFullHand.length; i++){
			 output.append("Card "+i+": "+pFullHand[i].toString()+"\n");	 
		 }
		 
		 valueHand(pFullHand);
		 if(playerNumber == 1){
			 p1ValueInInt = pValueInInt;
			 p1WonWithHighCardOf = wonWithHighCardOf;
		 }else if(playerNumber == 2){
			 p2ValueInInt = pValueInInt;
			 p2WonWithHighCardOf = wonWithHighCardOf;
		 }
	}
	
	/**
	 * Sorts an card array by suit from minimum to maximum.
	 * @param h card array
	 */
	public void sortBySuit(Card[] h){
		int i, j, min_j;
		
		for(i=0; i<h.length; i++){
			min_j = i; //assume i h[i] is the minimum
			
			for(j=i+1; j<h.length; j++){
				if(h[j].getSuit() < h[min_j].getSuit()){
					min_j = j; //found new smaller value, update min_j
				}
			}
			
			//now swap h[i] and a[min_j]
			Card help = h[i];
			h[i] = h[min_j];
			h[min_j]=help;
		}
	}
	
	/**
	 * Sorts an card array by rank from minimum to maximum.
	 * @param h card array
	 */
	public void sortByRank(Card[] h){
		int i, j, min_j;
		
		for(i=0; i<h.length; i++){
			min_j = i; //assume i h[i] is the minimum
			
			for(j=i+1; j<h.length; j++){
				if(h[j].getRank() < h[min_j].getRank()){
					min_j = j; //found new smaller value, update min_j
				}
			}
			
			//now swap h[i] and a[min_j]
			Card help = h[i];
			h[i] = h[min_j];
			h[min_j]=help;
		}
	}
	
	//---------------------------------------Hand value check algoriths--------------------------- //
	/**
	 * This method checks if the hand contains a flush
	 * @param h Card array
	 * @return
	 */
	public boolean isFlush(Card[] h){
		boolean a1, a2, a3;
		sortBySuit(h);
		//x x x x x a b
		a1 = h[0].getSuit() == h[4].getSuit();
		//a x x x x x b
		a2 = h[1].getSuit() == h[5].getSuit();
		//a b x x x x x
		a3 = h[2].getSuit() == h[6].getSuit();
		
		if(a1){
			wonWithHighCardOf = h[4];
		}else if(a2){
			wonWithHighCardOf = h[5];
		}else if(a3){
			wonWithHighCardOf = h[6];
		}
		
		
		return (a1 || a2 || a3);
	}
	
	/**
	 * This method checks if the hand contains a Straight
	 * @param h Card array
	 * @return
	 */	
	public boolean isStraight(Card[] h){
		
		
		sortByRank(h);
		
			//check for increasing value
			int testRank = h[0].getRank() +1;
			
			for(int i=0; i<7; i++){
				if(h[i].getRank() != testRank){
					return(false); //straight failed
				}else{
				testRank++;
				}
			}
			wonWithHighCardOf = h[4];
			return(true); //straight found
	}
	
	/**
	 * This method checks if the hand contains a flush and also straight
	 * @param h Card array
	 * @return
	 */
	public boolean isStraightFlush(Card[] h){
		return (isStraight(h) && isFlush(h));
	}
	
	/**
	 * This method checks if the hand contains a flush and also straight with Ace as a top card
	 * @param h Card array
	 * @return
	 */
	public boolean isRoyalFlush(Card[] h){
		sortByRank(h);
		return (isStraightFlush(h) && h[0].getRank() == 0);	
	}
	
	/**
	 * This method checks if the hand contains a 4 or a kind
	 * @param h Card array
	 * @return
	 */
	public boolean is4s(Card[] h){
		sortByRank(h);
		
		boolean a1, a2, a3, a4;
		
		//check x x x x y j k 
		a1 = h[0].getRank() == h[1].getRank() &&
			 h[1].getRank() == h[2].getRank() &&
			 h[2].getRank() == h[3].getRank();
		//check y x x x x k j
		a2 = h[1].getRank() == h[2].getRank() &&
			 h[2].getRank() == h[3].getRank() &&
			 h[3].getRank() == h[4].getRank();
		//check y k x x x x j
		a3 = h[2].getRank() == h[3].getRank() &&
			 h[3].getRank() == h[4].getRank() &&
			 h[4].getRank() == h[5].getRank();
		//check y k j x x x x
		a4 = h[3].getRank() == h[4].getRank() &&
			 h[4].getRank() == h[5].getRank() &&
			 h[5].getRank() == h[6].getRank();
		
		if(a1){
			wonWithHighCardOf = h[3];
		}else if(a2){
			wonWithHighCardOf = h[4];
		}else if(a3){
			wonWithHighCardOf = h[5];
		}else if(a4){
			wonWithHighCardOf = h[6];
		}
		
		
		return(a1 || a2 || a3 || a4);
	}
	
	/**
	 * This method checks if the hand contains a full house
	 * @param h card array
	 * @return
	 */
	public boolean isFullHouse(Card[] h){
		sortByRank(h);
		
		boolean a1a, a1b, a1c, b1a, b1b, b1c, c1a, c1b, c1c, d1a, d1b, d1c;
		//--------------//
		//check x x x y y i j
		a1a = h[0].getRank() == h[1].getRank() &&
			 h[1].getRank() == h[2].getRank() &&
			 h[3].getRank() == h[4].getRank();
		//check x x x i y y j
		a1b = h[0].getRank() == h[1].getRank() &&
			 h[1].getRank() == h[2].getRank() &&
			 h[4].getRank() == h[5].getRank();
		//check x x x i k y y
		a1c = h[0].getRank() == h[1].getRank() &&
			 h[1].getRank() == h[2].getRank() &&
			 h[5].getRank() == h[6].getRank();
		//---------------//
		//check i x x x y y k
		b1a = h[1].getRank() == h[2].getRank() &&
			 h[2].getRank() == h[3].getRank() &&
			 h[4].getRank() == h[5].getRank();
		//check i x x x k y y
		b1b = h[1].getRank() == h[2].getRank() &&
			 h[2].getRank() == h[3].getRank() &&
			 h[5].getRank() == h[6].getRank();
		//check i j x x x y y
		b1c = h[2].getRank() == h[3].getRank() &&
			 h[3].getRank() == h[4].getRank() &&
			 h[5].getRank() == h[6].getRank();
		//		
		//check x x y y y i j
		c1a = h[0].getRank() == h[1].getRank() &&
			 h[2].getRank() == h[3].getRank() &&
			 h[3].getRank() == h[4].getRank();
		//check x x i y y y j
		c1b = h[0].getRank() == h[1].getRank() &&
			 h[3].getRank() == h[4].getRank() &&
			 h[4].getRank() == h[5].getRank();
		//check x x i j y y y
		c1c = h[0].getRank() == h[1].getRank() &&
			 h[4].getRank() == h[5].getRank() &&
			 h[5].getRank() == h[6].getRank();
		//--------------..
		//check i x x y y y j
		d1a = h[1].getRank() == h[2].getRank() &&
			 h[3].getRank() == h[4].getRank() &&
			 h[4].getRank() == h[5].getRank();
		//check i x x j y y y
		d1b = h[1].getRank() == h[2].getRank() &&
			 h[4].getRank() == h[5].getRank() &&
			 h[5].getRank() == h[6].getRank();
		//check i j x x y y y
		d1c = h[2].getRank() == h[3].getRank() &&
			 h[4].getRank() == h[5].getRank() &&
			 h[5].getRank() == h[6].getRank();
		
		return(a1a || a1b || a1c || b1a || b1b || b1c || c1a || c1b|| c1c || d1a|| d1b|| d1c);
	}
	
	/**
	 * This method checks if the hand contains a 3 of a kind
	 * @param h card array
	 * @return
	 */
	public boolean is3s(Card[] h){
		sortByRank(h);
		if ( is4s(h) || isFullHouse(h) )
	         return(false);
		boolean a1, a2, a3, a4, a5;
		//checking x x x a b i j 
		a1 =h[0].getRank() == h[1].getRank() &&
			h[1].getRank() == h[2].getRank();
			
		//checking a x x x b i j 
		a2 =h[1].getRank() == h[2].getRank() &&
			h[2].getRank() == h[3].getRank();
		
		//checking a b x x x i j 
		a3 =h[2].getRank() == h[3].getRank() &&
			h[3].getRank() == h[4].getRank();
		//checking a b c x x x j 
		a4 =h[3].getRank() == h[4].getRank() &&
			h[4].getRank() == h[5].getRank();
		
		//checking a b c j x x x
		a5 =h[4].getRank() == h[5].getRank() &&
			h[5].getRank() == h[6].getRank();

		if(a1){
			wonWithHighCardOf = h[2];
		}else if(a2){
			wonWithHighCardOf = h[3];
		}else if(a3){
			wonWithHighCardOf = h[4];
		}else if(a4){
			wonWithHighCardOf = h[5];
		}else if(a5){
			wonWithHighCardOf = h[6];
		}
		
		
		return(a1 || a2 || a3 || a4 || a5);
	}
	
	/**
	 * This method checks if the hand contains a two pairs
	 * @param h card array
	 * @return
	 */
	public boolean is22s(Card[] h){
		sortByRank(h);
		
		if ( is4s(h) || isFullHouse(h) || is3s(h) )
	         return(false);
		
		boolean a1, a2, a3, a4, a5, a6, a7, a8, a9, a10;
		//check a a b b i j k
		a1=h[0].getRank() == h[1].getRank() &&
		   h[2].getRank() == h[3].getRank();
		//check a a x b b i j 
		a2=h[0].getRank() == h[1].getRank() &&
		   h[3].getRank() == h[4].getRank();
		//check a a i k b b j 
		a3=h[0].getRank() == h[1].getRank() &&
		   h[4].getRank() == h[5].getRank();
		//check a a i k j b b 
		a4=h[0].getRank() == h[1].getRank() &&
		   h[5].getRank() == h[6].getRank();
		//check x a a b b i j 
		a5=h[1].getRank() == h[2].getRank() &&
		   h[3].getRank() == h[4].getRank();
		//check x a a k b b j 
		a6=h[1].getRank() == h[2].getRank() &&
		   h[4].getRank() == h[5].getRank();
		//check x a a k j b b 
		a7=h[1].getRank() == h[2].getRank() &&
		   h[5].getRank() == h[6].getRank();
		//check x l a a b b j 
		a8=h[2].getRank() == h[3].getRank() &&
		   h[4].getRank() == h[5].getRank();
		//check x l a a j b b 
		a9=h[2].getRank() == h[3].getRank() &&
		   h[5].getRank() == h[6].getRank();
		//check x l j a a b b 
		a10=h[3].getRank() == h[4].getRank() &&
		   h[5].getRank() == h[6].getRank();
		
		if(a1){
			wonWithHighCardOf = h[3];
		}else if(a2){
			wonWithHighCardOf = h[4];
		}else if(a3){
			wonWithHighCardOf = h[5];
		}else if(a4){
			wonWithHighCardOf = h[6];
		}else if(a5){
			wonWithHighCardOf = h[4];
		}else if(a6){
			wonWithHighCardOf = h[5];
		}else if(a7){
			wonWithHighCardOf = h[6];
		}else if(a8){
			wonWithHighCardOf = h[5];
		}else if(a9){
			wonWithHighCardOf = h[6];
		}else if(a10){
			wonWithHighCardOf = h[6];
		}
		
		return(a1 || a2 || a3 || a4 || a5 || a6 || a7 || a8 || a9 || a10);	
	}
	
	/**
	 * This method checks if the hand contains a pair
	 * @param h card array
	 * @return
	 */
	public boolean is2s(Card[] h){
		sortByRank(h);
		
		boolean a1, a2, a3, a4, a5, a6;
		//checking a a x y z i j
		a1=h[0].getRank() == h[1].getRank();
		//checking x a a y z i j
		a2=h[1].getRank() == h[2].getRank();
		//checking x y a a z i j
		a3=h[2].getRank() == h[3].getRank();
		//checking x y z a a i j
		a4=h[3].getRank() == h[4].getRank();
		//checking x y z i a a j
		a5=h[4].getRank() == h[5].getRank();
		//checking x y z i j a a
		a6=h[5].getRank() == h[6].getRank();
		
		if(a1){
			wonWithHighCardOf = h[1];
		}else if(a2){
			wonWithHighCardOf = h[2];
		}else if(a3){
			wonWithHighCardOf = h[3];
		}else if(a4){
			wonWithHighCardOf = h[4];
		}else if(a5){
			wonWithHighCardOf = h[5];
		}else if(a6){
			wonWithHighCardOf = h[6];
		}
		
		return(a1 || a2 || a3 || a4 || a5 || a6);
	}
	

	/**
	 * Checks a hand value and stores it into pValueInInt variable
	 * @param h card array
	 * @return
	 */
	public String valueHand(Card[] h){
		String temp="default";
			
		if(isRoyalFlush(h)){
			temp = "Royal flush";
			pValueInInt = 10;
			output.append(temp);
			return temp;
		}else if(isStraightFlush(h)){
			temp = "straight flush";
			pValueInInt = 9;
			output.append(temp);
			return temp;
		}else if(is4s(h)){
			temp = "4 of a kind";
			pValueInInt = 8;
			output.append(temp);
			return temp;
		}else if(isFullHouse(h)){
			temp = "full house";
			pValueInInt = 7;
			output.append(temp);
			return temp;
		}else if(isFlush(h)){
			temp = "flush";
			pValueInInt = 6;
			output.append(temp);
			return temp;
		}else if(isStraight(h)){
			temp = "straight";
			pValueInInt = 5;
			output.append(temp);
			return temp;
		}else if(is3s(h)){
			temp = "3 of a kind";
			pValueInInt = 4;
			output.append(temp);
			return temp;
		}else if(is22s(h)){
			temp = "2 pair";
			pValueInInt = 3;
			output.append(temp);
			return temp;
		}else if(is2s(h)){
			for(int i=0; i<testHand.length; i++){
				testHand[i] = h[i];
			}
			temp="a pair";
			pValueInInt = 2;
			output.append(temp);
			//output.append("\n -- test hand check sorted -- \n");
			for(int i=0; i<testHand.length; i++){
				 output.append("Card "+i+": "+testHand[i].toString()+"\n");	 
			 }
			return temp;
		}else{
			sortByRank(h);
			if(h[0].getRank()== 0){
				temp = h[0].toString();
				pValueInInt = 1;
				wonWithHighCardOf = h[0];
				output.append("is High card: "+temp +"\n");
				return temp;
			}else{
				temp = h[6].toString();
				pValueInInt = 1;
				wonWithHighCardOf = h[6];
				output.append("is High card: "+temp +"\n");
				return temp;
			}
		}
	}
	
}
