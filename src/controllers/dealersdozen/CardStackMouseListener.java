package controllers.dealersdozen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import client.Solitaire;
import views.components.JCardStack;

public class CardStackMouseListener extends MouseAdapter {

	public static JCardStack lastCardStackMouseOn = null;

	private JCardStack jCardStack;
	private Solitaire solitaire;

	public CardStackMouseListener(Solitaire solitaire, JCardStack jCardStack) {
		this.jCardStack = jCardStack;
		this.solitaire = solitaire;
		jCardStack.addMouseListener(this);
		jCardStack.addMouseMotionListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		CardStackMouseListener.lastCardStackMouseOn = jCardStack;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		CardMouseListener.destinationCard = null;
		CardStackMouseListener.lastCardStackMouseOn = jCardStack;
		System.out.println("Mouse entered: " + CardStackMouseListener.lastCardStackMouseOn.getStackType().name());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		System.out.println(jCardStack.getStackType().name());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
        
		if (solitaire.getRedeals() > 0) {
        	if (jCardStack.getStackType().equals(JCardStack.StackType.Deck)) {

			for (int i = 0; i < solitaire.getDealtCards().size(); i++) {
				solitaire.getDeck().add(solitaire.getDealtCards().get(i).setFaceUp(false));
			}

			solitaire.getDealtCards().clear();

			solitaire.drawCardStacks();
			
			solitaire.decrementRedeals();
		}

	} else {
		
		JOptionPane.showMessageDialog(solitaire.getDisplay(), "Sorry you finished your redeals", "Redealing",
				JOptionPane.WARNING_MESSAGE);
			 
	}

	}
}
