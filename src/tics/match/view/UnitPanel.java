package tics.match.view;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JProgressBar;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Box;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.border.EtchedBorder;

import tics.match.model.Ability;
import tics.match.model.Player;
import tics.match.model.Unit;
import tics.match.model.UnitStatus;
import tics.util.load.PropertiesLoader;

import java.awt.Insets;

import javax.swing.SwingConstants;

@SuppressWarnings("serial")
/** 
 * Displays a single unit's status and abilities, complete with buttons for activating those abilities if appropriate.
 * Hides itself when not displaying unit data.
 * EXTRA: Add hotkeys for each ability and assign them to active ability buttons.
 * 
 * TODO: Maybe some more layout tweaking.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class UnitPanel extends JPanel {
	/** The width of this panel, which should not change. */
	public static final int NORMAL_WIDTH = 219;

	//WINDOW BUILDER COMPONENTS
	//EXTRA: Comment these anyway.
	
	private JLabel nameLabel;
	private JLabel currentHealthLabel;
	private JProgressBar healthBar;
	private JLabel currentMoveLabel;
	private JLabel maximumMoveLabel;
	private JPanel abilitiesPanel;
	private JScrollPane abilityScrollPane;
	private JLabel maximumHealthLabel;
	private JPanel statusPanel;
	private JProgressBar moveBar;
	private JPanel portraitPanel;
	private JPanel informationPanel;
	private JLabel factionLabel;

	/** Creates the unit display panel in a window-builder compatable manner. */
	public UnitPanel() {
		setBorder(new TitledBorder(new LineBorder(new Color(128, 128, 128)), "Unit", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setLayout(new BorderLayout(5, 5));
		
		informationPanel = new JPanel();
		add(informationPanel, BorderLayout.NORTH);
		GridBagLayout gbl_informationPanel = new GridBagLayout();
		gbl_informationPanel.columnWidths = new int[]{140, 48};
		gbl_informationPanel.rowHeights = new int[]{19, 1, 21, 0, 0, 0, 27};
		informationPanel.setLayout(gbl_informationPanel);
		
		nameLabel = new JLabel("Name");
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setPreferredSize(new Dimension(20, 15));
		nameLabel.setMaximumSize(new Dimension(0, 0));
		nameLabel.setMinimumSize(new Dimension(0, 0));
		nameLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.insets = new Insets(5, 0, 2, 5);
		gbc_nameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 0;
		informationPanel.add(nameLabel, gbc_nameLabel);
		
		portraitPanel = new JPanel();
		portraitPanel.setMinimumSize(new Dimension(48, 48));
		portraitPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		portraitPanel.setMaximumSize(new Dimension(48, 48));
		GridBagConstraints portraitPanelConstraints = new GridBagConstraints();
		portraitPanelConstraints.insets = new Insets(0, 0, 10, 0);
		portraitPanelConstraints.gridheight = 3;
		portraitPanelConstraints.fill = GridBagConstraints.BOTH;
		portraitPanelConstraints.gridx = 1;
		portraitPanelConstraints.gridy = 0;
		informationPanel.add(portraitPanel, portraitPanelConstraints);
		
		factionLabel = new JLabel("faction");
		factionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		factionLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		factionLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		factionLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_factionLabel = new GridBagConstraints();
		gbc_factionLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_factionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_factionLabel.gridx = 0;
		gbc_factionLabel.gridy = 1;
		informationPanel.add(factionLabel, gbc_factionLabel);
		
		JPanel healthPanel = new JPanel();
		healthPanel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		FlowLayout fl_healthPanel = (FlowLayout) healthPanel.getLayout();
		fl_healthPanel.setVgap(0);
		fl_healthPanel.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_healthPanel = new GridBagConstraints();
		gbc_healthPanel.insets = new Insets(0, 0, 2, 5);
		gbc_healthPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_healthPanel.anchor = GridBagConstraints.SOUTH;
		gbc_healthPanel.gridx = 0;
		gbc_healthPanel.gridy = 2;
		informationPanel.add(healthPanel, gbc_healthPanel);
		
		healthBar = new JProgressBar();
		GridBagConstraints healthBarConstraints = new GridBagConstraints();
		healthBarConstraints.insets = new Insets(0, 0, 5, 0);
		healthBarConstraints.anchor = GridBagConstraints.NORTH;
		healthBarConstraints.fill = GridBagConstraints.HORIZONTAL;
		healthBarConstraints.gridwidth = 2;
		healthBarConstraints.gridx = 0;
		healthBarConstraints.gridy = 3;
		informationPanel.add(healthBar, healthBarConstraints);
		
		JPanel movePanel = new JPanel();
		FlowLayout fl_movePanel = (FlowLayout) movePanel.getLayout();
		fl_movePanel.setVgap(0);
		fl_movePanel.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_movePanel = new GridBagConstraints();
		gbc_movePanel.insets = new Insets(0, 0, 2, 0);
		gbc_movePanel.anchor = GridBagConstraints.SOUTH;
		gbc_movePanel.gridwidth = 2;
		gbc_movePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_movePanel.gridx = 0;
		gbc_movePanel.gridy = 4;
		informationPanel.add(movePanel, gbc_movePanel);
		
		moveBar = new JProgressBar();
		GridBagConstraints moveBarConstraints = new GridBagConstraints();
		moveBarConstraints.insets = new Insets(0, 0, 5, 0);
		moveBarConstraints.fill = GridBagConstraints.HORIZONTAL;
		moveBarConstraints.gridwidth = 2;
		moveBarConstraints.gridx = 0;
		moveBarConstraints.gridy = 5;
		informationPanel.add(moveBar, moveBarConstraints);
		
		statusPanel = new JPanel();
		statusPanel.setMaximumSize(new Dimension(198, 32767));
		FlowLayout statusPanelLayout = (FlowLayout)statusPanel.getLayout();
		statusPanelLayout.setAlignOnBaseline(true);
		statusPanelLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints statusPanelConstraints = new GridBagConstraints();
		statusPanelConstraints.gridwidth = 2;
		statusPanelConstraints.fill = GridBagConstraints.BOTH;
		statusPanelConstraints.gridx = 0;
		statusPanelConstraints.gridy = 6;
		informationPanel.add(statusPanel, statusPanelConstraints);
		
		JLabel healthStaticLabel = new JLabel("Health");
		healthPanel.add(healthStaticLabel);
		healthStaticLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		currentHealthLabel = new JLabel("-");
		healthPanel.add(currentHealthLabel);
		currentHealthLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel healthSlashStaticLabel = new JLabel("/");
		healthSlashStaticLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		healthPanel.add(healthSlashStaticLabel);
		
		maximumHealthLabel = new JLabel("-");
		maximumHealthLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		healthPanel.add(maximumHealthLabel);
		
		JLabel moveStaticLabel = new JLabel("Move");
		moveStaticLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		movePanel.add(moveStaticLabel);
		
		currentMoveLabel = new JLabel("-");
		currentMoveLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		movePanel.add(currentMoveLabel);
		
		JLabel moveSlashStaticLabel = new JLabel("/");
		moveSlashStaticLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		movePanel.add(moveSlashStaticLabel);
		
		maximumMoveLabel = new JLabel("-");
		maximumMoveLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		movePanel.add(maximumMoveLabel);
		
		Component horizontalStrut = Box.createHorizontalStrut(NORMAL_WIDTH-10);
		//TODO: Figure out why subtracting 10 makes this work...
		//Always keep the panel at least this wide.
		add(horizontalStrut, BorderLayout.SOUTH);
		
		abilityScrollPane = new JScrollPane();
		abilityScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//Ability panels are always the same width, so this panel shouldn't need to scroll horizontally.
		add(abilityScrollPane, BorderLayout.CENTER);
		
		abilitiesPanel = new JPanel();
		abilityScrollPane.setViewportView(abilitiesPanel);
		abilityScrollPane.setBorder(null);
		abilitiesPanel.setLayout(new BoxLayout(abilitiesPanel, BoxLayout.Y_AXIS));
		
		setUnitUIVisible(false); //Don't display anything at the start.
	}

	
	/** 
	 * Displays information on a unit, or stops displaying unit information.
	 * 
	 * @param unit the unit to display information on, or null to clear the display.
	 * @param unitOwner the player who owns the unit - their faction name will also be displayed.
	 * @param actionListener the match that will need to know if a button in the unit UI is pressed.
	 * @param unitActive true if unit is selected by a player and can act.
	 */
	public void setUnit(Unit unit, Player unitOwner, ActionListener actionListener, boolean unitActive) 
	{
		if (unit == null) {
			setUnitUIVisible(false);
			nameLabel.setText(""); //If the panel labelled "unit" is empty, it should be clear that no unit is selected.
		} else {
			setUnitUIVisible(true);
			
			nameLabel.setText(" " + unit.getName()); //Add a space before the name since it looks better.
			if (unitOwner != null) {
				factionLabel.setText(" of the " + unitOwner.getTheme().getFactionName());
			} else {
				factionLabel.setText(" (independant) ");
			}
			
			portraitPanel.removeAll();
			try {
				portraitPanel.add(new JLabel("", new ImageIcon(unit.getImage()), JLabel.CENTER));
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			
			displayHealth(unit);
			displayMove(unit);
			
			statusPanel.removeAll();
			if (unit.getStatuses().isEmpty()) {
				JLabel normalLabel = new JLabel();
				normalLabel.setText("Status: NORMAL");
				statusPanel.add(normalLabel);
			} else {
				for (UnitStatus status : unit.getStatuses()) {
					addStatus(status.getType(), status.getRemainingDuration());
				}
			}
			
			//Make the ability panel exactly big enough for all the abilities.
			abilitiesPanel.removeAll();
			abilitiesPanel.setPreferredSize(new Dimension(AbilityPanel.WIDTH, unit.getAbilities().size()*AbilityPanel.HEIGHT));
			for (Ability ability : unit.getAbilities()) {
				abilitiesPanel.add(new AbilityPanel(ability, actionListener, unitActive));
			}
			
			revalidate(); //Always validate the entire panel when its components change.
			repaint();
		}
	}
	
	/**
	 * Shows a unit's current and maximum move, in text and on a progress bar.
	 * 
	 * @param unit the unit to display health for.
	 */
	private void displayHealth(Unit unit) 
	{
		maximumHealthLabel.setText(String.valueOf(unit.getMaximumHealth()));
		currentHealthLabel.setText(String.valueOf(unit.getCurrentHealth()));
		healthBar.setMaximum(unit.getMaximumHealth());
		healthBar.setValue(unit.getCurrentHealth());
		
		if (unit.getCurrentHealth() < Ability.ATTACK.getProperty(Ability.Property.DAMAGE)) {
			healthBar.setForeground(PropertiesLoader.getColour("very_low_health"));//If a unit will die from one attack, make that obvious.
			currentHealthLabel.setForeground(PropertiesLoader.getColour("very_low_health_text"));
		} else if (unit.getCurrentHealth() < 2*Ability.ATTACK.getProperty(Ability.Property.DAMAGE)) {
			healthBar.setForeground(PropertiesLoader.getColour("low_health")); //Similarly if two attacks will kill it.
			currentHealthLabel.setForeground(PropertiesLoader.getColour("low_health_text"));
		} else {
			healthBar.setForeground(PropertiesLoader.getColour("health"));
			currentHealthLabel.setForeground(PropertiesLoader.getColour("default_text"));
		}		
	}


	/**
	 * Shows a unit's remaining and total move, in text and on a progress bar.
	 * 
	 * @param unit the unit to display move for.
	 */
	private void displayMove(Unit unit) 
	{
		currentMoveLabel.setText(String.valueOf(unit.getCurrentMove()));
		maximumMoveLabel.setText(String.valueOf(unit.getMove()));
		moveBar.setMaximum(unit.getMove());
		moveBar.setValue(unit.getCurrentMove());
		
		if (unit.getCurrentMove() == unit.getMove()) {
			if (unit.getMove() > Unit.BASE_MOVE) {
				moveBar.setForeground(PropertiesLoader.getColour("buff"));
			} else if (unit.getMove() < Unit.BASE_MOVE){
				moveBar.setForeground(PropertiesLoader.getColour("debuff"));
			} else {
				moveBar.setForeground(PropertiesLoader.getColour("full_move"));
			}
		} else if (unit.getCurrentMove() < unit.getMove()) {
			moveBar.setForeground(PropertiesLoader.getColour("partial_move")); //The unit has finished *part* of its turn.
		}
	}
	
	/** 
	 * Adds text describing a status effect to the status panel.
	 * 
	 * @param status the type of status effect to describe.
	 * @param remainingDuration the turns left on the status, which will also be displayed.
	 */
	private void addStatus(UnitStatus.Type status, int remainingDuration) 
	{
		JLabel statusLabel = new JLabel();
		statusLabel.setText(status.toString()+" ("+remainingDuration+")");
		statusLabel.setToolTipText(status.getDescription());
		
		if (status.isPositive()) {
			statusLabel.setForeground(PropertiesLoader.getColour("buff"));
		} else {
			statusLabel.setForeground(PropertiesLoader.getColour("debuff"));
		}
		
		statusPanel.add(statusLabel);
	}
	
	/**
	 * Displays or stops displaying the unit status UI.
	 * It shouldn't be displayed if no unit is selected.
	 * 
	 * @param visible true to display it, false to stop displaying it.
	 */
	private void setUnitUIVisible(boolean visible) {
		informationPanel.setVisible(visible);
		abilityScrollPane.setVisible(visible);
	}
}
