package tics.util.load;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import tics.match.Match;
import tics.match.MatchCommandListener;
import tics.match.MatchSaveData;
import tics.match.model.Ability;
import tics.match.model.Board;
import tics.match.model.Player;
import tics.match.model.Tile;
import tics.match.model.TileStatus;
import tics.match.model.Unit;
import tics.match.model.UnitStatus;
import tics.match.view.MatchPanel;
import tics.util.MathUtil;

/**
 * Loads a scenario from json.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class MatchLoader {
	
	public static Match loadFromSave(String filePath) {
		/*
		MatchSaveData saveData = MatchSaveData.load(filePath);
		Match match = new Match(saveData.getPlayers(), saveData)
		
		MatchCommandListener commandListener = new MatchCommandListener(match);
		Unit[][] unitPositions = saveData.getUnitPositions();
		
		players = saveData.getPlayers();
		
		currentPlayerIndex = saveData.getCurrentPlayerIndex();
		currentRound = saveData.getRoundNumber();
		firstPlayerIndex = saveData.getFirstPlayerIndex();
		
		board = new Board(unitPositions.length, unitPositions[0].length, commandListener);
		panel = new MatchPanel(commandListener, commandListener, board);
		panel.getMatchInfoPanel().changeTurn(getCurrentPlayer(), currentRound);
		
		for (int i = 0; i < unitPositions.length; i++) {
			for (int j = 0; j < unitPositions[0].length; j++) {
				if (unitPositions[i][j] != null) {
					board.getTile(i, j).setUnit(unitPositions[i][j]);
				}
				//TODO: Load terrain here.
			}
		}
		*/
		
		// TODO: Implement this.
		return null;
	}
	
	/**
	 * This function makes some assumptions about the format of the json that it will read.
	 * It does not handle the exceptions that will be thrown if you don't follow the format.
	 * 
	 * @param scenario
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Match loadFromJSON(String filePath) throws FileNotFoundException {
		Map<String, Object> data = JSONLoader.get(filePath);
		
		System.out.println("Loading "+data.get("title")+", by "+data.get("author"));
		System.out.println(data.get("description"));
		
		int players = Integer.decode((String) data.get("players"));
		int width = Integer.decode((String) data.get("width"));
		int height = Integer.decode((String) data.get("height"));
		int abilityCount;
		
		Match match = new Match(players, width, height, 0, 0);
		
		if (data.containsKey("abilities")) {
			abilityCount = Integer.decode((String) data.get("abilities"));
		} else {
			abilityCount = 0;
		}
		
		if (data.containsKey("currentRound")) {
			match.setCurrentRound(Integer.decode((String) data.get("currentRound")));
		}
		
		if (data.containsKey("currentPlayer")) {
			match.setCurrentPlayerIndex(Integer.decode((String) data.get("currentPlayer")));
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, Object>> setup = (ArrayList<Map<String, Object>>) data.get("setup");
		
		for (Map<String, Object> instruction : setup) {
			int x = Integer.decode((String) instruction.get("x"));
			int y = Integer.decode((String) instruction.get("y"));
			Tile tile = match.getBoard().getTile(x, y);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> unitInstruction = (Map<String, Object>) instruction.get("unit");
			if (unitInstruction != null) {
				int owner = Integer.decode((String) unitInstruction.get("owner"));
				Player player = match.getPlayers()[owner-1];
				
				@SuppressWarnings("unchecked")
				ArrayList<String> abilityList = (ArrayList<String>) unitInstruction.get("abilities");
				ArrayList<Ability> abilities = new ArrayList<Ability>();
				ArrayList<Ability> possibleAbilities = new ArrayList<Ability>(Arrays.asList(Ability.values()));
				
				abilities.add(Ability.ATTACK);
				possibleAbilities.remove(Ability.ATTACK); //Every unit can ATTACK, so don't generate it as a random ability.
				
				if (abilityList != null) {
					for (String abilityString : abilityList) {
						Ability ability = Ability.valueOf(abilityString);
						abilities.add(ability);
						possibleAbilities.remove(ability);
					}
				}
				
				for (int i = abilities.size(); i <= abilityCount;) {
					int abilityIndex = MathUtil.randomInteger(0, possibleAbilities.size()-1);
					Ability ability = possibleAbilities.get(abilityIndex);
					possibleAbilities.remove(ability);
					
					abilities.add(ability);
					i++;
				}
				
				Unit unit = new Unit(player.getTheme(), abilities);
				player.getUnits().add(unit);
				tile.setUnit(unit);
				
				@SuppressWarnings("unchecked")
				Map<String, String> statusInstruction = (Map<String, String>) unitInstruction.get("status");
				if (statusInstruction != null) {
					for (Map.Entry<String, String> entry : statusInstruction.entrySet()) {
						UnitStatus.Type type = UnitStatus.Type.valueOf(entry.getKey());
						int duration = Integer.decode(entry.getValue());
						
						unit.applyStatus(new UnitStatus(type, duration, 0)); // TODO: Change this 0 to whatever it is supposed to be.
					}
				}

				if (data.containsKey("hp")) {
					unit.setCurrentHealth(Integer.decode((String) data.get("hp")));
				}

				if (data.containsKey("move")) {
					unit.setCurrentMove(Integer.decode((String) data.get("move")));
				}

				if (data.containsKey("active")) {
					unit.setActive(Boolean.parseBoolean((String) data.get("active")));
				}
			}

			@SuppressWarnings("unchecked")
			Map<String, String> statusInstruction = (Map<String, String>) instruction.get("status");
			if (statusInstruction != null) {
				for (Map.Entry<String, String> entry : statusInstruction.entrySet()) {
					TileStatus.Type type = TileStatus.Type.valueOf(entry.getKey());
					int duration = Integer.decode(entry.getValue());
					
					tile.applyStatus(new TileStatus(type, duration, 0));
				}
			}
		}
		
		return match;
	}
	
	public static Match generateFromPreset(String filePath) {
		// TODO: Implement this.
		return null;
	}
	
	public static Match generate(int width, int height, int players, int units, int abilityCount) {
		return new Match(width, height, players, units, abilityCount);
	}
	
}
