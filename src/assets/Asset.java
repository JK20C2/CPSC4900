package assets;


import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Asset {

	public static final String DEALERS_DOZEN_RULES = "<b>Dealers Dozen Solitaire Rules</b>"
			+ "<br><br>Uses the standard 52 card deck. "
			+ "You have 4 tableau piles (with one card per pile) and a reserve pile with 12 cards in it. Twos are removed from the deck to form 4 foundations."
			+ " You may build tableaus down in suit. Only the top card of each tableau pile and the reserve pile is available for play on the foundations. "
			+ "Foundations are built up to aces (ace high). One card or group of cards in the proper sequence can be moved to another tableau pile."
			+ " When one of the tableaus is empty it immediately fills with a card from the reserve pile if possible, then by any card."
			+ " When you have made all the moves initially available, begin turning over cards from the stockpile to the waste pile. "
			+ "You have two redeals. Four tableaus are built down in alternating color more similar to Klondike Solitaire (Source code)";

	public static final String KLONDIKE_RULES = "<b>Klondike Solitaire Rules</b>"
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

	public static BufferedImage hearts, diamonds, spades, clubs;

	public static void initSuits() {
		try {
			hearts = ImageIO.read(Asset.class.getResourceAsStream("/hearts.png"));
			diamonds = ImageIO.read(Asset.class.getResource("/diamonds.png"));
			clubs = ImageIO.read(Asset.class.getResource("/clubs.png"));
			spades = ImageIO.read(Asset.class.getResourceAsStream("/spades.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
