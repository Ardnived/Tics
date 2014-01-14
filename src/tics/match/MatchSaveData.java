package tics.match;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import tics.match.model.Board;
import tics.match.model.Player;
import tics.match.model.Unit;

/**
 * Contains the data needed to restart a match mid-game.
 * This can also be used to save the start of an interesting match.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class MatchSaveData implements Serializable {
	/** A randomly generated value, used by Java to identify saved instances of this class. */
	private static final long serialVersionUID = 3227064219798143659L;

	/** The relative path to the save file directory. */
	public static final String SAVE_FILE_FOLDER = "saves/";
	//This should be a file path and not a classpath, 
	//so that players can manipulate the save files directly if they need to.
	
	/** The extension for files of this type. */
	public static final String SAVE_FILE_EXTENSION = "tsv";
	//TSV = Tics SaVe
	
	/** The grid of units for the match, without their underlying tiles. */
	private Unit[][] unitPositions;
	//private Terrain[][] terrainPositions;
	
	/** The number of rounds passed (plus one) when the game was saved. */
	private int roundNumber;
	
	/** 
	 * The collections of units each player has to work with.
	 * This is an ordered collection of unordered collections of units.
	 * This is not a HashSet<Unit>[] because those seem to cause issues.
	 */
	private Player[] players;
	/** The index in the array of the player who had the turn when the match was saved. */
	private int currentPlayerIndex;
	/** The index in the array of the player who moved first in the saved match. */
	private int firstPlayerIndex;

	/**
	 * Constructs the save data for a match.
	 * 
	 * @param match the match to construct save data for.
	 */
	public MatchSaveData(Match match) {
		Board board = match.getBoard();
		unitPositions = new Unit[board.getGridWidth()][board.getGridHeight()];
		for (int i = 0; i < board.getGridWidth(); i++) {
			for (int j = 0; j < board.getGridHeight(); j++) {
				if (board.getTile(i, j).hasUnit()) {
					unitPositions[i][j] = board.getTile(i, j).getUnit();
				}
				//TODO: Save terrain here, if applicable.
			}
		}
		
		players = match.getPlayers();
		currentPlayerIndex = match.getCurrentPlayerIndex();
		roundNumber = match.getCurrentRound();
		firstPlayerIndex = match.getFirstPlayerIndex();
	}
	
	/** 
	 * Saves this match data to a specific file, without prompting the user.
	 * 
	 * @throws IOException if the file couldn't be saved.
	 */
	public void quicksave() throws IOException {
		File saveFolder = new File(SAVE_FILE_FOLDER);
		if (!saveFolder.isDirectory()) { //If the save folder doesn't exist, create it when quicksaving.
			saveFolder.mkdir(); //mkdir = make directory
		}
		//EXTRA: Allow multiple, maybe time-stamped, quicksaves.
		save(SAVE_FILE_FOLDER + "quicksave.tcs");
	}
	
	/** 
	 * Saves this match data to a given file.
	 * 
	 * @param filePath the path to the file to save to.
	 * @throws IOException if the file couldn't be saved.
	 */
	public void save(String filePath) throws IOException {
		ObjectOutputStream objectOutputStream = 
				new ObjectOutputStream(new FileOutputStream(filePath));
		objectOutputStream.writeObject(this);
		
		objectOutputStream.close();
	}
	
	/**
	 * Loads a match from data previously saved to a file.
	 * 
	 * @param filePath the path to the match data file.
	 * @return the match corresponding to the loaded data.
	 * @throws IOException if the file couldn't be loaded or if it didn't contain a Match.
	 * @throws ClassNotFoundException if no Match could be loaded because the MatchSaveData class is unavailable.
	 */
	public static MatchSaveData load(String filePath) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));
		Object loadedObject = objectInputStream.readObject();
		
		objectInputStream.close();
		
		if (loadedObject instanceof MatchSaveData) {
			return (MatchSaveData) loadedObject;
		} else {
			throw new IOException("The loaded file doesn't seem to contain Tics game data.");
		}
		
	}
	
	/** @return the grid of unit positions for the match. */
	public Unit[][] getUnitPositions() {
		return unitPositions;
	}

	/** @return the players in the match. */
	public Player[] getPlayers() {
		return players;
	}

	/** @return the array index of the player who had the turn when the match was saved. */
	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}
	
	/** @return the number of rounds passed in the match. */
	public int getRoundNumber() {
		return roundNumber;
	}
	
	/** @return the number of the player who moved first in the match. */
	public int getFirstPlayerIndex() {
		return firstPlayerIndex;
	}
}
