package tics.match.model;

import tics.util.MathUtil;

/**
 * A combined naming convention and visual style for a player's units.
 * 
 * It randomly generates names based on some simple rules and stores possible unit appearances.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public enum PlayerTheme {
	//TODO: Prevent duplicate names.
	BAL_ROG(
		/*Faction Name*/ "Bal-Rogs",
		/*First Syllable Letters*/ new String[] { "BDG", "ao", "lf" }, 
		/*Second Syllable Letters*/ new String[] { "RG", "ou", "gk" }, 
		/*Asset Folder*/ "bal_rog/",
		/*Torso Filenames*/ new String[]{"boulder.png", "cliff.png", "rock.png"}
	),
	IRI_ENK(
		/*Faction Name*/ "Iri-Enks",
		/*First Syllable Letters*/ new String[] { "IAU", "rk", "io" }, 
		/*Second Syllable Letters*/ new String[] { "AEIOU", "nms", "k" }, 
		/*Asset Folder*/ "iri_enk/",
		/*Torso Filenames*/ new String[]{"acolyte.png", "adept.png", "magus.png"}
	),
	SUR_REK(
		/*Faction Name*/ "Sur-Reks",
		/*First Syllable Letters*/ new String[] { "SAL", "ueo", "rnh" }, 
		/*Second Syllable Letters*/ new String[] { "RHG", "ei", "kn" }, 
		/*Asset Folder*/ "sur_rek/",
		/*Torso Filenames*/ new String[]{"contop.png", "tritop.png", "horntop.png"}
	),
	GAU_ASH(
		/*Faction Name*/ "Gau-Ashe",
		/*First Syllable Letters*/ new String[] { "GKR", "a", "aiou" }, 
		/*Second Syllable Letters*/ new String[] { "AUI", "sc", "h" }, 
		/*Asset Folder*/ "gau_ash/",
		/*Torso Filenames*/ new String[]{"pillar.png", "totem.png", "tower.png"}
	),
	FAL_THA(
		/*Faction Name*/ "Fal-Thau",
		/*First Syllable Letters*/ new String[] { "FYZ", "aeiou", "lm" }, 
		/*Second Syllable Letters*/ new String[] { "SZT", "h", "aeiou" }, 
		/*Asset Folder*/ "fal_tha/",
		/*Torso Filenames*/ new String[]{"feel.png", "know.png", "think.png"}
	),
	TIK_TOK(
		/*Faction Name*/ "Tik-Toks",
		/*First Syllable Letters*/ new String[] { "TDL", "aie", "sk" }, 
		/*Second Syllable Letters*/ new String[] { "TY", "eoi", "k" }, 
		/*Asset Folder*/ "tik_tok/",
		/*Torso Filenames*/ new String[]{"brute.png", "grunt.png", "pip.png"}
	),
	MAL_POK(
		/*Faction Name*/ "Mal-Poks",
		/*First Syllable Letters*/ new String[] { "MJG", "aou", "lkr" }, 
		/*Second Syllable Letters*/ new String[] { "PT", "oeua", "kd" }, 
		/*Asset Folder*/ "mal_pok/",
		/*Torso Filenames*/ new String[]{"mystic.png", "squirt.png", "squid.png"}
	);
	
	/** The classpath to the package that holds each theme's torso images folder. */
	private static final String TORSO_IMAGES_CLASSPATH = "/tics/torso/";
	
	/** The (plural) faction name for this theme. This can't be too long, or the UI starts jittering when it displays. */
	private String factionName;
	//EXTRA: Make the UI never jitter.
	
	private String[] firstWordCharPool;
	private String[] secondWordCharPool; //EXTRA: Make this system able to handle any number of words for names.
	
	/** The identifiers corresponding to the torso images used  */
	private String[] torsoImagePaths;
	
	/**
	 * Constructor
	 * 
	 * @param factionName the name of the faction that this theme represents.
	 * @param firstWordCharPool the pool of characters that can be used to create the first word in a unit name. See PlayerTheme.generateWord for more detail.
	 * @param secondWordCharPool the pool of characters that can be used to create the second word in a unit name. See PlayerTheme.generateWord for more detail.
	 * @param torsoImageFolder the folder that torso images can be found in for this theme.
	 * @param torsoImageFiles the filenames for each possible torso.
	 */
	private PlayerTheme(String factionName, String[] firstWordCharPool, String[] secondWordCharPool, String torsoImageFolder, String[] torsoImageFiles) {
		// Assign variables
		this.factionName = factionName;
		this.firstWordCharPool = firstWordCharPool;
		this.secondWordCharPool = secondWordCharPool;
		
		// Create filepaths for the torso images.
		this.torsoImagePaths = new String[torsoImageFiles.length];
		for (int i = 0; i < torsoImageFiles.length; i++) { //For each image file in the folder
			// Construct a class path string.
			this.torsoImagePaths[i] = TORSO_IMAGES_CLASSPATH + torsoImageFolder + torsoImageFiles[i];
		}
	}
	
	/** @return a name based on the theme's allowed character pool. Intended to be the name of a unit of that faction. */
	public String generateName() {
		return generateWord(firstWordCharPool) + "-" + generateWord(secondWordCharPool);
	} //EXTRA: Disallow the faction name as a unit name.
	
	/** 
	 * Generates a word from a given character pool.
	 * For every String in the character, the function will choose 1 letter randomly, 
	 * concatenate all those letters together to form the new word.
	 * 
	 * @param wordCharPool a list of characters arranged into String objects that can be used to generate the word.
	 * @return a String with the length of wordCharPool, and letters randomly chosen from that pool.
	 */
	private String generateWord(String[] wordCharPool) {
		String word = ""; // initialize the word
		
		// Loop through every set of characters in the character pool.
		for (String chars : wordCharPool) {
			char[] charPool = chars.toCharArray(); // Convert the string to an array of characters.
			int charIndex = MathUtil.randomInteger(charPool.length-1); //Pick a random character from the array.
			word += charPool[charIndex]; // Add the chosen character to the word.
		}
		
		return word; // Return the word we've generated.
	}
	
	//TODO: Maybe assign these nonrandomly, as is done with the themes themselves.
	public String getRandomImagePath() {
		return torsoImagePaths[MathUtil.randomInteger(torsoImagePaths.length-1)];
		//EXTRA: Ideally figure out why this is giving a NullPointer of all things when a theme folder doesn't exist.
	}
	
	/** @return the theme for a player of a given index number. Themes are handed out in order, so every index always gets the same theme. */
	public static PlayerTheme getThemeForIndex(int index) {
		int modifiedIndex = index % PlayerTheme.values().length;
		return PlayerTheme.values()[modifiedIndex];
	}
	
	/** @return the name of the faction this theme represents. */
	public String getFactionName() {
		return this.factionName;
	}
}
