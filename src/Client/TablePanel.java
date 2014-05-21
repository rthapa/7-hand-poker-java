import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class extends JPanel and contains all the game drawings and images of a Poker Game.
 * @author Rabi Thapa
 * @version 1
 */

class TablePanel extends JPanel implements PokerConstant{
	
	String relativePath = System.getProperty("user.dir") + "\\images\\";
	String imagePath = "C:/Users/Rabi/workspace/7handPoker/src/images/";
	
	//String imagePath = getClass().getResource("/images/");
	Card p1Card1, p1Card2, p2Card1, p2Card2, flop1, flop2, flop3, flop4, flop5 ;
	
	private PokerClient parent;
	
	private int currentPlayerIs;
	private String playerName;
	private String playerName2;
	
	private double p1TotalChips;
	private double p2TotalChips;
	double currentPot;
	
	private double p1TotalChipsBid;
	private double p2TotalChipsBid;
	private double currentBidToMatch;
	
	private int playerToken;
	
	private int currentGameRound;
	
	private String bubbleStatus = "default";
	
	private String winnerIs = "winner is ..... ";
	private int p1ValueInInt;
	private int P2ValueInInt;
	private Card p1WonWithHighCardOf;
	private Card p2WonWithHighCardOf;
	
	/**
	 * TablePanel constructor which takes PokerClient GUI
	 * @param gui
	 */
	public TablePanel( PokerClient gui){
		//card = selectCard; Card selectCard,
		parent = gui;
	}
	
	
	/**
	 * This method handles all the image and text drawing on this panel
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g.setColor(Color.white);
	
		ImageIcon bg;
		try {
			//background table
			bg = new ImageIcon(ImageIO.read(new File(relativePath + "pokerTableBlack.png")));
			//System.out.println(System.getProperty("user.dir"));
			bg.paintIcon(this, g, 0, 0);
			
			switch(currentGameRound){
			case WAITINGROUND:
					if(playerName != null && currentPlayerIs == PLAYER1){
						//player details
						drawPlayerPicture(g, 30, 70);
						drawPlayerDetailBox(g, 5, 158);
						g.drawString(playerName, 60, 180);
						g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
						g.drawString("£" + Double.toString(currentPot), 400, 150);
						drawNotification(g, "waitingForPlayers.png", 0, 0);
						g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
						g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					}else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//player details
						drawPlayerPicture(g, 30, 270);
						drawPlayerDetailBox(g, 5, 358);
						g.drawString(playerName2, 60, 380);
						g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
						g.drawString("£" + Double.toString(currentPot), 400, 150);
					}
					break;
			case PREFLOPROUND:
					//p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//notification
					drawNotification(g, "preFlopRound.png", 0, 0);
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
					if(playerName != null && currentPlayerIs == PLAYER1){
						//p1 cards
						drawCard(relativePath, p1Card1, g, 90, 100);
						drawCard(relativePath, p1Card2, g, 110, 100);
						//p2 facedown
						drawFaceDown( g, 90, 300);
						drawFaceDown( g, 110, 300);						
					}else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//p2 cards
						drawCard(relativePath, p2Card1, g, 90, 300);
						drawCard(relativePath, p2Card2, g, 110, 300);
						//p1 facedown
						drawFaceDown( g, 90, 100);
						drawFaceDown( g, 110, 100);	
					}					
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					break;
			case FLOPROUND:
					//p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//table values
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					drawNotification(g, "flopRound.png", 0, 0);
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
				    if(playerName != null && currentPlayerIs == PLAYER1){
				    	//p1 hand card
						drawCard(relativePath, p1Card1, g, 90, 100);
						drawCard(relativePath, p1Card2, g, 110, 100);
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);					
						//draw p2 facedown cards
						drawFaceDown( g, 90, 300);
						drawFaceDown( g, 110, 300);
				    }else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//p1 facedown cards
						drawFaceDown( g, 90, 100);
						drawFaceDown( g, 110, 100);	
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						//draw p2 hand cards stuffs	
						drawCard(relativePath, p2Card1, g, 90, 300);
						drawCard(relativePath, p2Card2, g, 110, 300);
				    }
				    break;
			case TURNROUND:
				//p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//table values
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					drawNotification(g, "turnRound.png", 0, 0);
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
					if(playerName != null && currentPlayerIs == PLAYER1){
				    	//p1 hand card
						drawCard(relativePath, p1Card1, g, 90, 100);
						drawCard(relativePath, p1Card2, g, 110, 100);
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						//draw p2 facedown cards
						drawFaceDown( g, 90, 300);
						drawFaceDown( g, 110, 300);
				    }else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//p1 facedown cards
						drawFaceDown( g, 90, 100);
						drawFaceDown( g, 110, 100);	
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						//draw p2 hand cards stuffs	
						drawCard(relativePath, p2Card1, g, 90, 300);
						drawCard(relativePath, p2Card2, g, 110, 300);
				    }
					break;
					
			   case RIVERROUND:
				    //p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//table values
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					drawNotification(g, "riverRound.png", 0, 0);
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
					if(playerName != null && currentPlayerIs == PLAYER1){
				    	//p1 hand card
						drawCard(relativePath, p1Card1, g, 90, 100);
						drawCard(relativePath, p1Card2, g, 110, 100);
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						drawCard(relativePath, flop5, g, 420, 190);
						//draw p2 facedown cards
						drawFaceDown( g, 90, 300);
						drawFaceDown( g, 110, 300);
				    }else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//p1 facedown cards
						drawFaceDown( g, 90, 100);
						drawFaceDown( g, 110, 100);	
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						drawCard(relativePath, flop5, g, 420, 190);
						//draw p2 hand cards stuffs	
						drawCard(relativePath, p2Card1, g, 90, 300);
						drawCard(relativePath, p2Card2, g, 110, 300);
				    }
					break;
					
			   case FINDWINNERROUND:
				 //p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//table values
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					drawNotification(g, "winnerBanner.png", 200, 270);
					g.drawString(getWinnerIs(), 210, 285);
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
					if(playerName != null && currentPlayerIs == PLAYER1){
					//p1 hand card
					drawCard(relativePath, p1Card1, g, 90, 100);
					drawCard(relativePath, p1Card2, g, 110, 100);
					drawCard(relativePath, p2Card1, g, 90, 300);
					drawCard(relativePath, p2Card2, g, 110, 300);
					
					}else if(playerName2 != null && currentPlayerIs == PLAYER2){
					//draw p2 hand cards stuffs	
					drawCard(relativePath, p1Card1, g, 90, 100);
					drawCard(relativePath, p1Card2, g, 110, 100);
					drawCard(relativePath, p2Card1, g, 90, 300);
					drawCard(relativePath, p2Card2, g, 110, 300);
					}
					//flopCards
					drawCard(relativePath, flop1, g, 220, 190);
					drawCard(relativePath, flop2, g, 270, 190);
					drawCard(relativePath, flop3, g, 320, 190);
					drawCard(relativePath, flop4, g, 370, 190);
					drawCard(relativePath, flop5, g, 420, 190);
			   break;
			   
			  case FOLDROUND:
				  	//p1 id holder
					drawPlayerPicture(g, 30, 70);
					drawPlayerDetailBox(g, 5, 158);
					g.drawString(playerName, 60, 180);
					g.drawString("£" + Double.toString(p1TotalChips), 60, 200);
					//p2 id holder
					drawPlayerPicture(g, 30, 270);
					drawPlayerDetailBox(g, 5, 358);
					g.drawString(playerName2, 60, 380);
					g.drawString("£" + Double.toString(p2TotalChips), 60, 400);
					//table values
					g.drawString("£" + Double.toString(getP1TotalChipsBid()), 400, 150);
					g.drawString("£" + Double.toString(getP2TotalChipsBid()), 400, 170);
					g.drawString("£" + Double.toString(currentPot), 450, 160);
					
					drawBubbleStatus(g, 370, 0);
					g.drawString(getBubbleStatus(), 385, 30);
					drawNotification(g, "winnerBanner.png", 200, 270);
					g.drawString(getWinnerIs(), 210, 285);
					if(playerName != null && currentPlayerIs == PLAYER1){
				    	//p1 hand card
						drawCard(relativePath, p1Card1, g, 90, 100);
						drawCard(relativePath, p1Card2, g, 110, 100);
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						drawCard(relativePath, flop5, g, 420, 190);
						//draw p2 facedown cards
						drawFaceDown( g, 90, 300);
						drawFaceDown( g, 110, 300);
				    }else if(playerName2 != null && currentPlayerIs == PLAYER2){
						//p1 facedown cards
						drawFaceDown( g, 90, 100);
						drawFaceDown( g, 110, 100);	
						//flopCards
						drawCard(relativePath, flop1, g, 220, 190);
						drawCard(relativePath, flop2, g, 270, 190);
						drawCard(relativePath, flop3, g, 320, 190);
						drawCard(relativePath, flop4, g, 370, 190);
						drawCard(relativePath, flop5, g, 420, 190);
						//draw p2 hand cards stuffs	
						drawCard(relativePath, p2Card1, g, 90, 300);
						drawCard(relativePath, p2Card2, g, 110, 300);
				    }
			  break;
			   	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
	}
	
	/**
	 * This method draws a card via paint component. 
	 * @param path path of the image directory
	 * @param card card object's toString
	 * @param g	graphics g for paintComponent
	 * @param x sets X coordinate of the image
	 * @param y sets Y coordinate of the image
	 * @throws IOException
	 */
	 private void drawCard(String path, Card card, Graphics g, int x, int y) throws IOException{
		 ImageIcon img;
			 img = new ImageIcon(ImageIO.read(new File(path+card.toString()+".png")));
			 Image card1 = img.getImage();
			 Image card1New = card1.getScaledInstance(45, 65, java.awt.Image.SCALE_SMOOTH);
			 ImageIcon card1Icon = new ImageIcon(card1New);
			 card1Icon.paintIcon(this, g, x, y);
	 }
	 
	 /**
	  * This method draws a face down card image 
	  * @param g graphics g for paintComponent
	  * @param x sets X coordinate of the image
	  * @param y sets Y coordinate of the image
	  * @throws IOException
	  */
	 private void drawFaceDown(Graphics g, int x, int y) throws IOException{
		ImageIcon img;
		img = new ImageIcon(ImageIO.read(new File(relativePath + "faceDown.png")));
		Image card1 = img.getImage();
		Image card1New = card1.getScaledInstance(45, 65, java.awt.Image.SCALE_SMOOTH);
		ImageIcon card1Icon = new ImageIcon(card1New);
		card1Icon.paintIcon(this, g, x, y);
	 }
	 
	 /**
	  * This method draws a dark box banner which I used to hold players detail.
	  * @param g graphics g for paintComponent
	  * @param x sets X coordinate of the image
	  * @param y sets Y coordinate of the image
	  * @throws IOException
	  */
	 private void drawPlayerDetailBox(Graphics g, int x, int y) throws IOException{
		 ImageIcon bg;
		 bg = new ImageIcon(ImageIO.read(new File(relativePath + "playerDetailBox.png")));
		 bg.paintIcon(this, g, x, y);
	 }
	 
	 /**
	  * This method draws a player picture to the panel
	  * @param g graphics g for paintComponent
	  * @param x sets X coordinate of the image
	  * @param y sets Y coordinate of the image
	  * @throws IOException
	  */
	 private void drawPlayerPicture(Graphics g, int x, int y) throws IOException{
		 ImageIcon bg;
		 bg = new ImageIcon(ImageIO.read(new File(relativePath + "playerPic.png")));
		 bg.paintIcon(this, g, x, y);
	 }
	 
	 /**
	  * This method draws a bubble status image to the panel
	  * @param g graphics g for paintComponent
	  * @param x sets X coordinate of the image
	  * @param y sets Y coordinate of the image
	  * @throws IOException
	  */
	 private void drawBubbleStatus(Graphics g, int x, int y) throws IOException{
		 ImageIcon bg;
		 bg = new ImageIcon(ImageIO.read(new File(relativePath + "bubbleStatus.png")));
		 bg.paintIcon(this, g, x, y);
	 }
	 
	 /**
	  * This method draws a notification banner in the panel.
	  * @param g graphics g for paintComponent
	  * @param fileName notification banner file name inside images directory
	  * @param x sets X coordinate of the image
	  * @param y sets Y coordinate of the image
	  * @throws IOException
	  */
	 private void drawNotification(Graphics g, String fileName, int x, int y) throws IOException{
		 ImageIcon bg;
		 bg = new ImageIcon(ImageIO.read(new File(relativePath + fileName)));
		 bg.paintIcon(this, g, x, y);
	 }
	 
	 //Accessors and Mutators
	 
	 /**
	  * Sets 1 if the player is 1 and 2 if the player is 2 and repaints the GUI
	  * @param currentPlayerInt current player in integer
	  */
	 public void setCurrentPlayerIs(int currentPlayerInt){
		currentPlayerIs = currentPlayerInt;
		repaint();
	}
	
	 /**
	  * Sets the current game round of the game and repaints the GUI.
	  * @param round integer representation of game round
	  */
	public void setCurrentGameRound(int round){
		currentGameRound = round;
		repaint();
	}
	
	/**
	 * Gets the current game round in a game
	 * @return current game round in integer
	 */
	public int getCurrentGameRound(){
		return currentGameRound;
	}
	
	/**
	 * Gets the total chips in table also known as pot
	 * @return total chips in table in double
	 */
	public double getCurrentPot(){
		return currentPot;
	}
	
	/**
	 * Sets the total chips in table also known as pot and repaints the GUI
	 * @param currentPot chips value in double
	 */
	public void setCurrentPot(double currentPot){
		this.currentPot = currentPot;
		repaint();
	}
	
	/**
	 * Sets the player 1 total chips and repaints the GUI
	 * @param p1TotalChips chips value in double
	 */
	public void setP1TotalChips(double p1TotalChips){
		this.p1TotalChips = p1TotalChips;
		repaint();
	}
	
	/**
	 * returns player 1 total chips in double
	 * @return p1TotalChips double variable
	 */
	public double getP1TotalChips(){
		return p1TotalChips;
	}
		
	/**
	 * Sets the player 2 total chips and repaints the GUI
	 * @param p2TotalChips chips value in double
	 */
	public void setP2TotalChips(double p2TotalChips){
		this.p2TotalChips = p2TotalChips;
		repaint();
	}
	
	/**
	 * returns player 2 total chips in double
	 * @return p2TotalChips double variable
	 */
	public double getP2TotalChips(){
		return p2TotalChips;
	}
	
	/**
	 * Sets player 1 name to playerName variable and repaints the GUI.
	 * @param name Player 1 name in String
	 */
	public void setPlayerName(String name){
		playerName = name;
		repaint();
	}
		
	/**
	 * Returns player 1 Name
	 * @return playerName player 1 name in String
	 */
	public String getPlayerName(){
		return playerName;
	}
	
	/**
	 * Sets player 2 name to playerName2 variable and repaints the GUI.
	 * @param name Player 2 name in String
	 */
	public void setPlayerName2(String name){
		playerName2 = name;
		repaint();
	}
	
	/**
	 * Returns player 2 name
	 * @return playerName2 player 2 name in String
	 */
	public String getPlayerName2(){
		return playerName2;
	}
	
	/**
	 * Sets player 1 first card and repaints the GUI.
	 * @param p1Card1 Card object which is obtained from server
	 */
	public void setP1Card1(Card p1Card1){
		this.p1Card1 = p1Card1;
		repaint();
	}
	
	/**
	 * Sets player 1 second card and repaints the GUI.
	 * @param p1Card2 Card object which is obtained from server
	 */
	public void setP1Card2(Card p1Card2){
		this.p1Card2 = p1Card2;
		repaint();
	}
	
	/**
	 * Sets player 2 first card and repaints the GUI.
	 * @param p2Card1 Card object 
	 */
	public void setP2Card1(Card p2Card1){
		this.p2Card1 = p2Card1;
		repaint();
	}
	
	/**
	 * Sets player 2 second card and repaints the GUI	
	 * @param p2Card2
	 */
	public void setP2Card2(Card p2Card2){
		this.p2Card2 = p2Card2;
		repaint();
	}
	
	/**
	 * Set the first community card also known as flop card and repaints the GUI.
	 * @param flop1 card object variable for first flop card
	 */
	public void setFlop1(Card flop1){
		this.flop1 = flop1;
		repaint();
	}
		
	/**
	 * Set the second community card also known as flop card and repaints the GUI.
	 * @param flop2 card object variable for second flop card
	 */
	public void setFlop2(Card flop2){
		this.flop2 = flop2;
		repaint();
	}
	
	/**
	 * Set the third community card also known as flop card and repaints the GUI.
	 * @param flop3 card object variable for third flop card
	 */
	public void setFlop3(Card flop3){
		this.flop3 = flop3;
		repaint();
	}
	
	/**
	 * Set the fourth community card also known as flop card and repaints the GUI.
	 * @param flop4 card object variable for fourth flop card
	 */
	public void setFlop4(Card flop4){
		this.flop4 = flop4;
		repaint();
	}
	
	/**
	 * Set the fifth community card also known as flop card and repaints the GUI.
	 * @param flop5 card object variable for fourth flop card
	 */
	public void setFlop5(Card flop5){
		this.flop5 = flop5;
		repaint();
	}  	 
	
	/**
	 * Gets player 1 total chips bid in a game 
	 * @return p1TotalChipsBid in double 
	 */
	public double getP1TotalChipsBid() {
		return p1TotalChipsBid;
	}
	
	/**
	 * Sets player 1 total chips bid in a game and repaints the GUI.
	 * @param p1TotalChipsBid stores player 1 total chips bid in double
	 */
	public void setP1TotalChipsBid(double p1TotalChipsBid) {
		this.p1TotalChipsBid = p1TotalChipsBid;
		repaint();
	}

	/**
	 * Gets player 2 total chips bid in a game 
	 * @return p2TotalChipsBid in double 
	 */
	public double getP2TotalChipsBid() {
		return p2TotalChipsBid;
	}
	
	/**
	 * Sets player 2 total chips bid in a game and repaints the GUI.
	 * @param p2TotalChipsBid stores player 2 total chips bid in double
	 */
	public void setP2TotalChipsBid(double p2TotalChipsBid) {
		this.p2TotalChipsBid = p2TotalChipsBid;
		repaint();
	}
	
	/**
	 * Gets current bet or bid which other player had made that this player has to match.
	 * @return currentBidtoMatch in double
	 */
	public double getCurrentBidToMatch() {
		return currentBidToMatch;
	}
	
	/**
	 * Sets current bet or bid made by this player for other players has to match and also repaints the GUI.
	 * @param currentBidToMatch chips value in double
	 */
	public void setCurrentBidToMatch(double currentBidToMatch) {
		this.currentBidToMatch = currentBidToMatch;
		repaint();
	}
	
	/**
	 * Gets a player token which is set as 1 for player 1 and 2 for player 2.
	 * @return player's Token as a integer
	 */
	public int getPlayerToken() {
		return playerToken;
	}
	
	/**
	 * Sets a player token 1 for player 1 and 2 for player 2 and also repaints the GUI.
	 * @param playerToken players token in integer (1 or 2)
	 */
	public void setPlayerToken(int playerToken) {
		this.playerToken = playerToken;
		repaint();
	}
	
	/**
	 * Gets a status which will be shown directly on top of bubble banner in this panel.
	 * @return bubbleStatus in String
	 */
	public String getBubbleStatus() {
		return bubbleStatus;
	}
	
	/**
	 * Sets a status which will be exactly shown on top of the bubble banner in this panel and also repaints the GUI.
	 * @param bubbleStatus String status to be shown as a bubble status
	 */
	public void setBubbleStatus(String bubbleStatus) {
		this.bubbleStatus = bubbleStatus;
		repaint();
	}
	
	/**
	 * Get the string representation of the game winner.
	 * @return winnerIs a String
	 */
	public String getWinnerIs() {
		return winnerIs;
	}
	
	/**
	 * Sets the string representation of the game winner and also repaints the GUI.
	 * @param winnerIs string that you would like to show when the game Ends
	 */
	public void setWinnerIs(String winnerIs) {
		this.winnerIs = winnerIs;
		repaint();
	}
	
	/**
	 * Gets the player 1 hand value in integer
	 * @return p1ValueInInt a integer value of player 1 full hand
	 */
	public int getP1ValueInInt() {
		return p1ValueInInt;
	}
	
	/**
	 * Sets the player 1 hand value in integer and also repaints the GUI.
	 * @param p1ValueInInt
	 */
	public void setP1ValueInInt(int p1ValueInInt) {
		this.p1ValueInInt = p1ValueInInt;
		repaint();
	}
	
	/**
	 * Gets the player 2 hand value in integer
	 * @return p2ValueInInt a integer value of player 2 full hand
	 */
	public int getP2ValueInInt() {
		return P2ValueInInt;
	}
	
	/**
	 * Sets the player 2 hand value in integer and also repaints the GUI.
	 * @param p2ValueInInt
	 */
	public void setP2ValueInInt(int p2ValueInInt) {
		P2ValueInInt = p2ValueInInt;
		repaint();
	}
	
	/**
	 * Gets player 1's high card within the 5 best card of the player's hand
	 * @return p1WonWithHighCardOf a Card type 
	 */
	public Card getP1WonWithHighCardOf() {
		return p1WonWithHighCardOf;
	}
	
	/**
	 * Sets player 1's high card of the player's hand and also repaints the GUI.
	 * @param p1WonWithHighCardOf a Card type
	 */
	public void setP1WonWithHighCardOf(Card p1WonWithHighCardOf) {
		this.p1WonWithHighCardOf = p1WonWithHighCardOf;
		repaint();
	}
	
	/**
	 * Gets player 2's high card within the 5 best card of the player's hand
	 * @return p2WonWithHighCardOf a Card type 
	 */
	public Card getP2WonWithHighCardOf() {
		return p2WonWithHighCardOf;
	}
	
	/**
	 * Sets player 2's high card of the player's hand and also repaints the GUI.
	 * @param p2WonWithHighCardOf a Card type
	 */
	public void setP2WonWithHighCardOf(Card p2WonWithHighCardOf) {
		this.p2WonWithHighCardOf = p2WonWithHighCardOf;
		repaint();
	}
}