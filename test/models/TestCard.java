package models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
// I used it to test if the Card class is setup appropriately
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Card.Suit;
import models.Card.Value;

class TestCard {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testConstuctor() {

		Card card = new Card(Suit.SPADES, Value.JACK);

		assertEquals(Suit.SPADES, card.getSuit());

		assertEquals(Value.JACK, card.getValue());
	}

	@Test
	void testCardColors() {

		Card spade = new Card(Suit.SPADES, Value.JACK);
		assertEquals(Color.BLACK, spade.getSuit().getColor());

		Card club = new Card(Suit.CLUBS, Value.JACK);
		assertEquals(Color.BLACK, club.getSuit().getColor());

		Card diamond = new Card(Suit.DIAMONDS, Value.ACE);
		assertEquals(Color.RED, diamond.getSuit().getColor());

		Card heart = new Card(Suit.HEARTS, Value.NINE);
		assertEquals(Color.RED, heart.getSuit().getColor());

	}

}
