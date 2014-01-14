package tics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import tics.match.Match;
import tics.match.MatchSaveData;
import tics.util.load.PropertiesLoader;
import tics.util.load.MatchLoader;

/**
 * The main class for Tics, which doubles as the game frame.
 * TODO: Make note of whatever license this is released under in the code.
 * 
 * EXTRA: Accessibility options/support of some sort. This game could be a good candidate for voice control or vision assistance.
 * Currently, the UI lacks even support for Java's AccessibleContext setup.
 * 
 * EXTRA: Make the dialog boxes the class uses look nicer (perhaps with custom icons.)
 * EXTRA: Maybe improve the exception handling here.
 * EXTRA: Implement crashing with error messages in general.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial") //There should be no need to serialize UI elements in this program.
public class Main extends JFrame implements ActionListener {
	
	/* This constant is for testing, if it is set to anything except null, the game will try to load that scenario. */
	public static String TEST_LOAD_SCENARIO = null;
	
	/** The game currently being played. */
	private Match currentMatch;
	//EXTRA: Add a variable for the starting state of the current match, to allow rematches and saving fun setups after the game.
	
	/** The new game panel */
	private GameSettingsPanel newGamePanel;
	
	/** The file chooser dialog used for saving and loading games. */
	private JFileChooser matchFileChooser;
	
	/**
	 * Starts the game in a new window.
	 * 
	 * @param args the command line arguments, which are ignored.
	 */
	public static void main(String[] args) {
		new Main();
	}
	
	/** Creates a main window, displaying the options screen. */
	public Main() {
		// Preload anything that should be loaded in advance.
		this.preload();
		
		// Set the properties of the program's main JFrame (which is this Main instance).
		this.setUpFrame();
		
		// Set the menu bar.
		super.setJMenuBar(new MenuBar(this));
		
		//Create the JFileChooser when the program starts. 
		this.setUpFileChooser();
		//This prevents a short but noticeable delay when selecting "save" or "load".
		//It also prevents the file chooser from resetting to the default location while the program stays open.
		
		// Initialize NewGamePanel
		newGamePanel = new GameSettingsPanel();
		newGamePanel.setVisible(false);
		super.add(newGamePanel);
		
		displaySettingsPanel();
		
		super.setVisible(true);
	}
	
	private void preload() {
		// Preload the game's properties files.
		PropertiesLoader.get("settings/Colours");
		PropertiesLoader.get("settings/Game");
	}
	
	/** Sets some basic options for the main game frame. */
	private void setUpFrame() {
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setResizable(false);
		super.setLayout(new BorderLayout());
	}
	
	/** Creates and sets up the saving/loading file chooser. */
	private void setUpFileChooser() {
		//EXTRA: Default to opening the last-opened directory when the program starts.
		//EXTRA: Include an easy way of navigating back to the original saves directory.
		matchFileChooser = new JFileChooser(new File(MatchSaveData.SAVE_FILE_FOLDER)); 
		//Note that the file chooser won't break if the saves directory doesn't exist - it'll just default to My Documents or some equivalent.
		matchFileChooser.setFileFilter(new FileNameExtensionFilter("Tics games (.tcs)", MatchSaveData.SAVE_FILE_EXTENSION));
		matchFileChooser.setAcceptAllFileFilterUsed(false);
		matchFileChooser.setMultiSelectionEnabled(false);
	}
	
	/**
	 * Stores a match, so that it can be displayed and played.
	 * 
	 * @param match the match to set.
	 */
	public void setMatch(Match match) {
		if (this.currentMatch != null) {
			super.remove(this.currentMatch.getPanel());
			// TODO: Dispose of the old match.
		}
		
		this.currentMatch = match;
		match.getPanel().setVisible(false);
		super.add(match.getPanel(), BorderLayout.CENTER);
	}
	
	public boolean hasMatch() {
		return this.currentMatch != null;
	}
	
	/**
	 * Display the current match.
	 */
	public void displayMatch() {
		this.newGamePanel.setVisible(false);
		this.currentMatch.getPanel().setVisible(true);
		super.pack(); //Makes the window resize to match the preferred size of it's component (the match panel).
		//The reason that we use pack() instead of directly calling super.setSize() is that 
		//super.setSize() will not give the desired amount of drawable space. Instead, it uses some of that space to draw the title bar.
	}
	
	/** Shows the game's starting/options screen. */
	public void displaySettingsPanel() {
		//Leave the current match intact until a new one is created, just in case the user forgot to save.
		if (this.currentMatch != null) {
			this.currentMatch.getPanel().setVisible(false);
		}
		
		this.newGamePanel.setVisible(true);
		
		super.pack(); //Makes the window resize to match the preferred size of it's visible components.
	}

	@Override
	protected void processWindowEvent(WindowEvent event) {
		try {
			if (event.getID() == WindowEvent.WINDOW_CLOSING) { //Quicksave the game if the window closes.
				if (doesCurrentMatchNeedSaving()) {
					new MatchSaveData(currentMatch).quicksave();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(0); //If anything unexpected happens here, forget the quicksave and just quit.
			//We don't want the program freezing on exit.
		}
		super.processWindowEvent(event);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Handle menu events.
		if (e.getActionCommand() == MenuBar.NEW_ACTION_COMMAND) {
			if (doesCurrentMatchNeedSaving()) {
				promptForMatchSave();
			}
			
			displaySettingsPanel();
		} else if (e.getActionCommand() == MenuBar.SAVE_ACTION_COMMAND) {
			showSaveDialog();//TODO: Grey out the save button when the match is over.
		} else if (e.getActionCommand() == MenuBar.LOAD_ACTION_COMMAND) {
			if (doesCurrentMatchNeedSaving()) {
				promptForMatchSave();
			}
			showLoadDialog();
		} else if (e.getActionCommand() == MenuBar.EXIT_ACTION_COMMAND) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); 
			//Imitate a standard window closing event, like EXIT_ON_CLOSE could cause. This will trigger a quicksave.
		}
	}
	
	/** Opens a JFileChooser window that can be used to save a match as a .tcs file. */
	private void showSaveDialog() {
		//TODO: Prevent saving (grey out this option) when the game is over.
		if (matchFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String filePath = matchFileChooser.getSelectedFile().getPath();
				if (!filePath.endsWith("." + MatchSaveData.SAVE_FILE_EXTENSION)) {
					filePath = filePath.concat("." + MatchSaveData.SAVE_FILE_EXTENSION); //Let users specify names without ".tcs", but add it anyway.
				}
				if (new File(filePath).exists()) { //Prompt for overwriting files, and be sure to check the file with ".tcs" at the end.
					int chosenOption = JOptionPane.showConfirmDialog(this, "Really overwite the existing file?", "Overwrite file?",
					        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (chosenOption == JOptionPane.YES_OPTION) {
						new MatchSaveData(currentMatch).save(filePath);
					} //Don't do anything if the user didn't hit "Yes" at the prompt.
				} else {
					new MatchSaveData(currentMatch).save(filePath); //Creating new files can be done without prompting.
				}
			} catch (IOException exception) {
				exception.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error: Failed to save the game.");
			}
		}
	}
	
	/** Opens a JFileChooser window that can be used to load and play a saved match. */
	private void showLoadDialog() {
		try {
			if (matchFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				if (matchFileChooser.getSelectedFile().isFile()) {
					setMatch(MatchLoader.loadFromSave(matchFileChooser.getSelectedFile().getPath()));
					displayMatch();
				} else {
					throw new FileNotFoundException("The selected file doesn't exist.");
				}
			}
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: Couldn't find the selected file.");
		}
	}
	
	/** Asks the user (via a dialog box) whether they should save the current match, and opens a dialog for this purpose if they accept. */
	private void promptForMatchSave() {
		int chosenOption = JOptionPane.showConfirmDialog(this, "Save the current game before closing it?", "Quit without saving?",
		        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (chosenOption == JOptionPane.YES_OPTION) {
			showSaveDialog();
		}
	}
	
	/** @return true if the current match needs saving, which is to say if it exists, isn't over, and hadn't already been saved. */
	private boolean doesCurrentMatchNeedSaving() {
		if (currentMatch != null) {
			if (currentMatch.isInProgress()) {
				//TODO: The "hasn't already been saved" part.
				//EXTRA: Let users turn these warnings off via the options.
				return true;
			}
		}
		return false;
	}
}
