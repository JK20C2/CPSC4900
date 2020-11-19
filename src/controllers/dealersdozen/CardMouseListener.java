package controllers.dealersdozen;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import client.Solitaire;
import models.Card;
import views.components.JCard;
import views.components.JCardStack;
import views.components.JCardStack.StackType;

public class CardMouseListener extends MouseAdapter {

	public static JCard originCard = null;
	public static JCard destinationCard = null;
	private Solitaire solitaire;

	private JCard jCard = null;

	public CardMouseListener(JCard jCard, Solitaire solitaire) {
		this.jCard = jCard;
		this.solitaire = solitaire;

		jCard.addMouseListener(this);
		jCard.addMouseMotionListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		originCard = jCard;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		System.out.println("Mouse released: " + originCard.getCard());

		if (originCard != null && destinationCard != null) {

			Container destContainer = destinationCard.getParent();

			if (destContainer instanceof JCardStack) {
				JCardStack destStack = (JCardStack) destContainer;

				// Handle to tableau movement

				if (destStack.getStackType().equals(JCardStack.StackType.Tableau)) {
					if ((destinationCard.getCard().getValue().getVal() == originCard.getCard().getValue().getVal() + 1)
							&& (!destinationCard.getCard().getSuit().getColor()
									.equals(originCard.getCard().getSuit().getColor()))) {

						Card card = originCard.getCard();

						Container originContainer = originCard.getParent();

						// Get the rest of the cards from the previous stack
						if (originContainer instanceof JCardStack) {
							JCardStack originStack = (JCardStack) originContainer;

							Vector<Card> transitCards = new Vector<>();

							boolean trackCards = false;

							for (int i = 0; i < originStack.getCards().size(); i++) {

								if (card.equals(originStack.getCards().get(i))) {
									trackCards = true;
								}

								if (trackCards) {
									transitCards.add(originStack.getCards().get(i));
								}
							}

							// Remove from the origin stack
							originStack.getCards().removeAll(transitCards);

							if (originStack.getCards().size() > 0) {
								originStack.getCards().get(originStack.getCards().size() - 1).setFaceUp(true);
							}

							// Add to the destination stack
							if (destStack.getCards().addAll(transitCards)) {
								System.out.println("Added");
							}

							solitaire.updateScore(5);

						}

						solitaire.drawCardStacks();

					}

				} else if (destStack.getStackType().equals(JCardStack.StackType.Foundation)) {
					if ((destinationCard.getCard().getValue().getVal() + 1 == originCard.getCard().getValue().getVal())
							&& (destinationCard.getCard().getSuit().getColor()
									.equals(originCard.getCard().getSuit().getColor()))) {

						Card card = originCard.getCard();

						Container originContainer = originCard.getParent();

						// Remove from origin vector
						if (originContainer instanceof JCardStack) {
							JCardStack originStack = (JCardStack) originContainer;

							if (originStack.getCards().remove(card)) {

								if (destStack.getCards().add(card)) {
									if (originStack.getStackType().equals(StackType.Tableau)) {
										if (originStack.getCards().size() > 0) {
											originStack.getCards().get(originStack.getCards().size() - 1)
													.setFaceUp(true);
										}
									}
								}
							}

						}

						solitaire.updateScore(10);

						solitaire.drawCardStacks();

					} else if ((destinationCard.getCard().getValue().equals(Card.Value.KING)
							&& originCard.getCard().getValue().equals(Card.Value.ACE))
							&& (destinationCard.getCard().getSuit().getColor()
									.equals(originCard.getCard().getSuit().getColor()))) {

						Card card = originCard.getCard();

						Container originContainer = originCard.getParent();

						// Remove from origin vector
						if (originContainer instanceof JCardStack) {
							JCardStack originStack = (JCardStack) originContainer;

							if (originStack.getCards().remove(card)) {
								System.out.println("Removed");
							}

						}

						// Add to the destination stack
						if (destStack.getCards().add(card)) {
							System.out.println("Added");
						}

						solitaire.updateScore(15);

						solitaire.drawCardStacks();
					}
				}
			}

			originCard = null;
			destinationCard = null;

		} else if (originCard != null && CardStackMouseListener.lastCardStackMouseOn != null) {

			Card card = originCard.getCard();

			if (CardStackMouseListener.lastCardStackMouseOn.getStackType().equals(StackType.Foundation)) {
				System.out.println("Start Foundation");
				if (card.getValue().equals(Card.Value.ACE)) {
					System.out.println("Ace");
					Container originContainer = originCard.getParent();

					if (originContainer instanceof JCardStack) {

						JCardStack originStack = (JCardStack) originContainer;

						originStack.getCards().remove(card);

						CardStackMouseListener.lastCardStackMouseOn.getCards().add(card);

						if (originStack.getStackType().equals(StackType.Tableau)) {
							if (originStack.getCards().size() > 0) {
								originStack.getCards().get(originStack.getCards().size() - 1).setFaceUp(true);
							}
						}

					}

					solitaire.drawCardStacks();
				}

			}

			if (card.getValue().equals(Card.Value.KING)) {

				Container originContainer = originCard.getParent();

				if (originContainer instanceof JCardStack) {

					JCardStack originStack = (JCardStack) originContainer;

					Vector<Card> transitCards = new Vector<>();

					boolean trackCards = false;

					for (int i = 0; i < originStack.getCards().size(); i++) {

						if (card.equals(originStack.getCards().get(i))) {
							trackCards = true;
						}

						if (trackCards) {
							transitCards.add(originStack.getCards().get(i));
						}
					}

					// Remove from the origin stack
					originStack.getCards().removeAll(transitCards);

					if (originStack.getStackType().equals(StackType.Tableau)) {
						if (originStack.getCards().size() > 0) {
							originStack.getCards().get(originStack.getCards().size() - 1).setFaceUp(true);
						}
					}

					// Add to the destination stack
					if (CardStackMouseListener.lastCardStackMouseOn.getCards().addAll(transitCards)) {
						System.out.println("Added");
					}

					solitaire.updateScore(5);

				}

				solitaire.drawCardStacks();
			}

		} else {
			System.out.println("Go on");
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);

		Container container = jCard.getParent();

		Card card = jCard.getCard();

		if (container instanceof JCardStack) {
			JCardStack stack = (JCardStack) container;

			if (stack.getStackType().equals(StackType.Deck)) {

				stack.getCards().remove(card);
				solitaire.getDealtCards().add(card.setFaceUp(true));

				solitaire.drawCardStacks();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		CardMouseListener.destinationCard = jCard;

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}
}
