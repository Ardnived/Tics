package tics.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import tics.match.model.Tile;

/** 
 * A class of miscellaneous static methods used by Tics.
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public abstract class Util {
	/** 
	 * Moves a unit from one tile to another.
	 * This method does it with just the Tile objects and no reference to Board.
	 * 
	 * @param source the tile with the unit to move. This tile must have a unit on it.
	 * @param target the tile to move the unit from source to. This tile must *not* have a unit on it.
	 */
	public static void moveUnit(Tile source, Tile target) {
		if (source.hasUnit()) {
			target.setUnit(source.getUnit());
			source.setUnit(null);
			
			source.repaint();
			target.repaint();
		}
	}
	
	/** 
	 * Flips a buffered image horizontally.
	 * 
	 * @param image the image to flip.
	 * @return the given image, flipped left-to-right.
	 */
	public static BufferedImage flipImageHorizontally(BufferedImage image) {
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-image.getWidth(null), 0);
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(image, null);
	}
}
