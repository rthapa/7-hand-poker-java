import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
/**
 * This is a Deck class which will have a arraylist of cards and can be used in any card game which includes 52 cards in a deck.
 * @author Rabi Thapa
 * @version 1
 */
public class Deck {

	private ArrayList<Card> cards = new ArrayList<Card>();
	int index_1;
	int index_2;
	Random generator = new Random();
	
	/**
	 * Deck constructor which adds Card object via a for loop to make it a 52 card deck.
	 * This constructor also shuffles the card in random order.
	 */
	public Deck(){
		
		for(int a=0; a<=3; a++){
			for(int b=0; b<=12; b++){
				cards.add(new Card(a,b));
			}
		}
		
		shuffleDeck();
	
	}
	
	/**
	 * shuffles the card in a deck
	 */
	public void shuffleDeck(){
		Collections.shuffle(cards);
	}
	
	/**
	 * Removes a card from a deck
	 * @return
	 */
	public Card drawFromDeck(){
		return cards.remove(cards.size()-1);
	}
	
	/**
	 * Gets the total card in a deck
	 * @return cards in a deck
	 */
	public int getTotalCards(){
		return cards.size();
	}
	

	
}
