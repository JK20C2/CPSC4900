package client;

import java.util.logging.Level;
import java.util.logging.Logger;

import assets.Asset;

public class Launcher {

	private static final String TAG = Launcher.class.getSimpleName();

	public static void main(String[] args) {

		Asset.initSuits();

		Solitaire solitaire = new Solitaire("Solitaire Games", 900, 600);

		Logger.getLogger(TAG).log(Level.INFO, solitaire.getClass().getSimpleName() + " Game Launched");

	}
}
