package tics.util.load;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Static class that handles the loading and saving of .properties files.
 * These files are then cached, so various parts of the program can access the data via this class.
 * 
 * There are two main areas that we use .properties files. 
 * The first is to store the program's constants, so that they can be tweaked externally by the end user.
 * Constants such as the min and max parameters for a game, and the colours that the program renders for various purposes.
 * The second use are presets for the Match generator.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class PropertiesLoader {
	/** All the game's properties, mapped to the paths to their files. */
	private static HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>();
	
	/**
	 * Retrieve the contents of a .properties file.
	 * This function will either get the Properties object from memory, 
	 * or load it from the file if it hasn't already been cached.
	 * 
	 * @param filePath the path to and name of the .properties file.
	 * @return the Properties object that represents the requested file.
	 */
	public static Properties get(String filePath) {
		if (! filePath.endsWith(".properties")) {
			filePath += ".properties";
		}
		
		if (propertiesMap.containsKey(filePath)) { // Check if the file is cached.
			// If so, return the cached version of the file.
			return propertiesMap.get(filePath);
		} else {
			try {
				InputStream input = new FileInputStream(filePath); // Open the requested file
				Properties properties = new Properties(); // Create a new Properties object.
				properties.load(input); // Populate the properties object with the input file.
				input.close(); // Close the requested file.
				
				propertiesMap.put(filePath, properties); // Cache the file, so that it can be quickly loaded in the future.
				
				return properties; // Return our new properties object.
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null; // If the try/catch block fails, then return null. The properties could not be loaded.
	}
	
	/**
	 * This function is used to save a .properties file.
	 * 
	 * @param properties the data to be saved.
	 * @param filePath the path to and name of the .properties file. Do not include the extension. Example "settings/Game" will resolve to "settings/Game.properties"
	 */
	public static void set(Properties properties, String filePath) {
		if (! filePath.endsWith(".properties")) {
			filePath += ".properties";
		}
		
		try {
			OutputStream output = new FileOutputStream(filePath); // Open the file we are saving to.
			properties.store(output, ""); // Save the properties file to the output stream.
			output.close(); // Close the file we're saving to.

			if (propertiesMap.containsKey(filePath)) { // Check if the file we are saving is cached.
				// If so, update the cache.
				propertiesMap.put(filePath, properties);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This is a convenience function for accessing "settings/Colours.properties"
	 * This file behaves a little differently from your standard .properties file,
	 * because every property we desire, is a reference to another property in the file.
	 * 
	 * Take a look at the file itself to get a better idea of this, 
	 * but basically the file defines a colour palette, and then defines the specific
	 * circumstances that the colours in the palette are used.
	 * 
	 * This function should not be used to retrieve the colours directly from the palette.
	 * If you wish to do that, use the PropertiesLoader.get() method.
	 * 
	 * @param property the name of the property that we want to retrieve from Colours.properties
	 * @return the Color object that corresponds to the requested property.
	 */
	public static Color getColour(String property) {
		Properties colour = PropertiesLoader.get("settings/Colours");
		
		// Each colour is in fact a reference to the colour palette, stored in the same file.
		return Color.decode((String) colour.get(colour.get(property)));
	}
	
}
