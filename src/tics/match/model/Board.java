package tics.match.model;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;

import tics.util.MathUtil;

/** 
 * Stores and draws the grid of tiles that the game is played on. 
 * 
 * Note that this class is currently not WindowBuilder compatible.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class Board extends JPanel {
	/** The game grid. */
	private Tile[][] grid;
	
	/**
	 * Generates an empty board.
	 * 
	 * @param width the board's width in tiles.
	 * @param height the board's height in tiles.
	 * @param mouseListener the current game, which needs to handle some tile mouse events.
	 */
	public Board(int width, int height, MouseListener mouseListener) {
		grid = new Tile[width][height];
		
		super.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y] = new Tile(x, y, mouseListener);
				constraints.gridx = x;
				constraints.gridy = y;
				
				this.add(grid[x][y], constraints);
			}
		}
	}
	
	/**
	 * Create units for a player.
	 * 
	 * @param player the player to place units for.
	 * @param unitQuantity
	 */
	public void placeUnits(Player player, Dimension minimumRange, Dimension maximumRange)  {
		for (Unit unit : player.getUnits()) {
			int x, y;
			do {
				x = MathUtil.randomInteger(minimumRange.width, maximumRange.width);
				y = MathUtil.randomInteger(minimumRange.height, maximumRange.height);
			} while(!grid[x][y].isEmpty());
			//TODO: Place units nonrandomly, somehow.
			//In the meantime, don't test this code with too many units.
			grid[x][y].setUnit(unit);
		}
	}
	
	/**
	 * Handles a player starting their turn: ticks down any tile statuses they caused.
	 * 
	 * @param currentPlayerIndex the index of the player who is starting their turn.
	 */
	public void tickDownTileStatuses(int currentPlayerIndex)
	{
		for (int y = 0; y < this.getGridHeight(); y++) {
			for (int x = 0; x < this.getGridWidth(); x++) {
				grid[x][y].tickDownStatuses(currentPlayerIndex);
			}
		}
	}
	
	/**
	 * Accesses a chosen tile.
	 * 
	 * @param x the x coordinate of the tile to return.
	 * @param y the y coordinate of the tile to return.
	 * @return the tile at the given coordinates, or null if the coordinates are off the board.
	 */
	public Tile getTile(int x, int y) 
	{
		try {
			return grid[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null; //Let the caller handle this.
		}
	}
	
	/** @return the width, in tiles, of this board. */
	public int getGridWidth()
	{
		return grid.length;
	}
	
	/** @return the height, in tiles, of this board. */
	public int getGridHeight()
	{
		return grid[0].length;
	}
	
	/**
	 * Finds all tiles that are adjacent to a given tile.
	 * 
	 * @param tile the tile to get neighbours for.
	 * @return the four tiles adjacent to the given tile, or less if the tile is on the edge of the board.
	 */
	public ArrayList<Tile> getAdjacentTiles(Tile tile)
	{
		ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
		for (Direction direction : Direction.values()) {
			Tile nextTile = getTile(tile.getGridX() + direction.getXOffset(), tile.getGridY() + direction.getYOffset());
			if (nextTile != null) { //null implies that the next tile would be off the board.
				adjacentTiles.add(nextTile);
			}
		}
		return adjacentTiles;
	}
	
	/**
	 * Finds every tile within a certain orthogonal distance of a chosen tile.
	 * This produces a diamond-shaped "radius".
	 * EXTRA: Show which tiles would be affected by an AOE ability when hovering.
	 * 
	 * @param center the center from which to search for tiles in range.
	 * @param radius the maximum distance to search for tiles at. This must be non-negative.
	 * @return a set of tiles in this "radius" (always including the center tile.)
	 */
	public HashSet<Tile> getTileRadius(Tile center, int radius) 
	{
		HashSet<Tile> innerTiles = new HashSet<Tile>(); //"Visited" tiles in range.
		HashSet<Tile> outerTiles = new HashSet<Tile>(); //"Found" tiles on the edge of our current range.
		outerTiles.add(center);
		while (radius > 0) {
			HashSet<Tile> newOuterTiles = new HashSet<Tile>();
			for (Tile tile : outerTiles) { //For each tile we haven't expanded from yet...
				innerTiles.add(tile);
				for (Tile adjacentTile : getAdjacentTiles(tile)) { //...get all tiles adjacent to it...
					if (adjacentTile != null && !innerTiles.contains(adjacentTile)) {
						newOuterTiles.add(adjacentTile); //...and add them to our list if they're not already there.
					}
				}
			}
			outerTiles = newOuterTiles;
			radius--;
		}
		innerTiles.addAll(outerTiles); //Fold in the tiles on the edge of the range.
		return innerTiles;
	}
	
	/** 
	 * A simple grid-based direction enum. 
	 * Defines each direction as the x and y coordinate change 
	 * required to reach the adjacent tile in that direction.
	 */
	private enum Direction { 
		NORTH(0, -1), 
		SOUTH(0, +1), 
		EAST(+1, 0), 
		WEST(-1, 0);
		
		/** The coordinate changes needed to travel in this direction. */
		private int xOffset, yOffset;

		/**
		 * Constructs a direction.
		 * 
		 * @param xOffset the x coordinate change for this direction.
		 * @param yOffset the y coordinate change for this direction.
		 */
		Direction(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
		
		/** @return the x coordinate change for this direction. */
		public int getXOffset() {
			return xOffset;
		}

		/** @return the y coordinate change for this direction. */
		public int getYOffset() {
			return yOffset;
		}
	}
}
