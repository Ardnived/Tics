package tics.match.view;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;

import tics.match.model.Player;

import java.awt.event.ActionListener;

/**
 * A small panel which displays basic data on a game in progress.
 * For now, that means the current turn, by number and current player.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class GameInfoPanel extends JPanel {
	
	/** The text that's normally displayed on the end turn button. */
	public static final String END_TURN_BUTTON_TEXT = "End Turn";
	/** The text that's normally displayed on the surrender button. */
	public static final String SURRENDER_BUTTON_TEXT = "Surrender";
	/** 
	 * The text that's displayed on the surrender button when it has been clicked
	 * and a surrender is pending.
	 */
	public static final String SURRENDER_BUTTON_CONFIRM_TEXT = "Really?";
	
	/** The preferred dimensions of the buttons on this panel. */
	private static final Dimension BUTTON_DIMENSION = new Dimension(100, 25);
	//Ideally, these shouldn't change during execution, because that shifts everything over.
	
	/** The label that displays "Current player:" or "Last turn:" if the match has ended. */
	private JLabel playerTurnLabel;
	
	/** The label that displays the current player's name. */
	private JLabel currentPlayerLabel;
	/** The label that displays the round number. */
	private JLabel currentRoundLabel;
	/** A button that a player can click to concede the match. */
	private JButton surrenderButton;
	
	private JButton endTurnButton;
	
	/** 
	 * Creates a game information panel with dummy information.
	 * This code is compatable with WindowBuilder.
	 * However, changeTurn should be called before this is displayed.
	 * 
	 * @param actionListener the game that this panel will provide information on,
	 * which needs to know about some button presses.
	 */
	public GameInfoPanel(ActionListener actionListener) {
		//Don't worry about the "(no variable)" stuff in that WindowBuilder shows,
		//it's just a side-effect of not holding onto components as they are created.
		setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		
		super.add(playerTurnLabel = new JLabel("Current Player:"));
		super.add(currentPlayerLabel = new JLabel("PLA_YER"));
		super.add(new JLabel("|"));
		super.add(new JLabel("Round:"));
		super.add(currentRoundLabel = new JLabel("0"));
		super.add(new JLabel("|"));
		
		endTurnButton = new JButton(END_TURN_BUTTON_TEXT);
		endTurnButton.setPreferredSize(BUTTON_DIMENSION);
		endTurnButton.setActionCommand(END_TURN_BUTTON_TEXT);
		endTurnButton.addActionListener(actionListener);
		super.add(endTurnButton);
		
		surrenderButton = new JButton(SURRENDER_BUTTON_TEXT);
		surrenderButton.setPreferredSize(BUTTON_DIMENSION);
		surrenderButton.setActionCommand(SURRENDER_BUTTON_TEXT);
		surrenderButton.addActionListener(actionListener);
		super.add(surrenderButton);
	}
	
	
	/**
	 * Handles a turn ending and a new one starting:
	 * shows the player and number for the new turn.
	 * 
	 * @param player the player who's turn it is now.
	 * @param the rounds passed in the current match.
	 */
	public void changeTurn(Player player, int currentRound) {
		currentPlayerLabel.setText(player.getTheme().toString());
		currentRoundLabel.setText(String.valueOf(currentRound));
	}
	
	/**
	 * Sets whether a surrender is pending confirmation right now.
	 * 
	 * @param pending true to start a surrender, false to confirm or cancel it.
	 */
	public void setSurrenderPending(boolean pending) {
		if (pending) {
			surrenderButton.setText(SURRENDER_BUTTON_CONFIRM_TEXT);
			surrenderButton.setActionCommand(SURRENDER_BUTTON_CONFIRM_TEXT);
		} else {
			surrenderButton.setText(SURRENDER_BUTTON_TEXT);
			surrenderButton.setActionCommand(SURRENDER_BUTTON_TEXT);
		}
	}

	/**
	 * Removes turn information and deactivates buttons on this panel.
	 * To be called when the match ends.
	 */
	public void endMatch() {
		endTurnButton.setEnabled(false);
		surrenderButton.setEnabled(false);
		playerTurnLabel.setText("Last turn:");
	}
}
