package tics.util;

/** 
 * A class of static mathematical methods used by Tics. 
 * 
 * @author Michael D'Andrea
 * @author Devindra Payment
 */
public class MathUtil {	
	/** 
	 * Produces a random non-negative integer.
	 * 
	 * @param maximum the upper bound for the random integer. It must be positive.
	 * 
	 * @return a random integer between 0 and max, inclusive.
	 */
	public static int randomInteger(int maximum) {
		return randomInteger(0, maximum);
	}
	
	/** 
	 * Produces a random integer.
	 * 
	 * @param minimum the lower bound for the random integer.
	 * @param maximum the upper bound for the random integer. It must be greater than min.
	 * 
	 * @return a random integer between min and max, inclusive. 
	 */
	public static int randomInteger(int minimum, int maximum) {
		return (int) (minimum + Math.round((Math.random() * (maximum - minimum))));
	}
	
	/**
	 * Checks whether a given integer is between two other integer.
	 * 
	 * @param number the integer to check.
	 * @param firstBoundary one end of the range to check.
	 * @param secondBoundary the other end of the range to check. Order doesn't matter.
	 * @param inclusive true if it counts if the given number is equal to one of the boundary numbers.
	 * 
	 * @return true if the number is within the boundaries, false otherwise.
	 */
	public static boolean isIntegerInRange(int number, int firstBoundary, int secondBoundary, boolean inclusive) {
		int minimum, maximum;
		
		if (firstBoundary < secondBoundary) {
			minimum = firstBoundary;
			maximum = secondBoundary;
		} else {
			minimum = secondBoundary;
			maximum = firstBoundary;
		}
		if (inclusive) {
			return minimum <= number && number <= maximum;
		} else {
			return minimum < number && number < maximum;
		}
	}
}
