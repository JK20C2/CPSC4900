package models;

import java.awt.Color;
import java.awt.image.BufferedImage;

import assets.Asset;

/**
 * The representation of a card
 * 
 * @author shadow
 *
 */
public class Card {

	public static final int CARD_HEIGHT = 150;
	public static final int CARD_WIDTH = 100;
	public static final int CORNER_ANGLE = 25;

	public enum Suit {

		HEARTS(Color.RED, Asset.hearts), SPADES(Color.BLACK, Asset.spades), CLUBS(Color.BLACK, Asset.clubs),
		DIAMONDS(Color.RED, Asset.diamonds);

		private final Color color;
		private BufferedImage image;

		Suit(Color color, BufferedImage image) {
			this.color = color;
			this.image = image;
		}

		/**
		 * Get the color of the card
		 * 
		 * @return Color of the card, based on the type
		 */
		public Color getColor() {
			return this.color;
		}

		public BufferedImage getImage() {
			return this.image;
		}
	}

	public enum Value {
		ACE(1, "A"), TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"), SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"),
		NINE(9, "9"), TEN(10, "10"), JACK(11, "J"), QUEEN(12, "Q"), KING(13, "K");

		private final int val;
		private final String symbol;

		Value(int val, String symbol) {
			this.val = val;
			this.symbol = symbol;
		}

		/**
		 * Get the value of the card
		 * 
		 * @return integer numeric value
		 */
		public int getVal() {
			return val;
		}

		/**
		 * Get the symbol of the card
		 * 
		 * @return
		 */
		public String getSymbol() {
			return symbol;
		}

	}

	private Suit suit;
	private Value value;
	private boolean faceUp = false;

	/**
	 * @param suit
	 * @param value
	 */
	public Card(Suit suit, Value value) {
		this.suit = suit;
		this.value = value;
	}

	public Suit getSuit() {
		return suit;
	}

	public Value getValue() {
		return value;
	}

	public boolean isFaceUp() {
		return faceUp;
	}

	public Card setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
		return this;
	}

	@Override
	public String toString() {
		return this.value.getSymbol() + " " + this.suit.name();
	}

}
