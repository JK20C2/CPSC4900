//package dealersdozen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import internal.Card;
import internal.CardStack;
import internal.FinalStack;
import main.Solitaire;

/**
 * The JPanel subclass for handling deal dozen version of the game
 *
 */
public class DealersDozenPanel extends JPanel {
	private static final String TAG = DealersDozenPanel.class.getName();

	private static final int FOUNDATION_STACKS = 4;
	private static final int TABLEAU_STACKS = 4;
	private static int deal = 0; 

	public static final Point DECK_POS = new Point(8, 8);
	public static final Point WASTE_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 8, DECK_POS.y);
	public static final Point FOUNDATION_POS = new Point(WASTE_POS.x + Card.CARD_WIDTH + 150, DECK_POS.y);
	public static final Point REVERSE_POS = new Point(WASTE_POS.x, WASTE_POS.y + Card.CARD_HEIGHT + 32);
	public static final Point TABLEAU_POS = new Point(DECK_POS.x + Card.CARD_WIDTH * 3,
			FOUNDATION_POS.y + Card.CARD_HEIGHT + 30);

	/**
	 * Track version changes of the class
	 */
	private static final long serialVersionUID = 1L;
	private int height;
	private int width;

	private static FinalStack[] foundationStacks;
	private static CardStack[] tableauStacks;
	private static CardStack reversePile;
	private static CardStack deck;
	private static CardStack wastePile;
	private static Card deckPlaceHolder = new Card();

	public DealersDozenPanel(int width, int height) {
		this.width = width;
		this.height = height;

		initPanel();

		startGame();
	}

	/**
	 * Initialize the panel
	 */
	private void initPanel() {

		setLayout(null);
		setSize(width, (height - 60));
		setBackground(new Color(0, 80, 0));

		addMouseListener(new CardMovementManager());
		addMouseMotionListener(new CardMovementManager());

	}

	/**
	 * Start the game
	 */
	private void startGame() {

		// Initial the game components

		if (foundationStacks == null) {
			foundationStacks = new FinalStack[FOUNDATION_STACKS];
		}

		if (tableauStacks == null) {
			tableauStacks = new CardStack[TABLEAU_STACKS];
		}

		if (wastePile == null) {
			wastePile = new CardStack(false);
		}

		if (reversePile == null) {
			reversePile = new CardStack(false);
		}

		for (int i = 0; i < foundationStacks.length; i++) {
			foundationStacks[i] = new FinalStack();
		}

		for (int i = 0; i < tableauStacks.length; i++) {
			tableauStacks[i] = new CardStack(false);
		}

		reversePile = new CardStack(false);
		wastePile = new CardStack(false);
		deck = new CardStack(true);
		deck.shuffle();

		Vector<Card> twos = new Vector<>();
		// Get the twos
		for (int i = 0; i < deck.getAllCards().size(); i++) {
			Card card = deck.getAllCards().get(i);
			if (card.getValue().equals(Card.Value.TWO)) {

				twos.add(card);
				deck.getAllCards().remove(card);
			}
		}

		if (twos.size() == 4) {
			Logger.getLogger(TAG).log(Level.INFO, "Foundation cards populated with 2's");

			for (int i = 0; i < foundationStacks.length; i++) {
				foundationStacks[i].push(twos.get(i));
			}

			for (CardStack stack : foundationStacks) {
				Logger.getLogger(TAG).log(Level.INFO, "Foundation stack size " + stack.showSize());
			}

		}

		// Dealing new game
		for (int x = 0; x < tableauStacks.length; x++) {
			Card c = deck.pop().setFaceup();
			tableauStacks[x].putFirst(c);
		}

		for (int i = 0; i < 12; i++) {
			Card c = deck.pop().setFaceup();
			reversePile.putFirst(c);
		}

		drawStacks();

	}

	/**
	 * Draw all the card components
	 */
	public void drawStacks() {

		add(Solitaire.moveCard(deckPlaceHolder, DECK_POS.x, DECK_POS.y));

		// initialize & place final (foundation) decks/stacks
		for (int x = 0; x < foundationStacks.length; x++) {
			Logger.getLogger(TAG).log(Level.INFO, "Foundation stack size " + foundationStacks[x].showSize());
			foundationStacks[x].setXY((FOUNDATION_POS.x + (x * Card.CARD_WIDTH)) + 10, FOUNDATION_POS.y);
			add(foundationStacks[x]);
		}

		// initialize & place play (tableau) decks/stacks
		for (int x = 0; x < tableauStacks.length; x++) {
			tableauStacks[x].setXY((TABLEAU_POS.x + (x * (Card.CARD_WIDTH + 10))), TABLEAU_POS.y);
			add(tableauStacks[x]);
		}

		reversePile.setXY(REVERSE_POS.x, REVERSE_POS.y);
		add(reversePile);

		repaint();

	}

	private class CardMovementManager extends MouseAdapter {

		private Card prevCard = null;// tracking card for waste stack
		private Card movedCard = null;// card moved from waste stack
		private boolean sourceIsFinalDeck = false;
		private boolean putBackOnDeck = true;// used for waste card recycling
		private boolean checkForWin = false;// should we check if game is over?
		private boolean gameOver = true;// easier to negate this than affirm it
		private Point start = null;// where mouse was clicked
		private Point stop = null;// where mouse was released
		private Card card = null; // card to be moved
		// used for moving single cards

		private CardStack source = null;

		private CardStack dest = null;
		// used for moving a stack of cards
		private CardStack transferStack = new CardStack(false);

		/**
		 * Check whether the move is valid on the playing stack
		 * 
		 * @param source the card on transit
		 * @param dest   the top card of the destination
		 * @return whether the move is valid or not
		 */
		private boolean validPlayStackMove(Card source, Card dest) {

			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();

			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();

			// destination card should be one higher value
			if ((s_val + 1) == d_val) {
				// destination card should be opposite color
				switch (s_suit) {
				case SPADES:
					if (d_suit != Card.Suit.HEARTS && d_suit != Card.Suit.DIAMONDS)
						return false;
					else
						return true;
				case CLUBS:
					if (d_suit != Card.Suit.HEARTS && d_suit != Card.Suit.DIAMONDS)
						return false;
					else
						return true;
				case HEARTS:
					if (d_suit != Card.Suit.SPADES && d_suit != Card.Suit.CLUBS)
						return false;
					else
						return true;
				case DIAMONDS:
					if (d_suit != Card.Suit.SPADES && d_suit != Card.Suit.CLUBS)
						return false;
					else
						return true;
				default:
					return false;
				}
			} else {
				return false;
			}
		}

		/**
		 * Check whether that is a valid foundation move
		 * 
		 * @param source the card on transit
		 * @param dest   the top card on the destination
		 * @return boolean of whether the move is valid or not
		 */
		private boolean validFinalStackMove(Card source, Card dest) {
			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();
			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();

			if (s_val == (d_val + 1))
				return s_suit == d_suit;
			return false;
		}

		@Override
		public void mousePressed(MouseEvent e) {

			start = e.getPoint();
			boolean stopSearch = false;

			for (int x = 0; x < tableauStacks.length; x++) {
				if (stopSearch)
					break;

				source = tableauStacks[x];

				// Search the exact card pressed
				for (Component ca : source.getComponents()) {
					Card c = (Card) ca;
					if (c.getFaceStatus() && source.contains(start)) {
						transferStack.putFirst(c);
					}

					if (c.contains(start) && source.contains(start) && c.getFaceStatus()) {
						card = c;
						stopSearch = true;
						System.out.println("Transfer Size: " + transferStack.showSize());
						break;
					}

				}

			}

			if (deckPlaceHolder.contains(start) && deck.showSize() > 0) {
				if (putBackOnDeck && prevCard != null) {
					System.out.println("Putting back on show stack: ");
					prevCard.getValue();
					prevCard.getSuit();
					deck.putFirst(prevCard);
				}

				System.out.print("poping deck ");
				deck.showSize();
				if (prevCard != null)
					remove(prevCard);
				Card c = deck.pop().setFaceup();
				add(Solitaire.moveCard(c, WASTE_POS.x, WASTE_POS.y));
				c.repaint();
				repaint();
				prevCard = c;
			}

			if (deckPlaceHolder.contains(start) && prevCard != null) {
				movedCard = prevCard;
			}

			for (int x = 0; x < foundationStacks.length; x++) {

				if (foundationStacks[x].contains(start)) {
					source = foundationStacks[x];
					card = source.getLast();
					transferStack.putFirst(card);
					sourceIsFinalDeck = true;
					break;
				}
			}
			putBackOnDeck = true;

		}

		@Override
		public void mouseReleased(MouseEvent e) {

			stop = e.getPoint();

			boolean validMoveMade = false;

			if (movedCard != null) {

				for (int x = 0; x < tableauStacks.length; x++) {
					dest = tableauStacks[x];

					if (dest.empty() && movedCard != null && dest.contains(stop)
							&& movedCard.getValue() == Card.Value.KING) {

						movedCard.setXY(dest.getXY());
						remove(prevCard);
						dest.putFirst(movedCard);
						repaint();
						movedCard = null;
						putBackOnDeck = false;
						validMoveMade = true;
						break;
					}

					if (movedCard != null && dest.contains(stop) && !dest.empty() && dest.getFirst().getFaceStatus()
							&& validPlayStackMove(movedCard, dest.getFirst())) {
						System.out.print("moving new card ");
						movedCard.setXY(dest.getFirst().getXY());
						remove(prevCard);
						dest.putFirst(movedCard);
						repaint();
						movedCard = null;
						putBackOnDeck = false;
						validMoveMade = true;
						break;
					}
				}

				for (int x = 0; x < foundationStacks.length; x++) {
					dest = foundationStacks[x];

					if (dest.empty() && dest.contains(stop)) {
						if (movedCard.getValue() == Card.Value.ACE) {
							dest.push(movedCard);
							remove(prevCard);
							dest.repaint();
							repaint();
							movedCard = null;
							putBackOnDeck = false;
							validMoveMade = true;
							break;
						}
					}
					if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCard, dest.getLast())) {
						System.out.println("Destin" + dest.showSize());
						dest.push(movedCard);
						remove(prevCard);
						dest.repaint();
						repaint();
						movedCard = null;
						putBackOnDeck = false;
						checkForWin = true;
						validMoveMade = true;
						break;
					}
				}
			}

			if (card != null && source != null) {
				for (int x = 0; x < tableauStacks.length; x++) {
					dest = tableauStacks[x];

					if (card.getFaceStatus() == true && dest.contains(stop) && source != dest && !dest.empty()
							&& validPlayStackMove(card, dest.getFirst()) && transferStack.showSize() == 1) {
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();

						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						repaint();

						System.out.print("Destination ");
						dest.showSize();
						if (sourceIsFinalDeck)
							setScore(15);
						else
							setScore(10);
						validMoveMade = true;
						break;
					} else if (dest.empty() && card.getValue() == Card.Value.KING && transferStack.showSize() == 1) {// MOVING
																														// TO
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();

						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						repaint();

						dest.showSize();
						setScore(5);
						validMoveMade = true;
						break;
					}

					if (dest.empty() && dest.contains(stop) && !transferStack.empty()
							&& transferStack.getFirst().getValue() == Card.Value.KING) {
						System.out.println("King To Empty Stack Transfer");
						while (!transferStack.empty()) {
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							source.popFirst();
						}
						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}

					if (dest.contains(stop) && !transferStack.empty() && source.contains(start)
							&& validPlayStackMove(transferStack.getFirst(), dest.getFirst())) {

						while (!transferStack.empty()) {
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							source.popFirst();
						}

						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}
				}

				for (int x = 0; x < foundationStacks.length; x++) {

					dest = foundationStacks[x];

					if (card.getFaceStatus() == true && source != null && dest.contains(stop) && source != dest) {// TO
																													// EMPTY
																													// STACK
						if (dest.empty()) {
							if (card.getValue() == Card.Value.ACE) {
								Card c = source.popFirst();
								c.repaint();
								if (source.getFirst() != null) {

									Card temp = source.getFirst().setFaceup();
									temp.repaint();
									source.repaint();
								}

								dest.setXY(dest.getXY().x, dest.getXY().y);
								dest.push(c);

								dest.repaint();

								repaint();

								System.out.print("Destination ");
								dest.showSize();
								card = null;
								setScore(10);
								validMoveMade = true;
								break;
							} // TO POPULATED STACK
						} else if (validFinalStackMove(card, dest.getLast())) {
							Card c = source.popFirst();
							c.repaint();
							if (source.getFirst() != null) {

								Card temp = source.getFirst().setFaceup();
								temp.repaint();
								source.repaint();
							}

							dest.setXY(dest.getXY().x, dest.getXY().y);
							dest.push(c);

							dest.repaint();

							repaint();

							System.out.print("Destination ");
							dest.showSize();
							card = null;
							checkForWin = true;
							setScore(10);
							validMoveMade = true;
							break;
						}
					}

				}
			} // end cycle through play decks

			// CHECKING FOR WIN
			if (checkForWin) {
				boolean gameNotOver = false;
				// cycle through final decks, if they're all full then game over
				for (int x = 0; x < foundationStacks.length; x++) {
					dest = foundationStacks[x];
					if (dest.showSize() != 13) {
						// one deck is not full, so game is not over
						gameNotOver = true;
						break;
					}
				}
				if (!gameNotOver)
					gameOver = true;
			}

			if (checkForWin && gameOver) {
				JOptionPane.showMessageDialog(null, "Congratulations! You've Won!");
			}

			start = null;
			stop = null;
			source = null;
			dest = null;
			card = null;
			sourceIsFinalDeck = false;
			checkForWin = false;
			gameOver = false;

		}

		private void setScore(int i) {
			// TODO Auto-generated method stub

		}
	}

}
