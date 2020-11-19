package views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import client.Solitaire;
import models.GameType;

/**
 * 
 * @author shadow
 *
 */
public class Display extends JFrame {

	/**
	 * The version tracker for the class
	 */
	private static final long serialVersionUID = 1L;
	private int width, height;

	/**
	 * Top menu bar, menus and menu items
	 */
	private JMenuBar menuBar;

	private JMenu fileMenu;
	private JMenu gameMenu;
	private JMenu viewMenu;

	private JMenuItem exitMenuItem;
	private JMenuItem dealersDozenMenuItem;
	private JMenuItem klondikeMenuItem;
	private JMenuItem dawsonMenuItem, duchessMenuItem, divorceMenuItem, doubleMenuItem, demonFanMenuItem;

	private JButton showRulesButton;
	private JButton newGameButton;
	private JButton toggleTimerButton;

	private JPanel gamePanel;

	private Solitaire solitaire;

	/**
	 * The status bar and the components
	 */
	private JPanel statusBar;
	private JLabel statusLabel, redealsLeft, timeLabel, scoreLabel;

	public Display(Solitaire solitaire, String title, int width, int height) {
		super(title);

		this.solitaire = solitaire;

		this.height = height;
		this.width = width;

		initWindow();
	}

	private void initGamePanel() {

		gamePanel = new JPanel();
		gamePanel.setLayout(null);
		gamePanel.setBackground(new Color(0, 80, 0));
		add(gamePanel, BorderLayout.CENTER);

	}

	private void initStatusBar() {
		statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());

		JPanel leftPanel = new JPanel();
		statusLabel = new JLabel("Stock Left: 24");
		redealsLeft = new JLabel("Redeals Left: 2");
		leftPanel.add(statusLabel);
		leftPanel.add(redealsLeft);

		JPanel centerPane = new JPanel();
		showRulesButton = new JButton("Show Rules");
		showRulesButton.addActionListener(c -> showRulesDialog());
		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(c -> solitaire.refreshGame());
		toggleTimerButton = new JButton("Pause Timer");
		toggleTimerButton.addActionListener(c -> solitaire.toggleTimer());
		centerPane.add(showRulesButton);
		centerPane.add(newGameButton);
		centerPane.add(toggleTimerButton);

		JPanel rightPanel = new JPanel();
		timeLabel = new JLabel("Time: 00:00:00");
		scoreLabel = new JLabel("Score: 0");
		rightPanel.add(timeLabel);
		rightPanel.add(scoreLabel);

		statusBar.add(leftPanel, BorderLayout.WEST);
		statusBar.add(centerPane, BorderLayout.CENTER);
		statusBar.add(rightPanel, BorderLayout.EAST);

		add(statusBar, BorderLayout.SOUTH);

	}

	private void initWidgets() {

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		gameMenu = new JMenu("Game");
		viewMenu = new JMenu("View");

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(c -> System.exit(0));
		fileMenu.add(exitMenuItem);

		
		dealersDozenMenuItem = new JMenuItem("Dealers Dozen");
		dealersDozenMenuItem.addActionListener(c -> solitaire.setCurrentGame(GameType.DEALERSDOZEN));
		gameMenu.add(dealersDozenMenuItem);

		klondikeMenuItem = new JMenuItem("Klondike");
		klondikeMenuItem.addActionListener(c -> solitaire.setCurrentGame(GameType.KLONDIKE));
		gameMenu.add(klondikeMenuItem);

		duchessMenuItem = new JMenuItem("Duchess");
		duchessMenuItem.addActionListener(c -> notImplemented("Duchess"));
		gameMenu.add(duchessMenuItem);

		doubleMenuItem = new JMenuItem("Double");
		doubleMenuItem.addActionListener(c -> notImplemented("Double"));
		gameMenu.add(doubleMenuItem);

		dawsonMenuItem = new JMenuItem("Dawson");
		dawsonMenuItem.addActionListener(c -> notImplemented("Dawson"));
		gameMenu.add(dawsonMenuItem);

		divorceMenuItem = new JMenuItem("Divorce");
		divorceMenuItem.addActionListener(c -> notImplemented("Divorce"));
		gameMenu.add(divorceMenuItem);

		demonFanMenuItem = new JMenuItem("Demon Fan");
		demonFanMenuItem.addActionListener(c -> notImplemented("Demon Fan"));
		gameMenu.add(demonFanMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(gameMenu);

		setJMenuBar(menuBar);

	}

	private void initWindow() {

		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());

		initWidgets();

		initGamePanel();

		initStatusBar();

		this.setVisible(true);

	}

	public JPanel getGamePanel() {
		return gamePanel;
	}

	public void updateTitle(String title) {

		this.setTitle(title);

	}

	public void updateDeckCountLabel(int val) {
		statusLabel.setText("Stock Left: " + val);
	}

	public void showRulesDialog() {

		JDialog ruleFrame = new JDialog(this, true);
		ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		ruleFrame.setSize(600, 400);

		JEditorPane rulesTextPane = new JEditorPane("text/html", "");
		rulesTextPane.setEditable(false);

		rulesTextPane.setText(solitaire.getCurrentGame().getRules());

		ruleFrame.add(new JScrollPane(rulesTextPane));
		ruleFrame.setLocationRelativeTo(this);
		ruleFrame.setVisible(true);
	}

	public void updateScore(int i) {
		scoreLabel.setText("Score: " + i);
	}

	public void setTime(long seconds) {
		timeLabel.setText("Time: " + seconds + " seconds");
	}

	private void notImplemented(String gameName) {
		JOptionPane.showMessageDialog(this, gameName + " not yet implemented", "Change Game",
				JOptionPane.WARNING_MESSAGE);
	}

}
