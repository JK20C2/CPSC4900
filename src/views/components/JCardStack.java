package views.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JComponent;

import client.Solitaire;
import controllers.dealersdozen.CardMouseListener;
import models.Card;

/**
 * Draws a single stack of cards
 * 
 * @author shadow
 *
 */
public class JCardStack extends JComponent {

	public enum StackType {
		Deck, Dealt, Tableau, Foundation, Reverse
	};

	private StackType stackType;
	private Vector<Card> cards;

	public JCardStack(Solitaire solitaire, Vector<Card> cards, int cardOffset, StackType stackType) {

		this.cards = cards;
		this.stackType = stackType;

		setLayout(null);

		Vector<Card> holder = cards;

		int size = holder.size();

		for (int i = size - 1; i >= 0; i--) {
			JCard card = new JCard(holder.get(i));

			new CardMouseListener(card, solitaire);

			card.setBounds(0, i * cardOffset, Card.CARD_WIDTH, Card.CARD_HEIGHT);

			add(card);

		}
	}

	/**
	 * @return the stackType
	 */
	public StackType getStackType() {
		return stackType;
	}

	/**
	 * @return the cards
	 */
	public Vector<Card> getCards() {
		return cards;
	}

	/**
	 * Tracker id for this class
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.GREEN);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), Card.CORNER_ANGLE, Card.CORNER_ANGLE);
	}

}
