import java.io.Serializable;
/**
 * This class is a Card class which can be used in any Card Game.
 * @author Rabi Thapa
 * @version 1
 */

public class Card implements Serializable{
	private static final long serialVersionUID = -2723363051271966964L;
	private int suit, rank;
	
	private static String[] suits ={"hearts", "spades", "diamonds", "clubs"};
	private static String[] ranks = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king" };
	
	public static String rankAsString(int intRank){
		return ranks[intRank];
	}
	
	public Card(int suit, int rank){
		this.rank = rank;
		this.suit = suit;
	}
	
	public String toString(){
		return ranks[rank] + "_of_" + suits[suit];
	}
	
	public int getRank(){
		return rank;
	}
	
	public int getSuit(){
		return suit;
	}
	
}
