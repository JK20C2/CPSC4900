package models;

import java.awt.Point;

import assets.Asset;

public enum GameType {

	DEALERSDOZEN(4, 4, true, new Point(16, 16), // Deck position
			new Point(16 + Card.CARD_WIDTH + 16, 16), // Dealt Position
			new Point(16 + Card.CARD_WIDTH + 16, 16 + Card.CARD_HEIGHT + 16), // Reverse position
			new Point(32 + Card.CARD_WIDTH * 2 + 16 * 2, 16), // Foundation Position
			new Point(32 + Card.CARD_WIDTH * 2 + 16 * 2, 16 + Card.CARD_HEIGHT + 16), // Tableau position
			Asset.DEALERS_DOZEN_RULES),

	KLONDIKE(4, 7, false, new Point(16, 16), // Deck position
			new Point(16 + Card.CARD_WIDTH + 16, 16), // Dealt Position
			null, // Reverse position
			new Point(16 + Card.CARD_WIDTH * 3 + 16 * 3, 16), // Foundation Position
			new Point(16, 16 + Card.CARD_HEIGHT + 16), // Tableau position
			Asset.KLONDIKE_RULES);

	private int foundationStacksCount;
	private int tableauStacksCount;
	private boolean hasReverseStack;
	private Point deckPosition;
	private Point dealtPosition;
	private Point reversePosition;
	private Point foundationPosition;
	private Point tableauPosition;
	private String rules;
	private int redeals;

	/**
	 * @param foundationStacksCount
	 * @param tableauStacksCount
	 * @param hasReverseStack
	 * @param deckPosition
	 * @param dealtPosition
	 * @param reversePosition
	 * @param foundationPosition
	 * @param tableauPosition
	 */
	private GameType(int foundationStacksCount, int tableauStacksCount, boolean hasReverseStack, Point deckPosition,
			Point dealtPosition, Point reversePosition, Point foundationPosition, Point tableauPosition, String rules) {
		this.foundationStacksCount = foundationStacksCount;
		this.tableauStacksCount = tableauStacksCount;
		this.hasReverseStack = hasReverseStack;
		this.deckPosition = deckPosition;
		this.dealtPosition = dealtPosition;
		this.reversePosition = reversePosition;
		this.foundationPosition = foundationPosition;
		this.tableauPosition = tableauPosition;
		this.rules = rules;
	}

	public int getFoundationStacksCount() {
		return foundationStacksCount;
	}

	public int getTableauStacksCount() {
		return tableauStacksCount;
	}

	public boolean hasReversePile() {
		return hasReverseStack;
	}

	public Point getDeckPosition() {
		return deckPosition;
	}

	public Point getDealtPosition() {
		return dealtPosition;
	}

	public Point getReversePosition() {
		return reversePosition;
	}

	public Point getFoundationPosition() {
		return foundationPosition;
	}

	public Point getTableauPostion() {
		return tableauPosition;
	}

	public String getRules() {
		return rules;
	}
	
	public int getRedeals() {
		return redeals;
	}

}
