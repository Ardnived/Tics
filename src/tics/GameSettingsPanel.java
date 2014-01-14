package tics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tics.match.Match;
import tics.util.load.ImageLoader;
import tics.util.load.JSONLoader;
import tics.util.load.PropertiesLoader;
import tics.util.load.MatchLoader;

@SuppressWarnings("serial")
public class GameSettingsPanel extends JPanel {
	
	public static final String SCENARIO_FOLDER = "scenarios/";
	
	private String[] scenarioFiles;
	private String[] scenarioNames;
	private String[] scenarioDescriptions;
	
	protected JPanel settingsPanel;
	
	protected JPanel generatorPanel;
	protected JPanel scenarioPanel;
	
	protected JComboBox gameTypeBox;
	protected JComboBox presetBox;
	protected JSlider unitsSlider;
	protected JSlider playersSlider;
	protected JSlider abilityCountSlider;
	protected JComboBox abilitiesBox;
	protected JSlider widthSlider;
	protected JSlider heightSlider;
	protected JComboBox placementBox;
	
	protected JTextArea scenarioDescription;
	protected JComboBox scenarioBox;
	
	protected JButton start;
	protected JButton cancel; // Should only be visible if there is an ongoing match.
	
	public GameSettingsPanel() {
		try {
			JLabel logoLabel = new JLabel();
			logoLabel.setIcon(new ImageIcon(ImageLoader.getImage("/tics/logo.png")));
			logoLabel.setText("Version 0.1");
			logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
			logoLabel.setHorizontalTextPosition(JLabel.CENTER);
			super.add(logoLabel);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.settingsPanel = new JPanel();
		this.settingsPanel.setLayout(new BorderLayout());
		super.add(settingsPanel);
		
		this.createGameTypePanel();
		this.createGeneratorPanel();
		this.createScenarioPanel();
		this.createActionPanel();
		
		this.scenarioPanel.setVisible(false);
	}
	
	private void createGameTypePanel() {
		this.gameTypeBox = new JComboBox(new String[] { "Generator", "Scenario" });
		this.gameTypeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (gameTypeBox.getSelectedItem() == "Scenario") {
					generatorPanel.setVisible(false);
					scenarioPanel.setVisible(true);
				} else {
					generatorPanel.setVisible(true);
					scenarioPanel.setVisible(false);
				}
				
				((Main) GameSettingsPanel.this.getTopLevelAncestor()).pack();
			}
		});
		
		this.settingsPanel.add(this.gameTypeBox, BorderLayout.NORTH);
	}
	
	private void createActionPanel() {
		JPanel panel = new JPanel();
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main main = (Main) GameSettingsPanel.this.getTopLevelAncestor();
				main.displayMatch();
			}
		});
		cancel.setVisible(false);
		panel.add(cancel);
		
		start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main main = (Main) GameSettingsPanel.this.getTopLevelAncestor();
				Match match;
				
				if (GameSettingsPanel.this.gameTypeBox.getSelectedItem() == "Scenario") {
					try {
						match = MatchLoader.loadFromJSON(GameSettingsPanel.this.scenarioFiles[scenarioBox.getSelectedIndex()]);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				} else {
					match = new Match(widthSlider.getValue(), heightSlider.getValue(), playersSlider.getValue(), unitsSlider.getValue(), abilityCountSlider.getValue());
				}
				
				main.setMatch(match);
				main.displayMatch();
			}
		});
		panel.add(start);
		
		this.settingsPanel.add(panel, BorderLayout.SOUTH);
	}
	
	private void createGeneratorPanel() {
		this.generatorPanel = new JPanel();
		this.generatorPanel.setLayout(new BoxLayout(this.generatorPanel, BoxLayout.PAGE_AXIS));
		
		Properties defaultValues = PropertiesLoader.get("presets/Default");
		Properties gameSettings = PropertiesLoader.get("settings/Game");
		int min, max, defaultValue;
		
		this.generatorPanel.add(new JLabel("Preset:"));
		this.presetBox = this.createGeneratorComboBox(new String[] {"Custom"}, new String[] {"Use your own custom settings."});
		
		defaultValue = Integer.parseInt(defaultValues.getProperty("width"));
		min = Integer.parseInt(gameSettings.getProperty("minimum_board_dimensions"));
		max = Integer.parseInt(gameSettings.getProperty("maximum_board_dimensions"));
		this.widthSlider = this.createGeneratorSlider(defaultValue, min, max, "Map Width");

		defaultValue = Integer.parseInt(defaultValues.getProperty("height"));
		min = Integer.parseInt(gameSettings.getProperty("minimum_board_dimensions"));
		max = Integer.parseInt(gameSettings.getProperty("maximum_board_dimensions"));
		this.heightSlider = this.createGeneratorSlider(Integer.parseInt(defaultValues.getProperty("height")), min, max, "Map Height");

		defaultValue = Integer.parseInt(defaultValues.getProperty("players"));
		min = Integer.parseInt(gameSettings.getProperty("minimum_players"));
		max = Integer.parseInt(gameSettings.getProperty("maximum_players"));
		this.playersSlider = this.createGeneratorSlider(Integer.parseInt(defaultValues.getProperty("players")), min, max, "Player Count");

		defaultValue = Integer.parseInt(defaultValues.getProperty("units"));
		min = Integer.parseInt(gameSettings.getProperty("minimum_units_per_player"));
		max = Integer.parseInt(gameSettings.getProperty("maximum_units_per_player"));
		this.unitsSlider = this.createGeneratorSlider(Integer.parseInt(defaultValues.getProperty("units")), min, max, "Units Per Player");
		
		this.placementBox = this.createGeneratorComboBox(new String[] { "Random", "Split", "Mirror" }, new String[] { "units will be placed randomly", "units will be placed randomly, but a player's units will always be together.", "Placement will be mirrored between all teams. Only works for even numbers of players." });
		
		defaultValue = Integer.parseInt(defaultValues.getProperty("abilities"));
		min = Integer.parseInt(gameSettings.getProperty("minimum_abilities_per_unit"));
		max = Integer.parseInt(gameSettings.getProperty("maximum_abilities_per_unit"));
		this.abilityCountSlider = this.createGeneratorSlider(Integer.parseInt(defaultValues.getProperty("abilities")), min, max, "Abilities Per Unit");
		
		this.abilitiesBox = this.createGeneratorComboBox(new String[] { "Random", "Mirror" }, new String[] { "abilities will be assigned randomly.", "abilities are assigned randomly, but each player gets the same set of abilities" });
		
		this.settingsPanel.add(this.generatorPanel, BorderLayout.WEST);
	}
	
	/**
	 * EXTRA: "Put a "Browse" button beside the dropdown list rather than hiding it inside it. Then we can have the dropdown list show the scenarios from the last folder that the filechooser selected."
	 */
	private void createScenarioPanel() {
		this.scenarioPanel = new JPanel();
		this.scenarioPanel.setLayout(new BoxLayout(this.scenarioPanel, BoxLayout.PAGE_AXIS));
		
		File folder = new File(SCENARIO_FOLDER);
		scenarioFiles = folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});
		scenarioNames = new String[scenarioFiles.length];
		scenarioDescriptions = new String[scenarioFiles.length];
		
		for (int index = 0; index < scenarioFiles.length; index++) {
	        Map<String, Object> scenario = JSONLoader.get(SCENARIO_FOLDER + scenarioFiles[index]);
	        
	        scenarioNames[index] = scenario.get("title").toString();
	        scenarioDescriptions[index] = "";
	        scenarioDescriptions[index] += "Author: "+scenario.get("author")+"\n";
	        scenarioDescriptions[index] += "Players: "+scenario.get("players")+"\n";
	        scenarioDescriptions[index] += "Size: "+scenario.get("width")+"x"+scenario.get("height")+"\n\n";
	        scenarioDescriptions[index] += scenario.get("description");
	    }
		
		this.scenarioBox = new JComboBox(scenarioNames);
		this.scenarioBox.setAlignmentX( JComboBox.LEFT_ALIGNMENT );
		this.scenarioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int index = GameSettingsPanel.this.scenarioBox.getSelectedIndex();
				String description = GameSettingsPanel.this.scenarioDescriptions[index];
				GameSettingsPanel.this.scenarioDescription.setText(description);
				((Main) GameSettingsPanel.this.getTopLevelAncestor()).pack();
			}
		});
		this.scenarioPanel.add(this.scenarioBox);

		this.scenarioDescription = new JTextArea(this.scenarioDescriptions[0]);
		this.setSize(JSlider.WIDTH, JTextArea.HEIGHT); // TODO: Make this work.
		this.scenarioDescription.setLineWrap(true);
		this.scenarioDescription.setEditable(false);
		this.scenarioDescription.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		this.scenarioPanel.add(this.scenarioDescription);
		
		this.settingsPanel.add(this.scenarioPanel, BorderLayout.EAST);
	}
	
	private JSlider createGeneratorSlider(int defaultValue, int min, int max, final String text) {
		final JSlider slider = new JSlider(min, max, defaultValue);
		final JLabel label = new JLabel(text+" ["+defaultValue+"]");
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				int value = (int) slider.getValue();
				label.setText(text+" ["+value+"]");
			}
		});
		
		label.setLabelFor(slider);
		
		slider.setAlignmentX( JSlider.LEFT_ALIGNMENT );
		label.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		
		this.generatorPanel.add(label);
		this.generatorPanel.add(slider);

		this.generatorPanel.add(new JSeparator()); // TODO: remove this line. test code.
		
		return slider;
	}
	
	private JComboBox createGeneratorComboBox(String[] values, final String[] descriptions) {
		final JComboBox box = new JComboBox(values);
		final JLabel label = new JLabel(descriptions[0]);

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				label.setText(descriptions[box.getSelectedIndex()]);
			}
		});

		label.setLabelFor(box);

		box.setAlignmentX( JComboBox.LEFT_ALIGNMENT );
		label.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		
		this.generatorPanel.add(box);
		this.generatorPanel.add(label);

		this.generatorPanel.add(new JSeparator()); // TODO: remove this line. test code.
		
		return box;
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			Main main = (Main) super.getTopLevelAncestor();
			cancel.setVisible(main.hasMatch());
		}
		
		super.setVisible(visible);
	}

}
