//package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dealersdozen.DealersDozenPanel;
import internal.Card;
import internal.CardStack;
import internal.FinalStack;
import internal.GameType;

public class Solitaire {

	// CONSTANTS
	public static final int NUM_FINAL_DECKS = 4;
	public static final int NUM_PLAY_DECKS = 7;
	public static final int TABLE_HEIGHT = Card.CARD_HEIGHT * 4;
	public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 7) + 100;
	public static final Point DECK_POS = new Point(5, 5);
	public static final Point SHOW_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 5, DECK_POS.y);
	public static final Point FINAL_POS = new Point(SHOW_POS.x + Card.CARD_WIDTH + 150, DECK_POS.y);
	public static final Point PLAY_POS = new Point(DECK_POS.x, FINAL_POS.y + Card.CARD_HEIGHT + 30);

	private static final String DEAL_DOZEN_RULES = "<b>Dealers Dozen Solitaire Rules</b>"
			+ "<br><br>Uses the standard 52 card deck. "
			+ "You have 4 tableau piles (with one card per pile) and a reserve pile with 12 cards in it. Twos are removed from the deck to form 4 foundations."
			+ " You may build tableaus down in suit. Only the top card of each tableau pile and the reserve pile is available for play on the foundations. "
			+ "Foundations are built up to aces (ace high). One card or group of cards in the proper sequence can be moved to another tableau pile."
			+ " When one of the tableaus is empty it immediately fills with a card from the reserve pile if possible, then by any card."
			+ " When you have madee all the moves initially available, begin turning over cards from the stockpile to the waste pile. "
			+ "You have two redeals. Four tableaus are built down in alternating color more similar to Klondike Solitaire (Source code)";

	private static final String KLONDIKE_RULES = "<b>Klondike Solitaire Rules</b>"
			+ "<br><br> (From Wikipedia) Taking a shuffled standard 52-card deck of playing cards (without Jokers),"
			+ " one upturned card is dealt on the left of the playing area, then six downturned cards"
			+ " (from left to right).<p> On top of the downturned cards, an upturned card is dealt on the "
			+ "left-most downturned pile, and downturned cards on the rest until all piles have an "
			+ "upturned card. The piles should look like the figure to the right.<p>The four foundations "
			+ "(light rectangles in the upper right of the figure) are built up by suit from Ace "
			+ "(low in this game) to King, and the tableau piles can be built down by alternate colors,"
			+ " and partial or complete piles can be moved if they are built down by alternate colors also. "
			+ "Any empty piles can be filled with a King or a pile of cards with a King.<p> The point of "
			+ "the game is to build up a stack of cards starting with 2 and ending with King, all of "
			+ "the same suit. Once this is accomplished, the goal is to move this to a foundation, "
			+ "where the player has previously placed the Ace of that suit. Once the player has done this, "
			+ "they will have \"finished\" that suit- the goal being, of course, to finish all suits, "
			+ "at which time the player will have won.<br><br><b> Scoring </b><br><br>"
			+ "Moving cards directly from the Waste stack to a Foundation awards 10 points. However, "
			+ "if the card is first moved to a Tableau, and then to a Foundation, then an extra 5 points "
			+ "are received for a total of 15. Thus in order to receive a maximum score, no cards should be moved "
			+ "directly from the Waste to Foundation.<p>	Time can also play a factor in Windows Solitaire, if the Timed game option is selected. For every 10 seconds of play, 2 points are taken away."
			+ "<b><br><br>Notes On My Implementation</b><br><br>"
			+ "Drag cards to and from any stack. As long as the move is valid the card, or stack of "
			+ "cards, will be repositioned in the desired spot. The game follows the standard scoring and time"
			+ " model explained above with only one waste card shown at a time."
			+ "<p> The timer starts running as soon as "
			+ "the game begins, but it may be paused by pressing the pause button at the bottom of" + "the screen. ";

	// GAMEPLAY STRUCTURES
	private static FinalStack[] final_cards;// Foundation Stacks
	private static CardStack[] playCardStack; // Tableau stacks
	private static final Card newCardPlace = new Card();// waste card spot
	private static CardStack deck; // populated with standard 52 card deck

	// Set Klondike as the default game
	private static GameType currentGame = GameType.Klondike;

	/**
	 * Window frame
	 */
	private static final JFrame frame = new JFrame("Klondike Solitaire");

	/**
	 * Frame panel
	 */
	private static final JPanel mainPanel = new JPanel();
	protected static final JPanel table = new JPanel();
	private static final JPanel statusBarPanel = new JPanel();

	/**
	 * Rules Dialog Pane
	 */
	private static JEditorPane gameTitle = new JEditorPane("text/html", "");

	/**
	 * Status Bar Components
	 */
	private static JButton showRulesButton = new JButton("Show Rules");
	private static JButton newGameButton = new JButton("New Game");
	private static JButton toggleTimerButton = new JButton("Pause Timer");
	private static JTextField scoreBox = new JTextField();// displays the score
	private static JTextField timeBox = new JTextField();// displays the time
	private static JTextField statusBox = new JTextField();// status messages

	private static final Card newCardButton = new Card();// reveal waste card

	/**
	 * Keep track of time
	 */
	private static Timer timer = new Timer();
	private static ScoreClock scoreClock = new ScoreClock();

	// MISC TRACKING VARIABLES
	/**
	 * A flag of whether the time is running or not, to update the time text field
	 */
	private static boolean timeRunning = false;// timer running?

	/**
	 * Keep track of the score
	 */
	private static int score = 0;// keep track of the score

	/**
	 * Keep track of time that has elapsed
	 */
	private static int time = 0;// keep track of seconds elapsed

	/**
	 * The window menu bar
	 */
	private static JMenuBar theMenuBar;
	/**
	 * The menu bar options
	 */
	private static JMenu fileMenu, gameMenu;

	/**
	 * All the menu items
	 */
	private static JMenuItem quitMenuItem, dealersDozenMenuItem, dawsonMenuItem, duchessMenuItem, divorceMenuItem,
			doubleMenuItem, demonFanMenuItem, klondikeMenuItem;

	/**
	 * 
	 * Move card to an absolute position in a component
	 * 
	 * @param c the card to be moved
	 * @param x coordinate of card destination
	 * @param y coordinate of card destination
	 * @return the card with the changed position
	 * 
	 */
	public static Card moveCard(Card c, int x, int y) {
		c.setBounds(new Rectangle(new Point(x, y), new Dimension(Card.CARD_WIDTH + 10, Card.CARD_HEIGHT + 10)));
		c.setXY(new Point(x, y));
		return c;
	}

	/**
	 * Change the score of the game with the specified value
	 * 
	 * @param deltaScore the change value
	 */
	protected static void setScore(int deltaScore) {
		Solitaire.score += deltaScore;
		String newScore = "Score: " + Solitaire.score;
		scoreBox.setText(newScore);
		scoreBox.repaint();
	}

	/**
	 * Update the timer of the game and the display
	 */
	protected static void updateTimer() {

		Solitaire.time += 1;
		// every 10 seconds elapsed we take away 2 points
		if (Solitaire.time % 10 == 0) {
			setScore(-2);
		}

		String time = "Seconds: " + Solitaire.time;

		// Fix the clock inconsistencies in updating the text field
		SwingUtilities.invokeLater(() -> {
			timeBox.setText(time);
			timeBox.repaint();
		});

	}

	protected static void startTimer() {
		scoreClock = new ScoreClock();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}

	// the pause timer button uses this
	protected static void toggleTimer() {
		if (timeRunning && scoreClock != null) {
			scoreClock.cancel();
			timeRunning = false;
		} else {
			startTimer();
		}
	}

	/**
	 * 
	 * Update the time tracker
	 *
	 */
	private static class ScoreClock extends TimerTask {
		@Override
		public void run() {
			updateTimer();
		}
	}

	/**
	 * Refresh the game and restart everything
	 */
	private static class NewGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (currentGame.equals(GameType.Klondike)) {

				freshKlondike();

			} else if (currentGame.equals(GameType.DealersDozen)) {

				freshDealersDozen();

			}

		}

	}

	/**
	 * Toggle the time tracker
	 *
	 */
	private static class ToggleTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			toggleTimer();
			if (!timeRunning) {

				toggleTimerButton.setText("Start Timer");

			} else {

				toggleTimerButton.setText("Pause Timer");

			}
		}

	}

	/**
	 * Show a rules dialog of the current playing game
	 */
	private static class ShowRulesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			JDialog ruleFrame = new JDialog(frame, true);
			ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ruleFrame.setSize(600, 400);

			JEditorPane rulesTextPane = new JEditorPane("text/html", "");
			rulesTextPane.setEditable(false);

			if (currentGame.equals(GameType.Klondike)) {
				rulesTextPane.setText(KLONDIKE_RULES);
			} else if (currentGame.equals(GameType.DealersDozen)) {
				rulesTextPane.setText(DEAL_DOZEN_RULES);
			}

			ruleFrame.add(new JScrollPane(rulesTextPane));
			ruleFrame.setLocationRelativeTo(frame);
			ruleFrame.setVisible(true);

		}
	}

	/**
	 * This class handles all of the logic of moving the Card components as well as
	 * the game logic. This determines where Cards can be moved according to the
	 * rules of KLONDIKE Solitaire
	 */
	private static class CardMovementManager extends MouseAdapter {

		/**
		 * The card drawn on the holder
		 * 
		 * This should be the whole card stack
		 */
		private Card prevCard = null;

		/**
		 * The first card clicked
		 */
		private Card movedCard = null;

		/**
		 * Checking whether the source of a card is a foundation stack
		 */
		private boolean sourceIsFinalDeck = false;

		/**
		 * Returning a card to the deck
		 * 
		 * This should not even exist, dealing should be used
		 */
		private boolean putBackOnDeck = true;

		/**
		 * Check whether the player has won the game
		 */
		private boolean checkForWin = false;

		/**
		 * To me this is repetition
		 */
		private boolean gameOver = true;

		/**
		 * Track the point where the mouse was clicked
		 */
		private Point start = null;

		/**
		 * Track the point where the mouse was released
		 */
		private Point stop = null;

		/**
		 * No idea what this one is used for
		 */
		private Card card = null;

		/**
		 * The CardSTack that the card(s) are take from
		 */
		private CardStack source = null;

		/**
		 * The CardSTack of where the cards a being taken to or dropped
		 */
		private CardStack dest = null;

		/**
		 * For carrying the cards on transit
		 */
		private CardStack transferStack = new CardStack(false);

		/**
		 * Check whether the move is valid on the playing stack, should be of
		 * alternating suits based on color and lower than the destination card
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
				case CLUBS:
					return d_suit == Card.Suit.HEARTS || d_suit == Card.Suit.DIAMONDS;
				case HEARTS:
				case DIAMONDS:
					return d_suit == Card.Suit.SPADES || d_suit == Card.Suit.CLUBS;
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

			statusBox.setText("");
			transferStack.makeEmpty();

			/*
			 * Here we use transferStack to temporarily hold all the cards above the
			 * selected card in case player wants to move a stack rather than a single card
			 */
			for (int x = 0; x < NUM_PLAY_DECKS; x++) {
				if (stopSearch)
					break;

				source = playCardStack[x];

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
			// SHOW (WASTE) CARD OPERATIONS
			// display new show card
			if (newCardButton.contains(start) && deck.showSize() > 0) {
				if (putBackOnDeck && prevCard != null) {
					System.out.println("Putting back on show stack: ");
					prevCard.getValue();
					prevCard.getSuit();
					deck.putFirst(prevCard);
				}

				System.out.print("poping deck ");
				deck.showSize();
				if (prevCard != null)
					table.remove(prevCard);
				Card c = deck.pop().setFaceup();
				table.add(Solitaire.moveCard(c, SHOW_POS.x, SHOW_POS.y));
				c.repaint();
				table.repaint();
				prevCard = c;
			}

			// preparing to move show card
			if (newCardPlace.contains(start) && prevCard != null) {
				movedCard = prevCard;
			}

			// FINAL (FOUNDATION) CARD OPERATIONS
			for (int x = 0; x < NUM_FINAL_DECKS; x++) {

				if (final_cards[x].contains(start)) {
					source = final_cards[x];
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
			// used for status bar updates
			boolean validMoveMade = false;

			// SHOW CARD MOVEMENTS
			if (movedCard != null) {
				// Moving from SHOW TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++) {
					dest = playCardStack[x];
					// to empty play stack, only kings can go
					if (dest.empty() && movedCard != null && dest.contains(stop)
							&& movedCard.getValue() == Card.Value.KING) {
						System.out.print("moving new card to empty spot ");
						movedCard.setXY(dest.getXY());
						table.remove(prevCard);
						dest.putFirst(movedCard);
						table.repaint();
						movedCard = null;
						putBackOnDeck = false;
						setScore(5);
						validMoveMade = true;
						break;
					}
					// to populated play stack
					if (movedCard != null && dest.contains(stop) && !dest.empty() && dest.getFirst().getFaceStatus()
							&& validPlayStackMove(movedCard, dest.getFirst())) {
						System.out.print("moving new card ");
						movedCard.setXY(dest.getFirst().getXY());
						table.remove(prevCard);
						dest.putFirst(movedCard);
						table.repaint();
						movedCard = null;
						putBackOnDeck = false;
						setScore(5);
						validMoveMade = true;
						break;
					}
				}
				// Moving from SHOW TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
					dest = final_cards[x];
					// only aces can go first
					if (dest.empty() && dest.contains(stop)) {
						if (movedCard.getValue() == Card.Value.ACE) {
							dest.push(movedCard);
							table.remove(prevCard);
							dest.repaint();
							table.repaint();
							movedCard = null;
							putBackOnDeck = false;
							setScore(10);
							validMoveMade = true;
							break;
						}
					}
					if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCard, dest.getLast())) {
						System.out.println("Destin" + dest.showSize());
						dest.push(movedCard);
						table.remove(prevCard);
						dest.repaint();
						table.repaint();
						movedCard = null;
						putBackOnDeck = false;
						checkForWin = true;
						setScore(10);
						validMoveMade = true;
						break;
					}
				}
			} // END SHOW STACK OPERATIONS

			// PLAY STACK OPERATIONS
			if (card != null && source != null) { // Moving from PLAY TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++) {
					dest = playCardStack[x];
					// MOVING TO POPULATED STACK
					if (card.getFaceStatus() == true && dest.contains(stop) && source != dest && !dest.empty()
							&& validPlayStackMove(card, dest.getFirst()) && transferStack.showSize() == 1) {
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();
						// if playstack, turn next card up
						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						table.repaint();

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
																														// EMPTY
																														// STACK,
																														// ONLY
																														// KING
																														// ALLOWED
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();
						// if playstack, turn next card up
						if (source.getFirst() != null) {
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						table.repaint();

						System.out.print("Destination ");
						dest.showSize();
						setScore(5);
						validMoveMade = true;
						break;
					}
					// Moving STACK of cards from PLAY TO PLAY
					// to EMPTY STACK
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

						table.repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}
					// to POPULATED STACK
					if (dest.contains(stop) && !transferStack.empty() && source.contains(start)
							&& validPlayStackMove(transferStack.getFirst(), dest.getFirst())) {
						System.out.println("Regular Stack Transfer");
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

						table.repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}
				}
				// from PLAY TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
					dest = final_cards[x];

					table.setLayout(null);
					table.setBackground(new Color(0, 200, 0));
					table.addMouseListener(new CardMovementManager());
					table.addMouseMotionListener(new CardMovementManager());
					if (card.getFaceStatus() == true && source != null && dest.contains(stop) && source != dest) {// TO
																													// EMPTY
																													// STACK
						if (dest.empty())// empty final should only take an ACE
						{
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

								table.repaint();

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

							table.repaint();

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

			// SHOWING STATUS MESSAGE IF MOVE INVALID
			if (!validMoveMade && dest != null && card != null) {
				statusBox.setText("That Is Not A Valid Move");
			}
			// CHECKING FOR WIN
			if (checkForWin) {
				boolean gameNotOver = false;
				// cycle through final decks, if they're all full then game over
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
					dest = final_cards[x];
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
				JOptionPane.showMessageDialog(table, "Congratulations! You've Won!");
				statusBox.setText("Game Over!");
			}
			// RESET VARIABLES FOR NEXT EVENT
			start = null;
			stop = null;
			source = null;
			dest = null;
			card = null;
			sourceIsFinalDeck = false;
			checkForWin = false;
			gameOver = false;
		}
	}

	private static void freshKlondike() {

		currentGame = GameType.Klondike;

		deck = new CardStack(true);
		deck.shuffle();

		table.removeAll();
		table.setSize(TABLE_WIDTH, TABLE_HEIGHT - (statusBarPanel.getHeight()));

		table.setLayout(null);
		table.setBackground(new Color(0, 200, 0));
		table.addMouseListener(new CardMovementManager());
		table.addMouseMotionListener(new CardMovementManager());

		// reset stacks if user starts a new game in the middle of one
		if (playCardStack != null && final_cards != null) {

			for (int x = 0; x < NUM_PLAY_DECKS; x++) {
				playCardStack[x].makeEmpty();
			}

			for (int x = 0; x < NUM_FINAL_DECKS; x++) {
				final_cards[x].makeEmpty();
			}

		}
		// initialize & place final (foundation) decks/stacks
		final_cards = new FinalStack[NUM_FINAL_DECKS];

		for (int x = 0; x < NUM_FINAL_DECKS; x++) {
			final_cards[x] = new FinalStack();

			final_cards[x].setXY((FINAL_POS.x + (x * Card.CARD_WIDTH)) + 10, FINAL_POS.y);
			table.add(final_cards[x]);

		}
		// place new card distribution button
		table.add(moveCard(newCardButton, DECK_POS.x, DECK_POS.y));
		// initialize & place play (tableau) decks/stacks
		playCardStack = new CardStack[NUM_PLAY_DECKS];
		for (int x = 0; x < NUM_PLAY_DECKS; x++) {
			playCardStack[x] = new CardStack(false);
			playCardStack[x].setXY((DECK_POS.x + (x * (Card.CARD_WIDTH + 10))), PLAY_POS.y);

			table.add(playCardStack[x]);
		}

		// Dealing new game
		for (int x = 0; x < NUM_PLAY_DECKS; x++) {

			Card c = deck.pop().setFaceup();
			playCardStack[x].putFirst(c);

			for (int y = x + 1; y < NUM_PLAY_DECKS; y++) {
				playCardStack[y].putFirst(c = deck.pop());
			}
		}

		mainPanel.removeAll();
		mainPanel.add(table, BorderLayout.CENTER);
		mainPanel.add(statusBarPanel, BorderLayout.SOUTH);
		mainPanel.repaint();

		// Reset the timer
		time = 0;
		startTimer();
	}

	public static void main(String[] args) {

		Container contentPane = frame.getContentPane();

		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initialize the menu bar
		initMenuBar();

		// Set the menu bar on the frame
		frame.setJMenuBar(theMenuBar);

		// Setup the main panel
		mainPanel.setLayout(new BorderLayout());

		// Setup the status bar
		initStatusBar();
		mainPanel.add(statusBarPanel, BorderLayout.SOUTH);

		freshDealersDozen();

		contentPane.add(mainPanel);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private static void initStatusBar() {

		newGameButton.addActionListener(new NewGameListener());

		showRulesButton.addActionListener(new ShowRulesListener());

		gameTitle.setText("<b>Dealer's Dozen Solitaire</b> CPSC 4900, Fall 2020");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);

		scoreBox.setText("Score: 0");
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		timeBox.setText("Seconds: 0");
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		toggleTimerButton.addActionListener(new ToggleTimerListener());

		statusBox.setEditable(false);
		statusBox.setOpaque(false);

		statusBarPanel.add(gameTitle);
		statusBarPanel.add(newGameButton);
		statusBarPanel.add(showRulesButton);
		statusBarPanel.add(toggleTimerButton);
		statusBarPanel.add(timeBox);
		statusBarPanel.add(statusBox);
		statusBarPanel.add(scoreBox);

	}

	/**
	 * Setup the menu bar
	 */
	private static void initMenuBar() {

		theMenuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(e -> System.exit(0));
		fileMenu.add(quitMenuItem);

		theMenuBar.add(fileMenu);

		gameMenu = new JMenu("Game");
		klondikeMenuItem = new JMenuItem("Klondike");
		klondikeMenuItem.addActionListener(e -> freshKlondike());
		gameMenu.add(klondikeMenuItem);
		dealersDozenMenuItem = new JMenuItem("Dealers Dozen");
		dealersDozenMenuItem.addActionListener(e -> freshDealersDozen());
		gameMenu.add(dealersDozenMenuItem);

		duchessMenuItem = new JMenuItem("Duchess");
		gameMenu.add(duchessMenuItem);
		doubleMenuItem = new JMenuItem("Double");
		gameMenu.add(doubleMenuItem);
		dawsonMenuItem = new JMenuItem("Dawson");
		gameMenu.add(dawsonMenuItem);
		divorceMenuItem = new JMenuItem("Divorce");
		gameMenu.add(divorceMenuItem);
		demonFanMenuItem = new JMenuItem("Demon Fan");
		gameMenu.add(demonFanMenuItem);

		theMenuBar.add(gameMenu);
	}

	private static void freshDealersDozen() {
		mainPanel.removeAll();
		mainPanel.add(new DealersDozenPanel(TABLE_WIDTH, (Card.CARD_HEIGHT * 4) - (statusBarPanel.getHeight())),
				BorderLayout.CENTER);
		mainPanel.add(statusBarPanel, BorderLayout.SOUTH);
		mainPanel.repaint();
		currentGame = GameType.DealersDozen;

		time = 0;

		startTimer();
	}
}
