package tics.util.load;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

/**
 * Static class that handles the loading and saving of .json files.
 * In the context of this program, .json files define a initial board layout (aka scenario)
 * that can be used to start a Match.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class JSONLoader {
	/** The object that is used to parse JSON files. */
	private static final JSONParser parser = JsonParserFactory.getInstance().newJsonParser();
	/** The object that is used to generate JSON files. */
	private static final JSONGenerator generator = JsonGeneratorFactory.getInstance().newJsonGenerator();
	
	/** All previously loaded jsons, mapped to the paths to their files. */
	private static HashMap<String, Map<String, Object>> jsonMap = new HashMap<String, Map<String, Object>>();
	
	/**
	 * Retrieve the contents of a .json file.
	 * This function will either get the Map<String, Object> object from memory, 
	 * or load it from the file if it hasn't already been cached.
	 * 
	 * @param filePath the path to and name of the .json file.
	 * @return the Map<String, Object> that represents the requested file.
	 */
	public static Map<String, Object> get(String filePath) {
		if (! filePath.endsWith(".json")) {
			filePath += ".json";
		}
		
		if (jsonMap.containsKey(filePath)) { // Check if the file is cached.
			// If so, return the cached version of the file.
			return jsonMap.get(filePath);
		} else {
			try {
				@SuppressWarnings("unchecked") // We know that it will always be a Map<String, Object>
				Map<String, Object> data = parser.parseJson(new FileInputStream(filePath), "UTF-8");
				
				jsonMap.put(filePath, data);
				return data;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null; // If the try/catch block fails, then return null. The json could not be loaded.
	}
	
	/**
	 * TODO: Test and comment this function.
	 * 
	 * @param filePath
	 * @param json
	 */
	public static void set(String filePath, Map<String, Object> json) {
		if (! filePath.endsWith(".json")) {
			filePath += ".json";
		}
		
		
		try {
			FileOutputStream output = new FileOutputStream(filePath); // Open the file we are saving to.
			String content = generator.generateJson(json);
			
			output.write(content.getBytes()); // Convert the json string to bytes.
			output.close(); // Close the file we're saving to.

			if (jsonMap.containsKey(filePath)) { // Check if the file we are saving is cached.
				// If so, update the cache.
				jsonMap.put(filePath, json);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
