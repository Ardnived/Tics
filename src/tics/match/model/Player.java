package tics.match.model;

import java.io.Serializable;
import java.util.HashSet;

/** 
 * A faction in a match.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class Player implements Serializable {
	/** A randomly generated value, used by Java to identify saved instances of this class. */
	private static final long serialVersionUID = 4147598720532175778L;
	
	/** The units owned by this player. */
	private HashSet<Unit> units; //TODO: Possibly order this collection to allow for predictable unit placement.
	/** The generator used to create images and names for this player's units. */
	private PlayerTheme theme;
	/** True if this player has surrendered or lost the match, false otherwise. */
	private boolean defeated;
	
	/** 
	 * Creates a new player with randomly generated units.
	 *  
	 * @param numberOfUnits the number of units the player will control.
	 * @param abilitiesPerUnit the number of randomly generated abilities each of the player's units will have.
	 * @param theme the combined visual style and naming convention for this player's faction.
	 */
	public Player(int numberOfUnits, int abilitiesPerUnit, PlayerTheme theme) 
	{
		this.theme = theme;
		units = new HashSet<Unit>();
		for (int i = 0; i < numberOfUnits; i++) {
			units.add(new Unit(theme, abilitiesPerUnit));
		}
	}
	
	/** 
	 * Creates a new player with an existing set of units.
	 *  
	 * @param units the units the player will control.
	 * @param theme the combined visual style and naming convention for this player's faction.
	 */
	public Player(HashSet<Unit> units, PlayerTheme theme) 
	{
		this.theme = theme;
		this.units = units;
	}
	
	/** @return all the player's units. */
	public HashSet<Unit> getUnits() {
		return units;
	}

	/** @return the player's units' "theme". */
	public PlayerTheme getTheme() {
		return theme;
	}
	
	/** Starts this player's turn by activating all their units. */
	public void beginTurn() 
	{
		for (Unit unit : units) {
			unit.beginTurn();
		}
	}
	
	/**
	 * Handles any player starting their turn: reduced the remaining duration on 
	 * 
	 * @param currentPlayerIndex the index of the player who is starting their turn.
	 */
	public void tickDownUnitStatuses(int currentPlayerIndex)
	{
		for (Unit unit : units) {
			unit.tickDownStatuses(currentPlayerIndex);
		}
	}
	
	/** Ends this player's turn by deactivating all their units. */
	public void endTurn() 
	{
		for (Unit unit : units) {
			unit.endTurn();
		}
	}
	
	/** @return true if this player has been defeated and can no longer act. */
	public boolean isDefeated() {
		return defeated;
	}
	
	/**
	 * Changes whether a player is considered defeated, which will also change whether they can act.
	 * 
	 * @param defeated true to mark this player as defeated, false to mark them as yet-undefeated.
	 */
	public void setDefeated(boolean defeated) {
		this.defeated = defeated;
	}
}
