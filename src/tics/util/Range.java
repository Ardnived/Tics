package tics.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import tics.match.Match;
import tics.match.model.Ability;
import tics.match.model.Tile;
import tics.util.load.PropertiesLoader;

/**
 * The current range of an ability or unit movement.
 * This lists the tiles that can be targeted by the action in question,
 * as well as those that are in range by cannot be targeted,
 * and those that limit the action's range.
 * 
 * This class also knows how to display itself on a Board object.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class Range {
	/** The match that this range exists in. */
	protected Match match;
	//For now, match is needed here, since only it can find tile owners.
	
	/** The current origin that the finder works from. */
	protected Tile origin;
	/** 
	 * The ability that this object is the range for.
	 * If this is null, then this object is a movement range.
	 */
	private Ability ability;
	//EXTRA: Use a more flexible structure than null = movement here.
	
	/** All valid target tiles that are in range, mapped to the paths. */
	private Map<Tile, ArrayList<Tile>> validTargetPaths;
	/** All tiles that are in range but are not valid targets. */
	private ArrayList<Tile> invalidTargets;
	/** All tiles that are in range but block paths. */
	private ArrayList<Tile> blockingTiles;
	//We don't bother storing the paths to these, as they won't be used.
	
	
	/**
	 * Creates a Range object and calculates the range in question.
	 * 
	 * @param match the match that this range exists in.
	 * @param origin the tile with the unit that this range is for. The range will be centered on this tile.
	 * @param ability the ability to find a range for, or null to find a movement range.
	 */
	public Range(Match match, Tile origin, Ability ability) 
	{
		this.match = match;
		this.origin = origin;
		this.ability = ability;
		
		validTargetPaths = new ConcurrentHashMap<Tile, ArrayList<Tile>>();
		invalidTargets = new ArrayList<Tile>();
		blockingTiles = new ArrayList<Tile>();
		
		//Movement ranges can "wrap" around obstacles (since units don't have to walk in a straight line)
		//but ability ranges must have a "line of fire" to be used.
		if (ability == null) {
			algorithmWrap(origin.getUnit().getCurrentMove(), 1);
		} else {
			algorithmLine(ability.getProperty(Ability.Property.RANGE), ability.getProperty(Ability.Property.MINIMUM_RANGE));
			//The range finding methods get passed ranges rather than being expected to infer them from ability,
			//so that whether the range is "wrappable" remains independent from whether it's an ability range.
			//This way, "wrappable" abilities are possible.
		}
	}
	
	/**
	 * Uses the algorithm found on this site, http://www.oocities.org/temerra/los_rays.html
	 * 
	 * The inline documentation here will mostly try to explain what is being done, 
	 * if you want to get a better understanding of why it is being done, read the line above.
	 * 
	 * The words "origin" and "source" are used interchangeably.
	 * TODO: Make sure this isn't broken. Testing with 8-range blockable SNIPE seems to be giving silly results.
	 */
	private void algorithmLine(int max, int min) {
		// Let's use the other algorithm to approximate the tiles that we need to check.
		algorithmWrap(max, min);
		// After calling the above function, the approximation is stored in this.result
		
		// These variables are explained when they are first set.
		// They are declared in advance only for optimisation.
		Tile tile;
		ArrayList<Tile> path;
		int source_x = this.origin.getGridX(), source_y = this.origin.getGridY(), target_x, target_y;
		int x, y, sign_x, sign_y;
		boolean steep;
		float d, dx, dy;

        tileLoop: //Simply a marker to be used later by the continue keyword.
        	// This is necessary because we have nested loops.
		for (Tile current : this.validTargetPaths.keySet()) { //loop through the approximated targets
			// And then inside the loop we check to see if that approximation is a valid target.
			path = new ArrayList<Tile>(); //This variable is used to track the tiles that have to be cross to reach the "current" tile.
			target_x = current.getGridX(); //the X position of the target that is currently being considered.
			target_y = current.getGridY(); //the Y position of the target that is currently being considered.
	        steep = false; //This variable is used to track whether the slope from the origin to the target is greater than 1
	        
	        dx = Math.abs(target_x - source_x); //The distance between the target and origin, on the X axis.
	        if (target_x - source_x > 0) {
	            sign_x = 1; //Indicates that we are operating in one of the right 2 quadrants. (In a grid that is split along the X and Y axis at 0)
	        } else {
	            sign_x = -1; //Indicates that we are operating in one of the left 2 quadrants.
	        }
	        
	        dy = Math.abs(target_y - source_y); //The distance between the target and origin, on the X axis.
	        if (target_y - source_y > 0) {
	            sign_y = 1; //Indicates that we are operating in one of the top 2 quadrants.
	        } else {
	            sign_y = -1; //Indicates that we are operating in one of the bottom 2 quadrants.
	        }
	        
	        // Using sign_x and sign_y we can determine exactly which quadrant the target tile is in, relative to the origin.
	        
	        /*
	         * As we loop through the path from the origin tile to the target tile,
	         * the x and y variables will track which tile we are currently checking.
	         * We start at the origin and move towards the target.
	         */
	        x = source_x;
	        y = source_y;
	        
	        if (dy > dx) {
	        	/*
	        	 * If operating in an environment where the slope from the origin to the target is greater than 1.
	        	 * ie. steep
	        	 * We need to reverse all our x and y coordinates.
	        	 * I can't give you a good explanation of why,
	        	 * but this is definitely this keeps the code a lot lighter than special casing for steep and not steep.
	        	 */
	            steep = true;
	            x = y;
	            y = x;
	            dx = dy;
	            dy = dx;
	            sign_x = sign_y;
	            sign_y = sign_x;
	        }
	        
	        /*
	         * Now we start looping through each tile from the origin to the target
	         * As we look at each tile we check to see if it blocks the range finding or not.
	         * 
	         * This is the point where we really use the Bresenham Line Algorithm
	         * To understand this fully you should read this,
	         * http://www.oocities.org/temerra/los_rays.html
	         */
	        d = (2 * dy) - dx;
	        for (int i = 0; i < dx; i++) {
	            if (steep) {
	            	/*
	            	 * Since the slope from the origin to the target is greater than 1, 
	            	 * we previously reversed x and y to simplify our calculations,
	            	 * however we did not reverse the board's grid system, so we should swap the x and y
	            	 * back to their original values just for this call.
	            	 */
	                tile = this.match.getBoard().getTile(y, x);
	            } else { 
	                tile = this.match.getBoard().getTile(x, y);
	            }
	            
	            if (tile == null || tile.isOfType(ability.getBlockingType(), match.getCurrentPlayer(), match.getTileOwner(tile))) {
	            	// If the tile we are checking at the moment is null, or would block this range search.
	            	// Then remove "current" target that we are considering from the list of results,
	            	// and skip back to the beginning of the first loop so we can consider the next potential target.
	            	this.validTargetPaths.remove(current);
	                continue tileLoop; // Jump back to where we set the "tileLoop:" marker
	            } else {
	            	// If the tile is valid, then add it to the path, 
	            	// which is the list of tiles that you have to cross to reach the "current" target.
	            	path.add(tile);
	            }
	            
	            // START BRESENHAM ALGORITHM
	            /* This is the core of the algorithm.
	             * Basically the point of this code is to determine precisely the next tile
	             * in the path from the origin to the target that we have to check.
	             * Once again, this site http://www.oocities.org/temerra/los_rays.html
	             * does a good job of explaining why and how.
	             */
	            while (d >= 0) {
	                y = y + sign_y;
	                d = d - (2 * dx);
	            }
	            
	            x = x + sign_x;
	            d = d + (2 * dy);
	            // END BRESENHAM ALGORITHM
	        }
	        
	        // Since the current target made it this far, that means it's valid.
	        // Store it in the results list, along with the path to reach it.
	        this.validTargetPaths.put(current, path);
		}
	}
	
	/**
	 * Calculates a range where paths to targets can wind around obstacles rather than having to be straight lines.
	 * This is done via a breadth-first search that vaguely resembles Dijkstra's algorithm.
	 * 
	 * @param maximumRange the maximum length of a path to a valid target tile. In other words, the range being checked.
	 * @param minimumRange the minimum length of a path to a valid target.
	 */
	private void algorithmWrap(int maximumRange, int minimumRange) {
		TargetType targetType;
		TargetType blockingType;
		if (ability == null) {
			targetType = TargetType.EMPTY;
			blockingType = TargetType.ANY_MOVEMENT_BLOCKER;
		} else {
			targetType = ability.getTargetType();
			blockingType = ability.getBlockingType();
		}
		
		/*
		 * A list of tile paths that we plan to check to see if they are paths to valid targets,
		 * but that we have not looked at yet. The reason this is exists is because every time
		 * you expand outwards from a tile there will be up to 4 adjoining tiles to look at, 
		 * and we want to check the paths we added earliest first, so that the search remains breadth-first.
		 */
		Queue<ArrayList<Tile>> pathQueue = new LinkedList<ArrayList<Tile>>();
		//Also, we use the versatile linked list class as a queue here.
		
		/*
		 * A list of tiles which we have visited.
		 * When checking a path in the queue, we first check to see if its destination has already been visited
		 * before checking if it's a valid target.
		 * We know that the current route being considered can't be shorter than one that
		 * was already considered because of the breadth-first property of this algorithm.
		 */
		HashSet<Tile> visitedTiles = new HashSet<Tile>();
		
		ArrayList<Tile> initialList = new ArrayList<Tile>(); //Create a path to hold the origin tile, which will be checked first.
		initialList.add(origin);
		pathQueue.add(initialList);
		
		while (pathQueue.size() > 0) { // As long as we still have paths in the queue to look at...
			ArrayList<Tile> currentPath = pathQueue.poll(); //...pull a path out of queue.
			int currentDistance = currentPath.size()-1; //The distance we're working with is the number of tiles in the path, not counting the origin.
			Tile currentTile = currentPath.get(currentPath.size()-1); //And the tile we're checking is the last one in the path.
			if (currentDistance <= maximumRange && // Only check tiles that are within range.
					!visitedTiles.contains(currentTile)) { 
				//Don't re-check visited tiles - since this algorithm is breadth-first, we already have a shortest path to them (if one exists).
				
				if (currentTile.isOfType(targetType, match.getCurrentPlayer(), match.getTileOwner(currentTile)) && 
						currentDistance >= minimumRange) {
					// If we've gotten this far the tile is of the type we are looking for, so add it to the results list.
					ArrayList<Tile> finalPath = new ArrayList<Tile>(currentPath);
					finalPath.remove(currentTile); //Don't include the target tile in the path - this makes range calculation easier.
					validTargetPaths.put(currentTile, finalPath);
				} else if (currentTile.isOfType(blockingType, match.getCurrentPlayer(), match.getTileOwner(currentTile))) {
					blockingTiles.add(currentTile);
				} else {
					invalidTargets.add(currentTile);
				}
				//TODO: Make bresenham (algorithmLine) get rid of unreachable tiles in the invalid targets list too.
				//This is probably best done by keeping both lists together until after bresenham finishes, then checking which targets are valid.
				//However, if bresenham changes a lot anyway, that might not be necessary, hence the current implementation.
				
				// Since we've not investigated this tile, added to the "visited" cache.
				visitedTiles.add(currentTile);
				
				if (!currentTile.isOfType(blockingType, match.getCurrentPlayer(), match.getTileOwner(currentTile)) || //Don't expand from tiles that block targeting...
						currentTile == origin) { //...unless that's the origin tile, which should never be able to block a range.
					//Otherwise, get every adjacent tile and add it to the queue.
					for (Tile tile : match.getBoard().getAdjacentTiles(currentTile)) {
						// Assuming that the tile is not null, add it to the queue.
						ArrayList<Tile> newPath = new ArrayList<Tile>(currentPath);
						newPath.add(tile);
						pathQueue.add(newPath);
					}
				}
			}
		}
	}
	
	/** Shows this range by colouring the tiles involved with it. */
	public void display() {
		// EXTRA: Make it so that when you hover over a valid target it shows you the path to it.
		for (Tile tile : validTargetPaths.keySet()) {
			tile.setBackground(PropertiesLoader.getColour("target_tile"));
		}
		
		for (Tile tile : invalidTargets) {
			tile.setBackground(PropertiesLoader.getColour("invalid_target_tile"));
		}
		
		for (Tile tile : blockingTiles) {
			tile.setBackground(PropertiesLoader.getColour("range_blocking_tile"));
			//TODO: Get barriers (if they still exist) displaying in these.
			//Currently, Tiles with barriers must set their own background repeatedly, so this call does nothing to them.
		}
	}
	
	/** Stops showing this range - sets the tiles involved back to their normal colours. */
	public void clearDisplay() {
		//TODO: Don't set BLOCKED tiles to NORMAL_TILE colour. Waiting to fix this on a decision on barriers.
		//That, or make barriers display a graphic rather than a background.
		for (Tile tile : validTargetPaths.keySet()) {
			tile.setBackground(PropertiesLoader.getColour("normal_tile"));
		}
		
		for (Tile tile : invalidTargets) {
			tile.setBackground(PropertiesLoader.getColour("normal_tile"));
		}
		
		for (Tile tile : blockingTiles) {
			tile.setBackground(PropertiesLoader.getColour("normal_tile"));
		}
	}
	
	/** @return the ability that can be used within this range. */
	public Ability getAbility() {
		return ability;
	}
	
	/** @return the valid target tiles for this range, mapped to the paths from the origin to them. */
	public Map<Tile, ArrayList<Tile>> getValidTargetPaths() {
		return validTargetPaths;
	}
	
	/** @return the list of tiles that are in range but can't be targeted. */
	public ArrayList<Tile> getInvalidTargets() {
		return invalidTargets;
	}

	/** @return the list of tiles that block the current range. (AND aren't valid targets.) */
	public ArrayList<Tile> getBlockingTiles() {
		return blockingTiles;
	}
}
