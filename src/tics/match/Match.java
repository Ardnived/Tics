package tics.match;

import java.awt.Dimension;

import tics.match.model.Ability;
import tics.match.model.Board;
import tics.match.model.Player;
import tics.match.model.PlayerTheme;
import tics.match.model.Tile;
import tics.match.model.Unit;
import tics.match.view.MatchPanel;
import tics.util.MathUtil;
import tics.util.Range;
import tics.util.Util;

/**
 * A match of 'Tics.
 * 
 * TODO: Mark the selected tile.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class Match {
	/** The JPanel that displays this match. */
	private MatchPanel panel;
	/** The board that this match is played on. */
	private Board board;
	
	/** A list of the players participating in this match. */
	private Player[] players;
	/** The number of the player who's turn it is. */
	private int currentPlayerIndex;
	/** The number of the player who went first. */
	private int firstPlayerIndex;
	
	/** True if the match is being played, false if it has ended. */
	private boolean inProgress;
	/** The number of rounds passed in this match (plus one). */
	private int currentRound;
	/** The tile selected by a player, if any. If this tile has one of the current player's units on it, it can be controlled. */
	private Tile selectedTile;
	
	/** 
	 * The ability or movement range currently being displayed.
	 * The player can move a unit or use an ability by clicking on a valid tile in this range.
	 */
	private Range range;
	
	/**
	 * Creates a new match with chosen settings. 
	 * 
	 */
	public Match(int width, int height, int playerCount, int unitCount, int abilityCount) {
		MatchCommandListener commandListener = new MatchCommandListener(this);
		board = new Board(width, height, commandListener);
		panel = new MatchPanel(commandListener, commandListener, board);
		
		this.players = new Player[playerCount];
		for (int index = 0; index < playerCount; index++) {
			this.players[index] = new Player(unitCount, abilityCount, PlayerTheme.getThemeForIndex(index));
		}
		
		if (playerCount == 2) {
			int buffer = 1;
			int range = (int) Math.floor(height / 4.0);
			board.placeUnits(players[0], new Dimension(buffer, buffer), new Dimension(width - 1 - buffer, range));
			board.placeUnits(players[1], new Dimension(buffer, height - range - buffer), new Dimension(width - 1 - buffer, height - 1 - buffer));
		} else {
			// Fallback
			for (int i = 0; i < players.length; i++) {
				board.placeUnits(players[i], new Dimension(0, 0), new Dimension(width - 1, height - 1));
			}
		}
		
		startMatch();
	}
	
	
	
	// =========================================================================
	
	/** Runs the match, giving a random player the first turn. */
	private void startMatch() {
		currentPlayerIndex = MathUtil.randomInteger(0, players.length-1);
		firstPlayerIndex = currentPlayerIndex;
		inProgress = true;
		currentRound = 1;
		beginTurn();
	}
	
	/**
	 * Indicates to the players that the match is over, and stops receiving input.
	 * 
	 * @param victor the player who won the match, or null if there was a draw.
	 */
	public void endMatch(Player victor) {
		inProgress = false;
		if (victor == null) {
			System.out.println("A draw has occurred.");
		} else {
			System.out.println(victor.getTheme().toString()+" is victorious.");
		}
		cancelCurrentAction();
		panel.endMatch();
		board.repaint();
		//TODO: Display victory on the match panel.
		//TODO: Add a button for going back to the title screen.
		//TODO: dispose() of the match panel when returning to the title screen (elsewhere.)
	}

	/** 
	 * Sets up the current player's turn. 
	 * This shouldn't be called until currentPlayer has changed.
	 */
	private void beginTurn() {
		panel.getMatchInfoPanel().changeTurn(getCurrentPlayer(), currentRound);
		board.repaint(); //Update the UI on the tiles themselves.
		getCurrentPlayer().beginTurn();
		//EXTRA: Some sort of flashy turn changing animation over the board.
		//Then wait for input.
	}
	
	/** Passes the turn on to the next player. */
	public void endTurn() {
		panel.getMatchInfoPanel().setSurrenderPending(false);
		getCurrentPlayer().endTurn();
		cancelCurrentAction(); //Don't let the next player use the previous player's selections.
		panel.setTile(null, null, null, null, true);
		if (inProgress) {
			do {
				currentPlayerIndex++;
				if (currentPlayerIndex == players.length) {
					currentPlayerIndex = 0; //Change the current player index back to 0 before doing anything with it, for consistency.
				}
				if (currentPlayerIndex == firstPlayerIndex) {
					currentRound++; //Since the first player is playing again, this is a new round.
				}
				for (Player player : players) {
					player.tickDownUnitStatuses(currentPlayerIndex);
				} //Tick down statuses even for players who get their turns passed over.
				board.tickDownTileStatuses(currentPlayerIndex);
			} while (getCurrentPlayer().isDefeated()); //Skip turns for defeated players.
			
			beginTurn();
		}
	}
	
	/** 
	 * Handles a click of the surrender button:
	 * takes away the player's control of their units and makes them inactive.
	 * This way, if the match is over, players can study the final placement of units,
	 * and if it isn't, the remaining players can continue to use the surrendered units as a kind of terrain.
	 */
	public void surrender() {
		panel.getMatchInfoPanel().setSurrenderPending(false);
		panel.setTile(null, null, null, null, true);
		getCurrentPlayer().setDefeated(true);
		checkVictoryConditions();
		endTurn(); //Deactivate the surrendering units and pass the turn on.
	}
	
	/** 
	 * Checks if a player has won the match.
	 * For now, this means checking for the last player standing.
	 */
	private void checkVictoryConditions() {
		int playersRemaining = 0;
		Player potentialVictor = null;
		for (Player player : players) {
			if (!player.isDefeated()) {
				playersRemaining++;
				potentialVictor = player;
			}
		}
		if (playersRemaining == 1) {
			potentialVictor.endTurn();
			endMatch(potentialVictor);
		} else if (playersRemaining == 0) {
			endMatch(null);
		}
	}
	
	// ================================= EVENT HANDLING =======================================
	
	/**
	 * Deals with a click on a tile, which has several possible results depending on the tile.
	 * 
	 * @param tile the tile that was clicked on.
	 */
	public void handleTileClick(Tile tile) {
		panel.getMatchInfoPanel().setSurrenderPending(false); //Any tile click cancels surrendering.
		if (selectedTile != null) {
			if (range != null && selectedTile.hasUnit()) { //Actions can only happen if a unit is selected and a range is shown.
				if (range.getAbility() != null && range.getValidTargetPaths().containsKey(tile) && 
						tile.isOfType(range.getAbility().getTargetType(), getCurrentPlayer(), getTileOwner(tile)) && 
						selectedTile.getUnit().isActive()) {
					handleAbilityTargetClick(tile);
				} else if (range.getValidTargetPaths().containsKey(tile) && tile != selectedTile) {
					handleMoveTargetClick(tile);
				} else {
					handleTileSelectClick(tile); 
					//Always fall through to selecting the clicked tile if nothing else happens.
				}
			} else {
				handleTileSelectClick(tile);
			}
		} else {
			handleTileSelectClick(tile);
		}
	}
	
	/** 
	 * Deals with a tile click that will move the selected unit.
	 * 
	 * @param tile the tile that the selected unit will move to.
	 */
	private void handleMoveTargetClick(Tile target) {
		//EXTRA: Allow undoing moves.
		Unit unit = selectedTile.getUnit();
		unit.setCurrentMove(unit.getCurrentMove() - range.getValidTargetPaths().get(target).size());

		Util.moveUnit(selectedTile, target);
		selectedTile = target;
		panel.setTile(selectedTile, selectedTile, getTileOwner(selectedTile), getTileOwner(selectedTile), false);
		setRange(new Range(this, selectedTile, null));
		//Refresh the unit display to update its status.
	}

	/**
	 * Deals with a tile click that will cause a unit to act.
	 * The range object must exist and contain an ability when this is called.
	 * 
	 * @param tile the tile that the selected unit will act on.
	 */
	private void handleAbilityTargetClick(Tile tile) {
		selectedTile.getUnit().endTurn();
		
		for (Tile target : board.getTileRadius(tile, range.getAbility().getProperty(Ability.Property.EFFECT_RADIUS))) {
			//Affect every tile in the ability's area of affect - usually, this is just the target tile.
			range.getAbility().affect(selectedTile, target, currentPlayerIndex);
			target.repaint();
			checkForDeath(target);
		}
		checkForDeath(selectedTile);
		
		selectedTile.repaint(); //Make sure the selected tile redraws itself immediately.
		setRange(null); //Don't allow further action.
		panel.setTile(selectedTile, selectedTile, getTileOwner(selectedTile), getTileOwner(selectedTile), false);
		//Refresh the unit display to grey out the ability buttons and update status.
		
		checkVictoryConditions();
	}
	
	/**
	 * Deals with a tile click that will select the clicked tile,
	 * which is to say any click that doesn't cause a move or action.
	 * 
	 * @param tile the tile that was clicked on and will be selected.
	 */
	private void handleTileSelectClick(Tile tile) {
		selectedTile = tile;
		panel.setTile(tile, selectedTile, getTileOwner(tile), getTileOwner(selectedTile), true);
		setRange(null);
		
		if (tile.hasUnit() && tile.getUnit().isActive()) {
			setRange(new Range(this, selectedTile, null));
		}
	}
	
	/** 
	 * Deals with a click on an ability button.
	 * This should only be called if the unit being displayed is active.
	 * 
	 * @param ability the ability represented by the button that was clicked on.
	 */
	public void handleAbilityButtonClick(Ability ability) {
		panel.getMatchInfoPanel().setSurrenderPending(false); 
		if (inProgress && selectedTile != null) { //Ignore ability button clicks when the match is over.
			setRange(new Range(this, selectedTile, ability));
		}
	}
	
	// =========================================================================
	
	/**
	 * Checks whether a tile has a dead (<=0 HP) unit on it and removes that unit if so.
	 * 
	 * @param tile the tile to check for dead units.
	 */
	public void checkForDeath(Tile tile) {
		if (tile.hasUnit()) {
			Unit unit = tile.getUnit();
			if (unit.getCurrentHealth() <= 0) {
				getCurrentPlayer().getUnits().remove(unit);
				if (getCurrentPlayer().getUnits().isEmpty()) {
					getCurrentPlayer().setDefeated(true);
					//EXTRA: Change this to check for rout being a victory condition.
				}
				tile.setUnit(null);
			}
		}
	}
	
	/** Cancels any pending action, and deselects the current unit. */
	public void cancelCurrentAction() {
		selectedTile = null;
		setRange(null);
	}
	
	/**
	 * Replaces the currently displayed range with a new one.
	 * Handles changing the displayed range.
	 * 
	 * @param ability the ability to store a range for, or null to store a movement range.
	 */
	private void setRange(Range newRange) {
		if (range != null) {
			range.clearDisplay();
		}
		
		range = newRange;
		if (range != null) {
			range.display();
		}
	}
	
	// =========================================================================
	
	/** @return the array of players in this match. */
	public Player[] getPlayers() {
		return players;
	}
	
	/** @return the position in the players array of the one who has the turn. */
	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}
	
	public void setCurrentPlayerIndex(int value) {
		currentPlayerIndex = value;
	}
	
	/** @return the player who currently has the turn and can control their units. */
	public Player getCurrentPlayer() {
		return players[currentPlayerIndex];
	}
	
	/** @return the display panel for this match. */
	public MatchPanel getPanel() {
		return panel;
	}
	
	/** @return the board this match is played on. */
	public Board getBoard() {
		return board;
	}
	
	/** @return the tile, if any, that a player currently has selected. */
	public Tile getSelectedTile() {
		return selectedTile;
	}
	
	/** @return true if the match is being played, false it it's over. */
	public boolean isInProgress() {
		return inProgress;
	}
	
	/** @return the number of the current round. */
	public int getCurrentRound() {
		return currentRound;
	}
	
	public void setCurrentRound(int value) {
		currentRound = value;
	}
	
	/** @return the number of the player who moved first. */
	public int getFirstPlayerIndex() {
		return firstPlayerIndex;
	}
	
	public void setFirstPlayerIndex(int value) {
		firstPlayerIndex = value;
	}
	
	/** 
	 * Determines which player, if any, owns a given unit.
	 * @param unit the unit to find the owner of.
	 * 
	 * @return the player that owns this unit, or null if the unit is unowned.
	 */
	public Player getTileOwner(Tile tile) {
		if (tile != null) {
			if (!tile.hasUnit()) {
				return null;
			}
			for (Player player : players) {
				if (player.getUnits().contains(tile.getUnit())) {
					return player;
				}
			}
		}
		return null;
	}
}
