package graphics;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import std.Picture;

/**
 * Static class used for comparing and finding the average of colours.
 * 
 * @author Sean
 */
public class ColourCompare {
	/** YCbCr colour space. */
	static final YCbCrColorSpace colourSpace = new YCbCrColorSpace();

	/**
	 * Computes Euclidian distance between two colour vectors. Assumes both
	 * vectors have the same size.
	 * <p>
	 * The Color.getColorComponents() method returns a float array, so it makes
	 * sense to work in the same format. In addition the integer component is
	 * never greater than 255, and floats give greater precision when computing
	 * the square root to get distance.
	 * <p>
	 * Experiments showed a difference of 100ms when using only ints, so it
	 * really is not worth the loss in precision.
	 * 
	 * @param vector1
	 *            A vector to find the distance from.
	 * @param vector2
	 *            A vector to find the distance to.
	 * @return Distance between the vectors.
	 */
	public static double distance(final float[] vector1, final float[] vector2) {
		float sum = 0.0f;
		for (int i = 0; i < vector1.length; i++) {
			float x = vector1[i] - vector2[i];
			sum += x * x;
		}
		return Math.sqrt(sum);
	}

	/**
	 * Gets the difference quotient between two colours.
	 * <p>
	 * The YCbCr colour space is the digital equivalent of YUV, and represents
	 * colours with a model of human perception.
	 * <p>
	 * Probably better to convert image once to YCbCr and back to RGB once done
	 * instead of doing it here.
	 * 
	 * @param colour1
	 *            1st colour.
	 * @param colour2
	 *            2nd colour.
	 * @return The difference is expressed as a ratio: 1f is the maximum
	 *         difference (black and white). 0f means the colours are the same.
	 *         For example, in the RGB colour space, the difference between
	 *         bright red and blue is the vector (255, 0, 0-255, 255-255). The
	 *         ratio of the norm of this vector over the maximum distance
	 *         possible is: 360/510 = 0.71.
	 */
	public static double getDifference(final Color colour1, final Color colour2) {
		Color c1 = toYCbCr(colour1);
		Color c2 = toYCbCr(colour2);

		float[] p1Components = c1.getColorComponents(null);
		float[] p2Components = c2.getColorComponents(null);

		double distance = distance(p1Components, p2Components);
		double max_distance = Math.sqrt(p1Components.length);

		return distance / max_distance;
	}

	/**
	 * Converts an RGB colour to YCbCr format, with values in the range 0-1.
	 * 
	 * @param colour
	 *            RGB colour.
	 * @return YCbCr colour.
	 */
	public static Color toYCbCr(final Color colour) {
		float[] c = colourSpace.fromRGB(colour.getColorComponents(null));
		for (int i = 0; i < c.length; i++) {
			c[i] /= 255f;
		}
		return new Color(colourSpace, c, 1f);
	}

	/**
	 * Gets the average colour of two colours.
	 * 
	 * @param colour1
	 *            A colour.
	 * @param colour2
	 *            Another colour.
	 * @return Average colour.
	 */
	public static Color meanColour(final Color colour1, final Color colour2) {
		LinkedList<Color> colours = new LinkedList<Color>();
		colours.add(colour1);
		colours.add(colour2);
		return meanColour(colours);
	}

	/**
	 * Gets the mean (average) colour of an array of colours.
	 * 
	 * @param colours
	 *            Array of colours to find the average of.
	 * @return Average colour.
	 */
	public static Color meanColour(final Collection<Color> colours) {
		int r = 0, g = 0, b = 0, a = 0;
		for (Color colour : colours) {
			r += colour.getRed();
			g += colour.getGreen();
			b += colour.getBlue();
			a += colour.getAlpha();
		}

		double n = (double) colours.size();
		r = (int) Math.round((double) r / n);
		g = (int) Math.round((double) g / n);
		b = (int) Math.round((double) b / n);
		a = (int) Math.round((double) a / n);
		return new Color(r, g, b, a);
	}

	/**
	 * Gets the median colour from an array of colours, relative to the
	 * reference colour.
	 * <p>
	 * O(NlogN) performance.
	 * 
	 * @param refColour
	 *            Reference colour.
	 * @param colours
	 *            Array of colours to find the average of.
	 * @return Median colour.
	 */
	public static Color medianColour(final Color refColour,
			final Collection<Color> colours) {
		if (colours == null || colours.isEmpty()) {
			return null;
		}

		Hashtable<Double, Color> colourTable = new Hashtable<Double, Color>();

		for (Color colour : colours) {
			double difference = getDifference(refColour, colour);
			colourTable.put(difference, colour);
		}

		Double[] keys = colourTable.keySet().toArray(
				new Double[colourTable.size()]);
		Arrays.sort(keys);
		int median = (int) Math.floor(keys.length / 2.0);

		return colourTable.get(keys[median]);
	}

	/**
	 * Gets the median colour from an array of colours.
	 * <p>
	 * O(N^2) performance.
	 * 
	 * @param colours
	 *            Array of colours to find the average of.
	 * @return Median colour.
	 */
	public static Color medianColour(final Collection<Color> colours) {
		if (colours == null || colours.isEmpty()) {
			return null;
		}

		Hashtable<Double, Color> colourTable = new Hashtable<Double, Color>();

		for (Color colour1 : colours) {
			Double maxDifference = Double.MAX_VALUE;
			for (Color colour2 : colours) {
				if (colour1 != colour2) {
					Double difference = getDifference(colour1, colour2);
					if (difference > maxDifference) {
						maxDifference = difference;
					}
				}
			}
			colourTable.put(maxDifference, colour1);
		}

		Double[] keys = colourTable.keySet().toArray(
				new Double[colourTable.size()]);
		Arrays.sort(keys);
		int median = (int) Math.floor(keys.length / 2.0);

		return colourTable.get(keys[median]);
	}

	/**
	 * Returns the colour minima and maxima of the picture.
	 * 
	 * @param picture
	 * @return An array of arrays, {minima, maxima} each consisting of an array
	 *         of colour components.
	 */
	public static float[][] getRange(final Picture picture) {
		int numComponents = picture.get(0, 0).getColorComponents(null).length;

		float[] minima = new float[numComponents];
		float[] maxima = new float[numComponents];

		for (int i = 0; i < numComponents; i++) {
			minima[i] = 1f;
		}

		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				float[] components = picture.get(x, y).getColorComponents(null);
				for (int i = 0; i < numComponents; i++) {
					if (components[i] < minima[i]) {
						minima[i] = components[i];
					} else if (components[i] > maxima[i]) {
						maxima[i] = components[i];
					}
				}
			}
		}

		float[][] result = { minima, maxima };
		return result;
	}

	/**
	 * Helper method. Returns the upper bound on the interval in which the
	 * specified colour component occurs.
	 * 
	 * @param component
	 *            Colour component.
	 * @param intervals
	 *            Array of intervals component may occur in.
	 * @return Upper bound on the interval in which the specified colour
	 *         component occurs.
	 */
	public static float findInterval(final float component,
			final float[] intervals) {
		for (int i = 0; i < intervals.length; i++) {
			if (component <= intervals[i]) {
				return intervals[i];
			}
		}
		return 0f;
	}
}
