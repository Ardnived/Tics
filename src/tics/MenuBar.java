package tics;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The main menu bar for the game, which allows the player to save, load, or quit matches, among other things.
 * Menu item clicks are handled by Main.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	/** The action commands for the various items in this menu, which match the text on their respective menu items. */
	public static final String NEW_ACTION_COMMAND = "New", SAVE_ACTION_COMMAND = "Save", 
			LOAD_ACTION_COMMAND = "Load", EXIT_ACTION_COMMAND = "Exit";
	
	/**
	 * Creates the menu bar for Tics.
	 * 
	 * @param actionListener the listener that will handle menu item clicks.
	 */
	public MenuBar(ActionListener actionListener) 
	{
		super();
		
		System.setProperty("apple.laf.useScreenMenuBar", "true"); //This line makes the JMenuBar appear in Mac OS's native menu bar.
		
		JMenu gameMenu = new JMenu("Game");
		add(gameMenu);
		
		JMenuItem newMenuItem = new JMenuItem(NEW_ACTION_COMMAND);
		newMenuItem.setActionCommand(NEW_ACTION_COMMAND);
		newMenuItem.addActionListener(actionListener);
		gameMenu.add(newMenuItem);
		
		JMenuItem saveMenuItem = new JMenuItem(SAVE_ACTION_COMMAND);
		saveMenuItem.setActionCommand(SAVE_ACTION_COMMAND);
		saveMenuItem.addActionListener(actionListener);
		gameMenu.add(saveMenuItem);
		
		JMenuItem loadMenuItem = new JMenuItem(LOAD_ACTION_COMMAND);
		loadMenuItem.setActionCommand(LOAD_ACTION_COMMAND);
		loadMenuItem.addActionListener(actionListener);
		gameMenu.add(loadMenuItem);
		
		JMenuItem exitMenuItem = new JMenuItem(EXIT_ACTION_COMMAND);
		exitMenuItem.setActionCommand(EXIT_ACTION_COMMAND);
		exitMenuItem.addActionListener(actionListener);
		gameMenu.add(exitMenuItem);
	}
}
