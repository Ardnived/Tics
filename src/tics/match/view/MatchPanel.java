package tics.match.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tics.match.model.Board;
import tics.match.model.Player;
import tics.match.model.Tile;
import tics.match.model.Unit;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/** 
 * The panel that a match is displayed on. 
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class MatchPanel extends JPanel {
	//EXTRA: Replace the static dimensions by a resolution system.
	//EXTRA: Make it look less awkward when the game board is smaller than the panel it's in.
	public static final int WIDTH = 900;
	public static final int HEIGHT = 700;
	
	/** The panel that displays the global information for the current match. */
	private GameInfoPanel gameInfoPanel;
	
	/** The panel that displays data for a unit. */
	private UnitPanel unitPanel;
	/** The pane used to scroll around the game board. */
	private JScrollPane boardPane;
	/** The panel that displays non-unit tile data. */
	private TerrainPanel terrainPanel;
	
	/** The class that handles button presses on this panel. */
	private ActionListener actionListener;
	
	/**
	 * Creates a MatchPanel for a new match. 
	 * 
	 * @param actionListener the class that needs to know about button presses.
	 * @param the class that needs to know about hotkey presses.
	 * @param board the board for the displayed match.
	 */
	public MatchPanel(ActionListener actionListener, KeyListener keyListener, Board board) {
		this.actionListener = actionListener;
		setFocusable(true); //Allow this component to receive focus, which is necessary for it to send keyboard events.
		addKeyListener(keyListener);
		
		setLayout(new BorderLayout(0, 0));
		super.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		boardPane = new JScrollPane();
		boardPane.setViewportView(board);
		add(boardPane, BorderLayout.CENTER);
		
		gameInfoPanel = new GameInfoPanel(actionListener);
		add(gameInfoPanel, BorderLayout.NORTH);
		
		JPanel tilePanel = new JPanel();
		add(tilePanel, BorderLayout.EAST);
		GridBagLayout gridBagLayout = new GridBagLayout(); //This layout is complex, but seems necessary for the panels to be sized properly.
		gridBagLayout.rowHeights = new int[]{15, 659, 0};
		tilePanel.setLayout(gridBagLayout);
		
		terrainPanel = new TerrainPanel();
		terrainPanel.setPreferredSize(new Dimension(UnitPanel.NORMAL_WIDTH, 15));
		GridBagConstraints gbc_terrainPanel = new GridBagConstraints();
		gbc_terrainPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_terrainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_terrainPanel.gridx = 0;
		gbc_terrainPanel.gridy = 0;
		tilePanel.add(terrainPanel, gbc_terrainPanel);
		
		unitPanel = new UnitPanel();
		GridBagConstraints gbc_unitPanel = new GridBagConstraints();
		gbc_unitPanel.fill = GridBagConstraints.VERTICAL;
		gbc_unitPanel.gridx = 0;
		gbc_unitPanel.gridy = 1;
		tilePanel.add(unitPanel, gbc_unitPanel);
	}
	
	/** @return the game information panel contained by this match panel. */
	public GameInfoPanel getMatchInfoPanel() {
		return gameInfoPanel;
	}
	
	/**
	 * Shows data for a tile. 
	 * Also shows data for the currently selected unit if the tile given has no unit on it and clearing unit data isn't forced.
	 * 
	 * @param tile the tile being hovered over or clicked on.
	 * @param selectedTile the tile selected in the current match, which might need to be displayed.
	 * @param tileOwner the player, if any, who owns the unit on the tile being clicked on or hovered over.
	 * @param selectedTileOwner the player, if any, who owns the unit on the selected tile.
	 * @param clearUnit true if the unit display panel can be cleared if there's no unit on the tile.
	 */
	public void setTile(Tile tile, Tile selectedTile, Player tileOwner, Player selectedTileOwner, boolean clearUnit) {
		Unit displayedUnit = null;
		Player displayedUnitOwner = null;
		if (tile != null) {
			if (tile.hasUnit() || clearUnit) {
				displayedUnit = tile.getUnit();
				displayedUnitOwner = tileOwner;
			}
		}
		//EXTRA: Prevent the UnitPanel from redrawing itself twice on every single tile mouseover.
		if (selectedTile != null && displayedUnit == null) { //Hovered-over units take precedence over selected units.
			if (selectedTile.hasUnit()) {
				displayedUnit = selectedTile.getUnit();
				displayedUnitOwner = selectedTileOwner;
			}
		}
		if (displayedUnit == null) {
			unitPanel.setUnit(displayedUnit, displayedUnitOwner, actionListener, true);
		} else if (displayedUnit.isActive()) {
			unitPanel.setUnit(displayedUnit, displayedUnitOwner, actionListener, true);
		} else {
			unitPanel.setUnit(displayedUnit, displayedUnitOwner, actionListener, false);
		}
		
		terrainPanel.displayTerrainData(tile);
	}

	/** Handles the game the panel is displaying ending. */
	public void endMatch() {
		gameInfoPanel.endMatch();
	}
}
