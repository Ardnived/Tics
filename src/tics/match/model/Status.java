package tics.match.model;

/** 
 * A "status effect" that can apply to a unit or a tile. 
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class Status {
	
	/** The number of turns the status will remain active for. This counts down at the beginning of the status creator's turn. */
	protected int remainingDuration;
	
	/** The index into the array of players corresponding to the player who caused this status. */
	protected int creatorIndex;
	//Note that the "creator" is the player who is acting when the status is created.
	//So if, for instance, an attacking unit receives a status effect upon hitting their target, the attacking player counts as the creator.
	
	/**
	 * Handles a player starting their turn:
	 * if the player created this status, reduces its remaining duration (otherwise do nothing.)
	 * 
	 * @param currentPlayerIndex the index of the player who is starting their turn.
	 */
	public void tickDown(int currentPlayerIndex) {
		if (creatorIndex == currentPlayerIndex) {
			remainingDuration--;
		}
	}
	
	/** @return the number of turns this status will remain active for, including the current turn. */
	public int getRemainingDuration() {
		return remainingDuration;
	}
}
