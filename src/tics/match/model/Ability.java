package tics.match.model;

import java.util.EnumMap;

import tics.util.TargetType;
import tics.util.Util;

/**
 * An action. Each turn, each unit can use one ability after moving.
 * TODO: Make some basic balance decisions about abilities before testing.
 * TODO: More commenting.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public enum Ability {
	//Note that Java enums are always serializable (and efficiently so), so this class doesn't need to implement that interface.
	
	// UNIVERSAL Ability
	
	/** Deals standard damage to an adjacent enemy. */
	ATTACK(TargetType.ENEMY, TargetType.NONE, "Deals damage to an adjacent enemy.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE, 4);
			properties.put(Property.RANGE,  1);
		}
		
		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(-getProperty(Property.DAMAGE), true);
		}
	},
	
	
	// OFFENSIVE Abilities
	
	/** Deals reduced damage to an enemy at range. */
	SHOOT(TargetType.ENEMY, TargetType.ANY_OBSTACLE, "Deals low damage to an enemy at a range. Cannot fire through units.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE, 2);
			properties.put(Property.RANGE,  2);
		}
		
		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(-getProperty(Property.DAMAGE), true);
		}
	},
	
	/** Deals greatly reduced damage to an enemy at long range. */
	SNIPE(TargetType.ENEMY, TargetType.NONE, "Deals very low damage to an enemy at a long range. Can fire past enemies.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE, 1);
			properties.put(Property.RANGE,  4);
		}
		
		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(-getProperty(Property.DAMAGE), true);
		}
	},
	
	/** Deals increased damage to an adjacent enemy and some damage to the user. */
	POUND(TargetType.ENEMY, TargetType.NONE, "Deals high damage to an adjacent enemy, at the cost of some of the user's hit points.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE,  6);
			properties.put(Property.RANGE,   1);
			properties.put(Property.HP_COST, 2);
		}
		
		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(-getProperty(Property.DAMAGE), true);
			origin.getUnit().changeHealth(-getProperty(Property.HP_COST), false);
		}
	},
	
	/** Deals greatly reduced damage to all enemies in an area. */
	BURST(TargetType.ANY, TargetType.NONE, "Deals damage to every unit in a large area at a range. Can fire past enemies. Can damage allied units.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE,        1);
			properties.put(Property.RANGE,         3);
			properties.put(Property.EFFECT_RADIUS, 2);
		}
		
		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			if (target.hasUnit()) {
				target.getUnit().changeHealth(-getProperty(Property.DAMAGE), true);
			}
		}
	},
	
	// DEFENSIVE Abilities
	
	/** Heals the user for an increased amount of health. */
	REGENERATE(TargetType.ALLY, TargetType.NONE, "Restores some of the user's hit points.") {
		@Override
		public void initProperties() {
			properties.put(Property.HP_RESTORED, 1);
			properties.put(Property.RANGE,       0);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(getProperty(Property.HP_RESTORED), false);
		}
	},
	
	/** Temporarily reduces incoming damage. */
	DEFEND(TargetType.ALLY, TargetType.NONE, "Temporarily reduces damage taken by the user. Does not reduce self-inflicted damage.") {
		@Override
		public void initProperties() {
			properties.put(Property.DAMAGE_REDUCTION, 1);
			properties.put(Property.DURATION,         1);
			properties.put(Property.RANGE,            0);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().applyStatus(new UnitStatus(UnitStatus.Type.DEFENDED, getProperty(Property.DURATION), currentPlayerIndex));
		}
	},
	
	// UTILITY abilities
	
	/** Heals a nearby ally for a standard amount of health. */
	HEAL(TargetType.ALLY, TargetType.NONE, "Restores some of a nearby ally's hit points. Cannot heal the user.") {
		@Override
		public void initProperties() {
			properties.put(Property.HP_RESTORED, 2);
			properties.put(Property.RANGE,       1);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().changeHealth(getProperty(Property.HP_RESTORED), false);
		}
	},
	
	/** Temporarily increases a nearby ally's movement speed. */
	HASTE(TargetType.ALLY, TargetType.NONE, "Temporarily increases the movement range of a nearby ally.") {
		@Override
		public void initProperties() {
			properties.put(Property.MOVE_BONUS, 2);
			properties.put(Property.RANGE,      2);
			properties.put(Property.DURATION,   1);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().applyStatus(new UnitStatus(UnitStatus.Type.HASTED, getProperty(Property.DURATION), currentPlayerIndex));
		}
	},
	
	/** Temporarily decreases a nearby enemy's movement speed. */
	SLOW(TargetType.ENEMY, TargetType.NONE, "Temporarily decreases the movement range of a nearby enemy.") {
		@Override
		public void initProperties() {
			properties.put(Property.MOVE_PENALTY, 2);
			properties.put(Property.RANGE,        2);
			properties.put(Property.DURATION,     1);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.getUnit().applyStatus(new UnitStatus(UnitStatus.Type.SLOWED, getProperty(Property.DURATION), currentPlayerIndex));
		}
	},
	
	/** Temporarily blocks off a nearby square. */
	BARRIER(TargetType.EMPTY, TargetType.NONE, "Temporarily makes a selected tile impassable. ") {
		@Override
		public void initProperties() {
			properties.put(Property.RANGE,    1);
			properties.put(Property.DURATION, 1);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			target.applyStatus(new TileStatus(TileStatus.Type.BLOCKED, getProperty(Property.DURATION), currentPlayerIndex));
		}
	},
	
	/** Moves the user a greatly increased distance, ignoring obstacles. */
	TELEPORT(TargetType.EMPTY, TargetType.NONE, "Moves the user anywhere in a large range, ignoring obstacles.") {
		@Override
		public void initProperties() {
			properties.put(Property.RANGE, 6);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			Util.moveUnit(origin, target);
		}
	},
	
	/** Moves the user an increased distance */ //TODO: Maybe reword this.
	SPRINT(TargetType.EMPTY, TargetType.ANY_MOVEMENT_BLOCKER, "Moves the user again.", true) {
		@Override
		public void initProperties() {
			properties.put(Property.RANGE, 3);
		}

		@Override
		public void affect(Tile origin, Tile target, int currentPlayerIndex) {
			Util.moveUnit(origin, target);
		}
	};
	
	/** The path to the package containing all the ability images. */
	private static final String IMAGE_CLASSPATH = "/tics/tool/";
	
	/** The type of image file used for ability pictures. */
	public static final String IMAGE_EXTENSION = ".png";
	
	/* ========================================== INSTANCE CODE ========================================== */
	/** The type of tile that this ability can target. */
	private TargetType targetType;
	/** The type(s) of tile that will block an ability's range path. */
	private TargetType blockedBy;
	/** Whether the targeting for this ability can wrap around obstacles. */
	private boolean wrappable;
	/** A description of the ability. */
	private String description;
	/** Stores a set of integer properties about this ability. */
	protected EnumMap<Ability.Property, Integer> properties;
	/** The image of this ability that is places on units. */
	private String imagePath;
	
	/** 
	 * Constructs abilities.
	 * 
	 * @param target the type of tile the ability can target.
	 * @param blockedBy the type of tile the ability can't fire through.
	 * @param range the range at which the ability can target.
	 * @param wrappable whether the targeting for this ability can wrap around obstacles.
	 */
	private Ability(TargetType target, TargetType blockedBy, String description) {
		this(target, blockedBy, description, false);
	}
	
	/** 
	 * Constructs abilities.
	 * 
	 * @param target the type of tile the ability can target.
	 * @param blockedBy the type of tile the ability can't fire through.
	 * @param range the range at which the ability can target.
	 */
	private Ability(TargetType target, TargetType blockedBy, String description, boolean wrappable) {
		this.targetType = target;
		this.blockedBy = blockedBy;
		this.description = description;
		this.properties = new EnumMap<Ability.Property, Integer>(Ability.Property.class);
		this.wrappable = wrappable;
		initProperties();

		//TODO: Make these theme-specific for colouring and/or style purposes, possibly moving them to PlayerTheme in the process.
		//TODO: Use variables rather than ability names for this (but do the above first.)
		imagePath = IMAGE_CLASSPATH + this.name().toLowerCase() + IMAGE_EXTENSION;
	}
	
	/**
	 * This method should be overridden in order to 
	 */
	public abstract void initProperties();
	
	/**
	 * Applies this ability's affect to the given square, or the unit on it.
	 * 
	 * @param origin the tile from which this ability originated.
	 * In most cases this actually means the tile containing the unit from which this ability originated.
	 * @param targets the group of tiles that the ability is to affect.
	 * @param the index into the array of players of the active player when the ability was used.
	 */
	public abstract void affect(Tile origin, Tile target, int currentPlayerIndex);
	//TODO: maybe move some AOE code into this class?
	
	/**
	 * Retrieves a stat for this ability.
	 * 
	 * @param property the type of stat to return.
	 * @return the numerical value for the property in question, or 0 if there is no such property.
	 */
	public int getProperty(Ability.Property property) {
		if (properties.containsKey(property)) {
			return properties.get(property);
		} else {
			return property.getDefaultValue();
		}
	}
	
	/** @return the type of tile this ability targets. */
	public TargetType getTargetType() {
		return targetType;
	}
	
	/** @return the type of tile this ability can't target through. */
	public TargetType getBlockingType() {
		return blockedBy;
	}
	
	/** @return the path to this ability's image representation. */
	public String getImagePath() {
		return imagePath;
	}
	
	public boolean canWrap() {
		return wrappable;
	}
	
	/** @return a short description of this ability, to be provided to the players. */
	public String getDescription() {
		String description = this.description;
		for (Property property : properties.keySet()) {
			description += "<br />"+property.toString()+": "+getProperty(property);
		}
		return description;
	}
	
	/** A "stat" for an ability which specifies how it works numerically. */
	public enum Property {
		/**
	     * The range at which this ability can be targeted.
	     * Range is calculated by tracing an orthogonal path.
	     * Abilities with range 0 can only target the user or the user's square.
	     */
		RANGE("Range", 1),
		MINIMUM_RANGE("Minimum Range", 0),
		/** 
		 * The duration of this ability's effects.
		 * Duration is counted from TODO: finish this sentence.
		 */
		DURATION("Duration"),
		/** Damage dealt by this ability to its target. */
		DAMAGE("Damage"),
		HP_RESTORED("Healing"),
		HP_COST("Health Cost"),
		EFFECT_RADIUS("Effect Radius", 0),
		MOVE_BONUS("Move Bonus"),
		MOVE_PENALTY("Move Penalty"),
		DAMAGE_REDUCTION("Damage Reduction");
		
		int defaultValue;
		String label;
		
		Property(String label) {
			this(label, 0);
		}
		
		Property(String label, int defaultValue) {
			this.defaultValue = defaultValue;
			this.label = label;
		}
		
		public int getDefaultValue() {
			return defaultValue;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
}
