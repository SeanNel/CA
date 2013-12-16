package graphics;

import java.awt.Color;

import std.Picture;

/**
 * Static class used for comparing and finding the average of colours.
 * 
 * @author Sean
 */
public class ColourCompare {
	/** YCbCrColorSpace colour space. */
	static final YCbCrColorSpace colourSpace = new YCbCrColorSpace();

	/**
	 * Gets the similarity between two colours.
	 * 
	 * @param colour1
	 *            1st colour.
	 * @param colour2
	 *            2nd colour.
	 * @return The similarity is expressed as a ratio: 0f is the maximum
	 *         similarity (the colours are the same), 1f is minimum similarity
	 *         (black and white).
	 */
	public static float getMatch(Color colour1, Color colour2) {
		return 1f - getDifference(colour1, colour2);
	}

	/**
	 * Gets the difference between two colours.
	 * <p>
	 * The YCbCr colour space is the digital equivalent of YUV, and represents
	 * colours with a model of human perception. Probably better to convert
	 * image once to YCbCr and back to RGB once done instead of doing it here.
	 * 
	 * @param colour1
	 *            1st colour.
	 * @param colour2
	 *            2nd colour.
	 * @return The difference is expressed as a ratio: 1f is the maximum
	 *         difference (black and whit)e. 0f means the colours are the same.
	 *         For example, in the RGB colour space, the difference between
	 *         bright red and blue is the vector (255, 0, 0-255, 255-255). The
	 *         ratio of the norm of this vector over the maximum distance
	 *         possible is: 360/510 = 0.71.
	 */
	public static float getDifference(Color colour1, Color colour2) {
		colour1 = toYCbCr(colour1);
		colour2 = toYCbCr(colour2);

		float[] p1Components = colour1.getColorComponents(null);
		float[] p2Components = colour2.getColorComponents(null);
		ColourVector v1 = new ColourVector(p1Components);
		ColourVector v2 = new ColourVector(p2Components);

		// Compute Euclidian distance between colour vectors:
		double distance = v1.distance(v2);
		double max_distance = Math.sqrt(v1.size());

		return (float) distance / (float) max_distance;
	}

	/**
	 * Converts an RGB colour to YCbCr format, with values in the range 0-1.
	 * 
	 * @param colour
	 *            RGB colour.
	 * @return YCbCr colour.
	 */
	public static Color toYCbCr(Color colour) {
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
	public static Color averageColour(Color colour1, Color colour2) {
		Color[] colours = { colour1, colour2 };
		return averageColour(colours);
	}

	/**
	 * Gets the average colour of an array of colours.
	 * 
	 * @param colours
	 *            Array of colours to find the average of.
	 * @return Average colour.
	 */
	public static Color averageColour(Color[] colours) {
		int r = 0, g = 0, b = 0, a = 0;
		int n = 0;
		for (int i = 0; i < colours.length; i++) {
			if (colours[i] != null) {
				r += colours[i].getRed();
				g += colours[i].getGreen();
				b += colours[i].getBlue();
				a += colours[i].getAlpha();
				n++;
			}
		}

		r = (int) Math.round((double) r / (double) n);
		g = (int) Math.round((double) g / (double) n);
		b = (int) Math.round((double) b / (double) n);
		a = (int) Math.round((double) a / (double) n);
		return new Color(r, g, b, a);
	}

	/**
	 * Returns the colour minima and maxima of the picture.
	 * 
	 * @param picture
	 * @return An array of arrays, {minima, maxima} each consisting of an array
	 *         of colour components.
	 */
	public static float[][] getRange(Picture picture) {
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
	public static float findInterval(float component, float[] intervals) {
		for (int i = 0; i < intervals.length; i++) {
			if (component <= intervals[i]) {
				return intervals[i];
			}
		}
		return 0f;
	}
}
