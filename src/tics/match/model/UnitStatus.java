package tics.match.model;

/** 
 * A status effect that applies to units. 
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class UnitStatus extends Status {
	/** The type (effect) of this status. */
	private Type type;
	
	/**
	 * Creates a status effect for a unit.
	 * 
	 * @param type an enum constant describing the effect this status will have.
	 * @param duration the turns that this status will last.
	 * @param creatorIndex the index number of the player who created this status.
	 */
	public UnitStatus(Type type, int duration, int creatorIndex) {
		this.type = type;
		this.remainingDuration = duration;
		this.creatorIndex = creatorIndex;
	}
	
	/** @return an enum constant that describes this status' effect. */
	public Type getType() {
		return type;
	}
	
	
	/** A type of status effect that a unit can have. */
	public enum Type {
		SLOWED(false, "This unit can't move as far as normal."),
		HASTED(true, "This unit can move farther than normal."),
		DEFENDED(true, "This unit takes reduced damage from enemies.");
		
		/**
		 * Constructs a unit status type with a description.
		 * 
		 * @param positive true if this type of status is useful, false if it's harmful.
		 * @param description the status's descriptive text.
		 */
		private Type(boolean positive, String description) {
			this.positive = positive;
			this.description = description;
		}
		
		/** A short description of the status effect, written in relation to the affected unit ("This unit..."). */
		private String description;
		/** True for beneficial statuses, false for detrimental ones. */
		private boolean positive;
		
		/** @return the description of this status effect. */
		public String getDescription() {
			return description;
		}
		
		/** @return true for useful statuses, false for harmful ones. */
		public boolean isPositive() {
			return positive;
		}
	}
}