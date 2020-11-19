package client;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.SwingUtilities;

import controllers.dealersdozen.CardStackMouseListener;
import models.Card;
import models.GameType;
import views.Display;
import views.components.JCardStack;

public class Solitaire {

	private GameType currentGame = GameType.DEALERSDOZEN;

	private Display display;

	private String title;
	private int width, height;

	private Vector<Card> deck = new Vector<>();
	private Vector<Card> dealtCards = new Vector<>();
	private Vector<Card>[] foundationStacks;
	private Vector<Card>[] tableauStacks;
	private Vector<Card> reverseStack;
	private int redeals;

	/**
	 * Keep track of time
	 */
	private Timer timer = new Timer();
	private ScoreClock scoreClock = new ScoreClock();
	private boolean timeRunning = false;// timer running?
	private int score = 0;// keep track of the score
	private int time = 0;// keep track of seconds elapsed

	public Solitaire(String title, int width, int height) {

		this.title = title;
		this.width = width;
		this.height = height;

		launchDisplay();

		refreshGame();
	}

	private void launchDisplay() {

		display = new Display(this, title, width, height);
	}

	/**
	 * Initialize the deck
	 */
	public void initDeck() {

		deck.clear();
        dealtCards.clear();
		
        for (Card.Suit suit : Card.Suit.values()) {
			for (Card.Value value : Card.Value.values()) {
				deck.add(new Card(suit, value));
			}
		}
	}

	/**
	 * Shuffling a deck in the most fair-way
	 */
	public void shuffleDeck() {
		Collections.shuffle(deck);
	}

	public void gameSetup() {

		// Initialize foundation stacks
		foundationStacks = new Vector[currentGame.getFoundationStacksCount()];
		for (int i = 0; i < foundationStacks.length; i++) {
			foundationStacks[i] = new Vector<Card>();
		}

		// Initialize tableau stacks
		tableauStacks = new Vector[currentGame.getTableauStacksCount()];
		for (int i = 0; i < tableauStacks.length; i++) {
			tableauStacks[i] = new Vector<Card>();
		}

		// Initialize the reverse pile
		if (currentGame.hasReversePile()) {
			reverseStack = new Vector<Card>();
		}

		// Get the twos and put them in the foundation stacks
		if (currentGame.equals(GameType.DEALERSDOZEN)) {
			Vector<Card> twos = new Vector<Card>();

			for (int i = 0; i < deck.size(); i++) {
				Card card = deck.get(i);
				if (card.getValue().getVal() == 2) {

					twos.add(card);
					deck.remove(card);
				}
			}

			// Populate the foundations
			if (twos.size() == 4) {
				System.out.println("All the twos found there");

				for (int i = 0; i < 4; i++) {
					foundationStacks[i].add(twos.get(i).setFaceUp(true));
				}
			}
		}

		// Populate the reverse stack
		if (currentGame.hasReversePile()) {
			for (int i = 0; i < 12; i++) {
				reverseStack.add(deck.remove(deck.size() - 1).setFaceUp(true));
			}
		}

		// Deal a single card for each tableau
		if (currentGame.equals(GameType.DEALERSDOZEN)) {
			for (int i = 0; i < currentGame.getTableauStacksCount(); i++) {
				tableauStacks[i].add(deck.remove(deck.size() - 1).setFaceUp(true));
			}
		}

		// Deal cards for klondike
		if (currentGame.equals(GameType.KLONDIKE)) {

			for (int i = 0; i < currentGame.getTableauStacksCount(); i++) {

				for (int j = 0; j <= i; j++) {
					Card card = deck.remove(deck.size() - 1);

					if (i == j)
						card.setFaceUp(true);

					tableauStacks[i].add(card);
				}
			}
		}

	
		redeals = 2; //currentGame.getRedeals(); This adds the redeals graphic and sets it to 2
	
	}
	

	/**
	 * Return the game display
	 * 
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * @return the currentGame
	 */
	public GameType getCurrentGame() {
		return currentGame;
	}

	/**
	 * @param currentGame the currentGame to set
	 */
	public void setCurrentGame(GameType currentGame) {

		this.currentGame = currentGame;
		refreshGame();
	}

	public void refreshGame() {
		initDeck();

		shuffleDeck();

		gameSetup();

		drawCardStacks();

		startTimer();
	}

	public void drawCardStacks() {

		display.getGamePanel().removeAll();

		// Draw the deck first
		JCardStack deckStack = new JCardStack(this, deck, 0, JCardStack.StackType.Deck);
		new CardStackMouseListener(this, deckStack);

		deckStack.setBounds(currentGame.getDeckPosition().x, currentGame.getDeckPosition().y, Card.CARD_WIDTH,
				Card.CARD_HEIGHT);
		display.getGamePanel().add(deckStack);

		// Draw the dealt first
		JCardStack dealtStack = new JCardStack(this, dealtCards, 0, JCardStack.StackType.Deck);

		dealtStack.setBounds(currentGame.getDealtPosition().x, currentGame.getDealtPosition().y, Card.CARD_WIDTH,
				Card.CARD_HEIGHT);
		display.getGamePanel().add(dealtStack);

		// Draw the foundation stacks
		for (int i = 0; i < foundationStacks.length; i++) {
			JCardStack foundationCardsStack = new JCardStack(this, foundationStacks[i], 0,
					JCardStack.StackType.Foundation);
			new CardStackMouseListener(this, foundationCardsStack);
			foundationCardsStack.setBounds(currentGame.getFoundationPosition().x + (Card.CARD_WIDTH + 16) * i,
					currentGame.getFoundationPosition().y, Card.CARD_WIDTH, Card.CARD_HEIGHT);
			display.getGamePanel().add(foundationCardsStack);
		}

		// Draw the reverse if it exists
		if (currentGame.hasReversePile()) {
			JCardStack reverseCardsStack = new JCardStack(this, reverseStack, 4, JCardStack.StackType.Reverse);
			reverseCardsStack.setBounds(currentGame.getReversePosition().x, currentGame.getReversePosition().y,
					Card.CARD_WIDTH, Card.CARD_HEIGHT + ((reverseStack.size() - 1) * 4));
			display.getGamePanel().add(reverseCardsStack);
		}

		// Draw the tableau stacks
		for (int i = 0; i < tableauStacks.length; i++) {
			JCardStack tableauCardsStack = new JCardStack(this, tableauStacks[i], 24, JCardStack.StackType.Tableau);
			new CardStackMouseListener(this, tableauCardsStack);

			tableauCardsStack.setBounds(currentGame.getTableauPostion().x + (Card.CARD_WIDTH + 16) * i,
					currentGame.getTableauPostion().y, Card.CARD_WIDTH,
					tableauStacks[i].size() != 0 ? Card.CARD_HEIGHT + ((tableauStacks[i].size() - 1) * 24)
							: Card.CARD_HEIGHT);
			display.getGamePanel().add(tableauCardsStack);
		}

		updateDeckCount();
		display.getGamePanel().repaint();
	}

	/**
	 * @return the deck
	 */
	public Vector<Card> getDeck() {
		return deck;
	}

	/**
	 * @param deck the deck to set
	 */
	public void setDeck(Vector<Card> deck) {
		this.deck = deck;
	}

	/**
	 * @return the dealtCards
	 */
	public Vector<Card> getDealtCards() {
		return dealtCards;
	}

	/**
	 * @param dealtCards the dealtCards to set
	 */
	public void setDealtCards(Vector<Card> dealtCards) {
		this.dealtCards = dealtCards;
	}

	/**
	 * @return the reverseStack
	 */
	public Vector<Card> getReverseStack() {
		return reverseStack;
	}

	/**
	 * @param reverseStack the reverseStack to set
	 */
	public void setReverseStack(Vector<Card> reverseStack) {
		this.reverseStack = reverseStack;
	}

	public void updateDeckCount() {

		display.updateDeckCountLabel(deck.size());

	}

	protected void updateTimer() {

		time++;
		// every 10 seconds elapsed we take away 2 points
		if (time % 10 == 0) {
			score -= 2;
		}

		// Fix the clock inconsistencies in updating the text field
		SwingUtilities.invokeLater(() -> {

			display.updateScore(score);

			if (timeRunning) {
				display.setTime(time);
			}
		});

	}

	public void startTimer() {
		scoreClock = new ScoreClock();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}

	// the pause timer button uses this
	public void toggleTimer() {
		if (timeRunning && scoreClock != null) {
			scoreClock.cancel();
			timeRunning = false;
		} else {
			startTimer();
		}
	}

	private class ScoreClock extends TimerTask {
		@Override
		public void run() {
			updateTimer();
		}
	}

	public void updateScore(int change) {
		score += change;
	}
	
	public boolean gameWon() {

		boolean won = true;

		for (int i = 0; i < foundationStacks.length; i++) {
			if (foundationStacks[i].size() != 13)
				won = false;
		}

		for (int i = 0; i < tableauStacks.length; i++) {
			if (tableauStacks[i].size() != 0)
				won = false;
		}

		if (deck.size() != 0)
			won = false;

		return won;
	}

	public void decrementRedeals() {
		redeals--;

		display.updateRedeals(redeals);
	}

	public int getRedeals() {
		
		return this.redeals;
		
	}
}
