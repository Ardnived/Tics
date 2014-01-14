package tics.util;

/** 
 * A type of tile, based on what (if anything) it on the tile.
 * An ability will affect (and can have its targeting blocked) by one of these types.
 * This is from a code standpoint, not a game standpoint.
 * For instance, SPRINT technically affects the user (an ALLY type) by moving them, 
 * but targets an EMPTY tile because that's where it moves them.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public enum TargetType {
	NONE, //No tile is of this type - this is used for targeting that cannot be blocked.
	BARRIER, //A barrier.
	ALLY, //A unit on your team.
	ENEMY, //A unit on your opponent's team.
	EMPTY, //A tile with no unit or barrier.
	
	ANY,
	ANY_UNIT, //ALLY or ENEMY.
	ANY_MOVEMENT_BLOCKER, //BARRIER or ENEMY.
	ANY_OBSTACLE; //ALLY, ENEMY, or BARRIER.
}
