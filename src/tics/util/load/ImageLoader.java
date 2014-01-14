package tics.util.load;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Static class that loads and stores all the images used in the game.
 * Other classes access images from here, so they are not duplicated, and are not loaded until they are needed.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class ImageLoader {
	/** All the game's images, mapped to the paths to their files. This static collection is pre-initialized. */
	private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	/**
	 * Accesses an image by path. Loads the image if it hasn't been loaded already, and returns it.
	 * 
	 * @param imagePath the path to the image to retrieve.
	 * @return the named image, or null if no image with that name seems to exist.
	 * @throws IOException if an image couldn't be loaded.
	 * @throws IllegalArgumentException if a path to an image couldn't be constructed properly.
	 */
	public static BufferedImage getImage(String imagePath) throws IOException, IllegalArgumentException {
		if (images.containsKey(imagePath)) {
			return images.get(imagePath);
		} else {
			try {
				images.put(imagePath, ImageIO.read(ImageLoader.class.getResource(imagePath)));
			} catch (IllegalArgumentException exception) {
				throw new IllegalArgumentException("'" + imagePath + "' can't be used as an image classpath.", exception);
				//TODO: Use a reasonably informative message if this error comes up.
			}
			return images.get(imagePath);
		}
	}
}
