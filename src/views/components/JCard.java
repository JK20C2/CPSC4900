package views.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import models.Card;

/**
 * 
 * @author shadow
 *
 */
public class JCard extends JComponent {

	private final Card card;

	public JCard(Card card) {

		this.card = card;
	}

	/**
	 * Version changes tracker for this class
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Shape roundRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Card.CORNER_ANGLE,
				Card.CORNER_ANGLE);

		if (card.isFaceUp()) {
			g2d.setColor(Color.WHITE);
			g2d.fill(roundRect);

			g2d.setPaint(card.getSuit().getColor());
			g2d.drawString(card.getValue().getSymbol(), 8, 16);
			g2d.drawString(String.valueOf(card.getValue().getSymbol()), (getWidth() - 20), (getHeight() - 8));

			g2d.drawImage(card.getSuit().getImage().getScaledInstance(24, 24, BufferedImage.TYPE_INT_ARGB), 38, 63,
					null);

		} else {

			g2d.setColor(Color.DARK_GRAY);
			g2d.fill(roundRect);
		}

		g2d.setPaint(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(roundRect);

	}

	/**
	 * @return the card
	 */
	public Card getCard() {
		return card;
	}

}
