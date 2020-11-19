package controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomMouseListener extends MouseAdapter {

	@Override
	public void mouseMoved(MouseEvent e) {
		System.out.println(e.getPoint());
	}

}
