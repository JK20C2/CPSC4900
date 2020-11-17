//package internal;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JComponent;

import main.Solitaire;

/**
 * This is GUI component with a embedded data structure. This structure is a
 * mixture of a queue and a stack
 */
public class CardStack extends JComponent {

	private static final long serialVersionUID = 1L;

	protected final int NUM_CARDS = 52;
	protected Vector<Card> v;

	protected boolean playStack = false;
	protected int SPREAD = 18;
	protected int _x = 0;
	protected int _y = 0;

	public CardStack(boolean isDeck) {

		this.setLayout(null);
		v = new Vector<>();

		if (isDeck) {
			// Add all the 52 cards to the deck
			for (Card.Suit suit : Card.Suit.values()) {
				for (Card.Value value : Card.Value.values()) {
					v.add(new Card(suit, value));
				}
			}

		} else {

			playStack = true;

		}
	}

	public boolean empty() {
		return v.isEmpty();
	}

	public void putFirst(Card c) {
		v.add(0, c);
	}

	public Card getFirst() {
		if (!this.empty()) {
			return v.get(0);
		} else
			return null;
	}

	/**
	 * Remove a card form a specific index
	 * 
	 * @param index
	 * @return
	 */
	public Card removeCard(int index) {
		if (index >= 0 && index < v.size()) {
			return v.remove(index);
		}

		return null;
	}

	/**
	 * Get all cards of the value specified, and removes them from the deck
	 * 
	 * @param value specified for the cards
	 * @return Vector of the cards that match that value from the deck
	 */
	public Vector<Card> getAllCardsWithValue(Card.Value value) {

		System.out.println("Thread: " + Thread.currentThread().getName());
		Vector<Card> suitCards = new Vector<>();

		ListIterator<Card> iterator = v.listIterator();

		while (iterator.hasNext()) {

			Card card = iterator.next();

			if (card.getValue().equals(value)) {
				suitCards.add(card);
				v.remove(card);
			}
		}

		return suitCards;

	}

	// analogous to peek()
	public Card getLast() {
		if (!this.empty()) {
			return v.lastElement();
		} else
			return null;
	}

	// queue-like functionality
	public Card popFirst() {
		if (!this.empty()) {
			Card c = this.getFirst();
			v.remove(0);
			return c;
		} else
			return null;

	}

	public void push(Card c) {
		v.add(c);
	}

	public Card pop() {
		if (!this.empty()) {
			Card c = v.lastElement();
			v.remove(v.size() - 1);
			return c;
		} else
			return null;
	}

	// shuffle the cards
	public void shuffle() {
		Vector<Card> v = new Vector<Card>();

		while (!this.empty()) {
			v.add(this.pop());
		}

		while (!v.isEmpty()) {
			Card c = v.elementAt((int) (Math.random() * v.size()));
			this.push(c);
			v.removeElement(c);
		}

	}

	public int showSize() {
		return v.size();
	}

	// reverse the order of the stack
	public CardStack reverse() {

		Vector<Card> v = new Vector<Card>();
		while (!this.empty()) {
			v.add(this.pop());
		}

		while (!v.isEmpty()) {
			Card c = v.firstElement();
			this.push(c);
			v.removeElement(c);
		}
		return this;
	}

	public void makeEmpty() {
		v.clear();
	}

	@Override
	public boolean contains(Point p) {
		Rectangle rect = new Rectangle(_x, _y, Card.CARD_WIDTH + 10, Card.CARD_HEIGHT * 3);
		return (rect.contains(p));
	}

	public void setXY(int x, int y) {
		_x = x;
		_y = y;
		setBounds(_x, _y, Card.CARD_WIDTH + 10, Card.CARD_HEIGHT * 3);
	}

	/**
	 * Get the position of the deck
	 * 
	 * @return the top left point of the deck
	 */
	public Point getXY() {
		return new Point(_x, _y);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (playStack) {
			removeAll();

			// An iterator for all the cards in the component
			ListIterator<Card> iter = v.listIterator();

			Point prev = new Point(); // positioning relative to the container
			Point prevWhereAmI = new Point();// abs positioning on the board

			if (iter.hasNext()) {
				Card c = iter.next();
				// this origin is point(0,0) inside the card stack container
				prev = new Point();// c.getXY(); // starting deck position
				add(Solitaire.moveCard(c, prev.x, prev.y));
				// setting x & y position
				c.setWhereAmI(getXY());
				prevWhereAmI = getXY();
			} else {
				removeAll();
			}

			for (; iter.hasNext();) {

				Card c = iter.next();
				c.setXY(new Point(prev.x, prev.y + SPREAD));
				add(Solitaire.moveCard(c, prev.x, prev.y + SPREAD));
				prev = c.getXY();
				// setting x & y position
				c.setWhereAmI(new Point(prevWhereAmI.x, prevWhereAmI.y + SPREAD));
				prevWhereAmI = c.getWhereAmI();
			}

		}
	}

	public Vector<Card> getAllCards() {
		return v;
	}
	
}// END CardStack
