package tics.match.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import tics.util.MathUtil;
import tics.util.Util;
import tics.util.load.ImageLoader;

/** 
 * A creature, controlled by a player, which participates in combat.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class Unit implements Serializable {
	/** A randomly generated value, used by Java to identify saved instances of this class. */
	private static final long serialVersionUID = -6371218202599827010L;
	/** A unit's normal orthogonal movement range for one turn. */
	public static final int BASE_MOVE = 3;
	/** A unit's normal starting and *maximum* health. */
	public static final int BASE_HEALTH = 15;
	
	/** The unit's status effects, mapped to their remaining durations. */
	private HashSet<UnitStatus> statuses;
	/** The unit's current (remaining) hit points. */
	private int currentHealth;
	/** The unit's current (remaining) move range. */
	private int currentMove;
	/** The unit's ability set. */
	private ArrayList<Ability> abilities;
	/** The unit's name. */
	private String name;
	/** The path to the unit's torso image. */
	private String torsoImagePath;
	/** True if it's the unit's turn to move. This becomes false when the unit uses an ability. */
	private boolean active;
	
	
	/**
	 * Creates a unit with chosen abilities.
	 * 
	 * @param theme a combined graphical style and naming convention for the unit.
	 * @param abilities the list of abilities this unit will have. This should normally contain ATTACK.
	 */
	public Unit(PlayerTheme theme, ArrayList<Ability> abilities) {
		name = theme.generateName(); //EXTRA: Prevent duplicate names somehow.
		currentHealth = BASE_HEALTH;
		statuses = new HashSet<UnitStatus>();
		this.abilities = abilities;
		
		torsoImagePath = theme.getRandomImagePath();
	}
	
	/** 
	 * Creates a unit with a number of random abilities, as well as a normal attack. 
	 * 
	 * @param theme a combined graphical style and naming convention for the unit.
	 * @param numberOfAbilities the number of special abilities the unit will have.
	 */
	public Unit(PlayerTheme theme, int numberOfAbilities) {
		this(theme, generateAbilities(numberOfAbilities));
	}
	
	/**
	 * Randomly selects abilities for a unit.
	 * 
	 * @param numberOfAbilities the number of abilities to choose.
	 * @return the abilities that were randomly generated for this unit.
	 */
	private static ArrayList<Ability> generateAbilities(int numberOfAbilities) {
		ArrayList<Ability> abilities = new ArrayList<Ability>(numberOfAbilities+1);
		abilities.add(Ability.ATTACK);
		ArrayList<Ability> possibleAbilities = new ArrayList<Ability>(Arrays.asList(Ability.values()));
		possibleAbilities.remove(Ability.ATTACK); //Every unit can ATTACK, so don't generate it as a random ability.
		
		for (int i = 0; i < numberOfAbilities;) {
			if (possibleAbilities.isEmpty()) {
				break; //If the unit has all the available abilities, we're done.
			}
			//EXTRA: Arrange the randomized abilities in a specific order (maybe alphabetically.)
			//ALT: Keeping them in a random order means more unit variation.
			int abilityIndex = MathUtil.randomInteger(0, possibleAbilities.size()-1);
			Ability ability = possibleAbilities.get(abilityIndex);
			possibleAbilities.remove(ability);
			if (!abilities.contains(ability)) {
				abilities.add(ability);
				i++;
			}
		}
		
		return abilities;
	}
	
	/**
	 * Generates and returns the unit's appearance.
	 * 
	 * @return the image that represents the unit.
	 * @throws IOException if part of the image couldn't be loaded.
	 * @throws IllegalArgumentException if an image classpath couldn't be used for loading.
	 */
	public BufferedImage getImage() throws IOException, IllegalArgumentException {
		BufferedImage leftTool = null, rightTool = null;
		//Note that some of this code currently shows up as dead, 
		//but is necessary if reducing ABILITIES_PER_UNIT is to be possible.
		if (abilities.size() > 1) {
			leftTool = ImageLoader.getImage(abilities.get(1).getImagePath());
		}
		if (abilities.size() > 2) {
			rightTool = Util.flipImageHorizontally(ImageLoader.getImage(abilities.get(2).getImagePath()));
		} else {
			rightTool = ImageLoader.getImage(Ability.ATTACK.getImagePath());
		}
        
        BufferedImage torsoImage = ImageLoader.getImage(torsoImagePath);
        
        BufferedImage image = new BufferedImage(Tile.WIDTH - 2*Tile.PADDING, Tile.HEIGHT - 2*Tile.PADDING,
				BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageDrawingGraphics = image.createGraphics();
        //Use a new image's graphics object to draw the partial images on it.
        
        int torsoPositionX = image.getWidth()/2 - torsoImage.getWidth()/2;
        int torsoPositionY = image.getHeight()/2 - torsoImage.getHeight()/2;
        
        imageDrawingGraphics.drawImage(torsoImage, torsoPositionX, torsoPositionY, null);
        if (leftTool != null) {
        	imageDrawingGraphics.drawImage(leftTool, torsoPositionX - leftTool.getWidth(), image.getHeight()/2 - leftTool.getHeight()/2, null);
        }
        imageDrawingGraphics.drawImage(rightTool, torsoPositionX + torsoImage.getWidth(), image.getHeight()/2 - rightTool.getHeight()/2, null);
        return image;
	}
	
	/** 
	 * Starts the unit's turn, giving it the ability to act.
	 * 
	 * @param currentPlayerIndex the index of the player who is starting their turn.
	 */
	public void beginTurn() {
		currentMove = getMove();
		active = true;
	}
	
	/**
	 * Handles any player starting their turn: reduces the remaining duration on this unit's statuses if it's time to do so.
	 * This should be called even when passing over the turn for a defeated player.
	 * 
	 * @param currentPlayerIndex the index of the player who is starting their turn.
	 */
	public void tickDownStatuses(int currentPlayerIndex) {
		// Tick down all statuses caused by the player who is starting their turn.
		for (Status status : statuses) {
			status.tickDown(currentPlayerIndex);
			if (status.getRemainingDuration() <= 0) {
				statuses.remove(status);
			}
		}
	}
	
	/** 
	 * Ends the unit's turn, preventing it from acting during someone else's turn.
	 * This should also be called whenever the unit uses an ability.
	 */
	public void endTurn() {
		active = false;
		currentMove = 0;
	}
	
	/**
	 * Adjusts the unit's health up or down. 
	 * 
	 * @param adjustment the positive or negative change to make to the unit's health.
	 * @param defendable true if this health adjustment is damage that can be mitigated.
	 */
	public void changeHealth(int adjustment, boolean defendable) {
		if (hasStatus(UnitStatus.Type.DEFENDED) && defendable && adjustment < 0) {
			//Only defend health *reduction* that isn't self-inflicted.
			adjustment += Ability.DEFEND.getProperty(Ability.Property.DAMAGE_REDUCTION);
		}
		
		currentHealth += adjustment;
		if (currentHealth > BASE_HEALTH) {
			currentHealth = BASE_HEALTH;
		}
	}
	
	/** @return the unit's maximum movement range, possibly modified by statuses. */
	public int getMove() {
		int currentMove = BASE_MOVE;
		if (hasStatus(UnitStatus.Type.HASTED)) {
			currentMove += Ability.HASTE.getProperty(Ability.Property.MOVE_BONUS);
		}
		if (hasStatus(UnitStatus.Type.SLOWED)) {
			currentMove -= Ability.SLOW.getProperty(Ability.Property.MOVE_PENALTY);
		}
		return currentMove;
	}
	
	/** @return the unit's remaining movement range this turn. */
	public int getCurrentMove() {
		return currentMove;
	}
	
	/**
	 * Changes the unit's remaining move.
	 * Most commonly called when the unit moves.
	 * 
	 * @param move the unit's new remaining move.
	 */
	public void setCurrentMove(int move) {
		this.currentMove = move;
	}
	
	/** @return a list of the unit's abilities. */
	public ArrayList<Ability> getAbilities() {
		return abilities;
	}
	
	/** @return the unit's status effects, mapped to their remaining duration. */
	public HashSet<UnitStatus> getStatuses() {
		return statuses;
	}
	
	/**
	 * Checks whether this unit is affected by a certain status.
	 * 
	 * @param statusType the type of status to check for.
	 * @return true if this unit has a status of the type in question, false otherwise.
	 */
	public boolean hasStatus(UnitStatus.Type statusType) {
		for (UnitStatus status : statuses) {
			if (status.getType() == statusType) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gives the unit a status effect.
	 * 
	 * @param status the status effect to apply to the unit.
	 */
	public void applyStatus(UnitStatus status) {
		statuses.add(status);
	}
	
	/** @return true if the unit is "active", which is to say that the current player can order it to act. */
	public boolean isActive() {
		return active;
	}
	
	/** @return the unit's randomly generated name. */
	public String getName() {
		return name;
	}
	
	/** @return the unit's remaining health. */
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	/**
	 * Changes the unit's remaining hp.
	 * 
	 * @param hp the unit's new remaining hp.
	 */
	public void setCurrentHealth(int hp) {
		this.currentHealth = hp;
	}
	
	/** @return the unit's maximum health. */
	public int getMaximumHealth() {
		//This isn't ever modified at the moment, but the possibility is there.
		return BASE_HEALTH;
	}

	public void setActive(boolean value) {
		this.active = value;
	}
}
