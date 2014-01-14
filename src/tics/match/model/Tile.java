package tics.match.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import tics.util.TargetType;
import tics.util.load.PropertiesLoader;

/** 
 * A tile on the game board, including information about what's ON the tile.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class Tile extends JPanel implements MouseListener {
	//TODO: Commenting.
	public static final int PADDING = 3;
	public static final int WIDTH = PADDING*2 + 48;
	public static final int HEIGHT = WIDTH;
	
	//EXTRA: If possible, change this so that tile positions are only stored in one place.
	/** The position of this tile in the Match's tile grid. */
	private final int gridX, gridY;
	/** The unit that is standing on the tile, if there is one. */
	private Unit unit;
	/** The "status effects" that this tile currently has, mapped to their remaining duration. */
	private HashSet<TileStatus> statuses;
	
	private BufferedImage selectorIcon, selectorPressIcon;
	
	private boolean mouseHovering, mouseClicking;
	
	/**
	 * @param x the x position of this tile in the Match's tile grid.
	 * @param y the y position of this tile in the Match's tile grid.
	 * @param mouseListener the game this tile is in, which will need to know about some of its mouse events.
	 */
	public Tile(int x, int y, MouseListener mouseListener) {
		super();
		
		Dimension size = new Dimension(Tile.WIDTH, Tile.HEIGHT);
		super.setPreferredSize(size);
		
		this.gridX = x;
		this.gridY = y;
		
		this.addMouseListener(this);
		this.addMouseListener(mouseListener);
		statuses = new HashSet<TileStatus>();
		super.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		super.setBackground(PropertiesLoader.getColour("normal_tile"));
		//try {
			//TODO: Load these via ImageLoader, if at all.
			//selectorIcon = ImageIO.read(new File("img/selector-hover.png"));
			//selectorPressIcon = ImageIO.read(new File("img/selector-press.png"));
		//} catch (IOException e) {
			//TODO: Handle if we're using this code.
		//}
		
		mouseHovering = false;
		mouseClicking = false;
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		//EXTRA: If implementing crashing, find a way to crash from exceptions thrown here.
		//TODO: Split this up.
		super.paintComponent(graphics);
		
		if (this.hasUnit()) {
			//EXTRA: Grey out unowned units.
			
			//TODO: Maybe tweak more positions.
			int activeIndicatorX = Tile.WIDTH - Tile.PADDING - 5;
			int activeIndicatorY = Tile.PADDING;
			int activeSize = 5;
			
			//TODO: Fix this conditional.
			//if (unit.getUnitOwner() != null) { 
				//Don't draw a move square for units who's player has lost.
				//TODO: different inactive colour for enemies. This means getting currentPlayer here somehow.
				if (!unit.isActive()) {
					graphics.setColor(PropertiesLoader.getColour("inactive"));
				} else if (unit.getCurrentMove() < unit.getMove()) {
					graphics.setColor(PropertiesLoader.getColour("partially_active"));
				} else {
					graphics.setColor(PropertiesLoader.getColour("fully_active"));
				}
				
				graphics.fillRect(activeIndicatorX, activeIndicatorY, activeSize, activeSize);
			//}
				
			int hpBarX = Tile.PADDING + 3;
			int hpBarY = Tile.HEIGHT - Tile.PADDING - 6;
			int hpBarFullWidth = Tile.WIDTH - Tile.PADDING*2 - 5;
			int hpBarPartialWidth = (int) (hpBarFullWidth*((double) unit.getCurrentHealth() / Unit.BASE_HEALTH));
			int hpBarHeight = 5;

			if (unit.getCurrentHealth() < Ability.ATTACK.getProperty(Ability.Property.DAMAGE)) {
				graphics.setColor(PropertiesLoader.getColour("very_low_health")); //If a unit will die from one attack, make that obvious.
			} else if (unit.getCurrentHealth() < 2*Ability.ATTACK.getProperty(Ability.Property.DAMAGE)) {
				graphics.setColor(PropertiesLoader.getColour("low_health"));
			} else {
				graphics.setColor(PropertiesLoader.getColour("health"));
			}
			graphics.fillRect(hpBarX, hpBarY, hpBarPartialWidth, hpBarHeight);
			
			//Draw a border for the health bar.
			graphics.setColor(Color.BLACK);
			graphics.drawRect(hpBarX-1, hpBarY-1, hpBarFullWidth, hpBarHeight);
			
			/*
			if (Main.ENABLE_TEAMBOXES) {
				graphics.setColor(Color.BLUE); //Change this to use PlayerTheme.getTeamColour()
				graphics.drawRect(1, 1, Tile.WIDTH-3, Tile.HEIGHT-3);
			}
			*/
			
			graphics.setColor(PropertiesLoader.getColour("buff"));
			try {
				graphics.drawImage(getUnit().getImage(), PADDING, PADDING, null);
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (this.hasStatus(TileStatus.Type.BLOCKED)) {
			this.setBackground(PropertiesLoader.getColour("blocked_tile"));
		}
		
		/* Old Selector Implementation
		if (mouseClicking) {
			graphics.drawImage(selectorPressIcon, Tile.PADDING, Tile.PADDING, this);
		} else if (mouseHovering) {
			graphics.drawImage(selectorIcon, Tile.PADDING, Tile.PADDING, this);
		}
		*/

		if (mouseClicking) {
			graphics.setColor(Color.RED);
			graphics.drawRect(1, 1, Tile.WIDTH-3, Tile.HEIGHT-3);
		} else if (mouseHovering) {
			graphics.setColor(Color.BLACK);
			graphics.drawRect(1, 1, Tile.WIDTH-3, Tile.HEIGHT-3);
		}
	}
	
	/**
	 * Handles any player starting their turn: reduces the remaining duration on this tile's statuses if it's time to do so.
	 * This should be called even when passing over the turn for a defeated player.
	 * 
	 * @param currentPlayerIndex the index of the player who started their turn.
	 */
	public void tickDownStatuses(int currentPlayerIndex) {
		// Tick down all of the tile's status effects.
		for (TileStatus status : statuses) {
			status.tickDown(currentPlayerIndex);
			if (status.getRemainingDuration() <= 0) {
				statuses.remove(status);
				//TODO: If we had multiple statuses, then this would cause visual problems.
				this.setBackground(PropertiesLoader.getColour("normal_tile"));
			}
		}
	}
	
	/** @return true if there is a unit on this tile. */
	public boolean hasUnit() {
		return unit != null;
	}
	
	/** @return the unit standing on the tile, or null if no unit is on this tile. */
	public Unit getUnit() {
		return unit;
	}
	
	/** 
	 * Changes the unit on this tile. If there was already a unit here, be sure to move it off first!
	 * 
	 * @param unit the new unit to put on the tile.
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	/** 
	 * Checks if this tile is of a given target type.
	 * 
	 * @param type
	 * @param currentPlayer the player taking their turn right now.
	 * @param owner the player that owns the unit on this tile.
	 * This affects the distinction between the ALLY and ENEMY tile types.
	 * @return
	 */
	public boolean isOfType(TargetType type, Player currentPlayer, Player owner) {
		switch (type) {
			case NONE: return false;
			case BARRIER: return hasStatus(TileStatus.Type.BLOCKED);
			case ALLY : return hasUnit() && currentPlayer == owner; 
			case ENEMY : return hasUnit() && currentPlayer != owner; 
			//EXTRA: make these work for allied players.
			case EMPTY : return !hasStatus(TileStatus.Type.BLOCKED) && !hasUnit();
			case ANY_UNIT : return hasUnit();
			case ANY_MOVEMENT_BLOCKER : 
				return isOfType(TargetType.ENEMY, currentPlayer, owner) || isOfType (TargetType.BARRIER, currentPlayer, owner);
			case ANY_OBSTACLE : 
				return isOfType(TargetType.ANY_UNIT, currentPlayer, owner) || isOfType (TargetType.BARRIER, currentPlayer, owner);
			case ANY : return true;
			default : return false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		//No response to clicks in this class - Match handles that.
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		mouseHovering = true; //Show the targeting cursor.
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent event) {
		mouseHovering = false;
		repaint(); //Stop showing the targeting cursor.
	}

	@Override
	public void mousePressed(MouseEvent event) {
		mouseClicking = true;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		mouseClicking = false;
		repaint();
	}
	
	/** @return true if this tile is passable and unoccupied. */
	public boolean isEmpty() {
		return unit == null && !hasStatus(TileStatus.Type.BLOCKED);
	}
	
	/**
	 * Applies the effect of a status to this tile.
	 * 
	 * @param status the status to apply to this tile.
	 */
	public void applyStatus(TileStatus status) {
		this.statuses.add(status);
	}
	
	/** 
	 * Checks whether this tile has a certain type of status.
	 * @param type the type of status to check for.
	 * @return true if this tile has a status of the type indicated.
	 */
	public boolean hasStatus(TileStatus.Type type) {
		for (TileStatus status : statuses) {
			if (status.getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	/** @return the x position of this tile on the grid. */
	public int getGridX() {
		return gridX;
	}

	/** @return the y position of this tile on the grid. */
	public int getGridY() {
		return gridY;
	}

	/**
	 * @param type the type of status we are querying about.
	 * @return the remaining duration for the requested status type. Or 0 if the tile doesn't have that status.
	 */
	public int getStatusDuration(TileStatus.Type type) {
		if (!this.hasStatus(type)) { // Check if the tile even has the status at all.
			return 0; // If not, then that status has 0 effectively 0 duration.
		} else {
			// Loop through all the status the tile does have.
			for (TileStatus status : this.statuses) {
				// Check if it is the status we are looking for.
				if (status.getType() == type) {
					// If so, return that status' remaining duration.
					return status.getRemainingDuration();
				}
			}
			
			// This return value is a fallback, 
			// but theoretically the function should never reach this point.
			// Because we already made sure that the status existed, before we looped.
			return 0;
		}
	}
}
