package tics.match.view;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import tics.match.model.Tile;
import tics.match.model.TileStatus;

import java.awt.Color;

/** 
 * A panel that displays non-unit data for a tile. 
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class TerrainPanel extends JPanel {
	/** The label that displays the terrain information. */
	private JLabel label;
	
	/** Creates the terrain panel in a Window Builder-compatable fashion. */
	public TerrainPanel() {
		setMinimumSize(new Dimension(229, 80)); //TODO: Maybe make these sizes non-hardcoded.
		setBorder(new TitledBorder(new LineBorder(new Color(128, 128, 128)), "Terrain", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(null);
		
		label = new JLabel("No Selection");
		label.setBounds(10, 15, 230, 25);
		label.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(label);

	}
	
	/** 
	 * Displays the terrain data for a tile.
	 * 
	 * @param tile the tile to show data for, or null to show no data.
     */
	public void displayTerrainData(Tile tile) {
		if (tile == null) {
			label.setText("None");
		} else if (tile.hasStatus(TileStatus.Type.BLOCKED)) {
			label.setText(TileStatus.Type.BLOCKED.toString() + "(" + tile.getStatusDuration(TileStatus.Type.BLOCKED) + ")");
		} else {
			label.setText("Normal terrain");
		}
	}

}
