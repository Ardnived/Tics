package tics.match;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import tics.match.model.Ability;
import tics.match.model.Tile;
import tics.match.view.AbilityPanel;
import tics.match.view.GameInfoPanel;

/** 
 * Helper class for Match that receives player input from various sources and tells Match how to react.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class MatchCommandListener extends MouseAdapter implements ActionListener, KeyListener {
	/** The match that this class will handle input for. */
	private Match match;
	
	/**
	 * Constructs a command listening helper for a given match.
	 * 
	 * @param match the match to send commands back to.
	 */
	public MatchCommandListener(Match match) {
		this.match = match;
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		//Match hotkeys:
		switch (event.getKeyCode()) {
			case KeyEvent.VK_ESCAPE : match.cancelCurrentAction(); break; //Escape : cancel action/deselect unit.
			case KeyEvent.VK_E : if (event.isControlDown()) { //Ctrl-E : end turn.
				match.endTurn();
			} break;
			//TODO: Add more hotkeys if necessary.
		}
	}

	//These methods need to be implemented since this class is a KeyListener, but are unused.
	//KeyAdapter can't be used here because MouseAdapter already is.
	@Override
	public void keyReleased(KeyEvent event) { }

	@Override
	public void keyTyped(KeyEvent event) { }
	
	@Override
	public void actionPerformed(ActionEvent event) {
		//Handle button presses.
		if (match.isInProgress()) { //Don't do anything if the game is over.
			String actionCommand = event.getActionCommand();
			if (actionCommand == GameInfoPanel.END_TURN_BUTTON_TEXT) { //Handle turn ending.
				match.endTurn();
			} else if (actionCommand == GameInfoPanel.SURRENDER_BUTTON_TEXT) { //Handle surrendering.
				match.getPanel().getMatchInfoPanel().setSurrenderPending(true); //"Are you sure?"
			} else if (actionCommand == GameInfoPanel.SURRENDER_BUTTON_CONFIRM_TEXT) {
				match.surrender();
			} else if (actionCommand == AbilityPanel.ABILITY_BUTTON_COMMAND) { //Handle ability activation.
				JButton abilityButton = (JButton) event.getSource();
				match.handleAbilityButtonClick(Ability.valueOf(abilityButton.getText()));
				match.getPanel().getMatchInfoPanel().setSurrenderPending(false);
				//TODO: Maybe improve this structure.
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		//Handle tile clicks.
		if (event.getSource() instanceof Tile) {
			Tile sourceTile = (Tile)event.getSource();
	        if(match.isInProgress() &&
	        		//Make sure the mouse was released while still on the tile,
	        		//by checking the event's position relative to the tile.
	        		event.getX() >= 0 && 
	        		event.getX() < sourceTile.getWidth() &&
	        		event.getY() >= 0 &&
	        		event.getY() < sourceTile.getHeight()) {
	        	//This way, pressing, dragging out of the tile, and releasing has no effect.
	        	match.handleTileClick((Tile)event.getSource());
	        }
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent event) {
		//Handle tile mouse hovering.
		if (event.getSource() instanceof Tile) {
			Tile tile = (Tile)event.getSource();
			Tile selectedTile = match.getSelectedTile();
			match.getPanel().setTile(tile, selectedTile, match.getTileOwner(tile), match.getTileOwner(selectedTile), false);
		}
		//Always display data on the tile being moused over, but don't replace selected unit data by a blank panel.
	}
	
	@Override
	public void mouseExited(MouseEvent event) {
		//When hovering off the board, only display data for the selected tile.
		if (event.getSource() instanceof Tile) {
			Tile selectedTile = match.getSelectedTile();
			match.getPanel().setTile(null, selectedTile, null, match.getTileOwner(selectedTile), true);
		}
	}
}
