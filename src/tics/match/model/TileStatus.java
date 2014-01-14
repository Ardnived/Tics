package tics.match.model;

/** 
 * A status effect that applies to tiles.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class TileStatus extends Status {
	private Type type;
	
	/**
	 * Creates a status effect for a tile.
	 * 
	 * @param type an enum constant describing the effect this status will have.
	 * @param duration the turns that this status will last.
	 * @param creatorIndex the index number of the player who created this status.
	 */
	public TileStatus(Type type, int duration, int creatorIndex)
	{
		this.type = type;
		this.remainingDuration = duration;
		this.creatorIndex = creatorIndex;
	}
	
	/** @return the type of status this is. */
	public Type getType() {
		return type;
	}
	
	/** The possible types (effects) of tile statuses.  */
	public enum Type {
		BLOCKED;
		//TODO: Consider changing this to a boolean or an int if no other statuses pop up.
	}
}
