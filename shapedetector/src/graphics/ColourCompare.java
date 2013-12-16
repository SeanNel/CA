package graphics;

import java.awt.Color;

public class ColourCompare {
	static final YCbCrColorSpace colourSpace = new YCbCrColorSpace();

	public static float getMatch(Color pixel1, Color pixel2) {
		return 1f - getDifference(pixel1, pixel2);
	}

	public static float getDifference(Color pixel1, Color pixel2) {
		// The difference is expressed as a percentage: 1f is the maximum
		// difference, i.e. black and white. 0f means exactly alike, i.e. the
		// same colour. For example, in the RGB colour space, the difference
		// between bright red and blue is the vector (255, 0, 0-255, 255-255).
		// The ratio of the norm of this vector over the maximum distance
		// possible is: 360/510 = 0.71.

		// The YCbCr colour space is the digital equivalent of YUV, and
		// represents colours with a model of human perception.
		// Probably better to convert image once to YCbCr and back to RGB once
		// done instead of doing it here.
		pixel1 = toYCbCr(pixel1);
		pixel2 = toYCbCr(pixel2);

		float[] p1Components = pixel1.getColorComponents(null);
		float[] p2Components = pixel2.getColorComponents(null);
		ColourVector v1 = new ColourVector(p1Components);
		ColourVector v2 = new ColourVector(p2Components);

		// Compute Euclidian distance between colour vectors:
		double distance = v1.distance(v2);
		double max_distance = Math.sqrt(v1.size());

		return (float) distance / (float) max_distance;
	}

	public static Color toYCbCr(Color colour) {
		float[] c = colourSpace.fromRGB(colour.getColorComponents(null));
		for (int i = 0; i < c.length; i++) {
			c[i] /= 255f;
		}
		return new Color(colourSpace, c, 1f);
	}

	public static Color averageColour(Color pixel1, Color pixel2) {
		Color[] pixels = { pixel1, pixel2 };
		return averageColour(pixels);
	}

	public static Color averageColour(Color[] pixels) {
		int r = 0, g = 0, b = 0, a = 0;
		int n = 0;
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] != null) {
				r += pixels[i].getRed();
				g += pixels[i].getGreen();
				b += pixels[i].getBlue();
				a += pixels[i].getAlpha();
				n++;
			}
		}

		r = (int) Math.round((double) r / (double) n);
		g = (int) Math.round((double) g / (double) n);
		b = (int) Math.round((double) b / (double) n);
		a = (int) Math.round((double) a / (double) n);
		return new Color(r, g, b, a);
	}

	/*
	 * public static float standardDeviation(Color[] pixels) { // Using integers
	 * instead of floats may improve performance considerably // here. Color
	 * mean = averageColour(pixels);
	 * 
	 * float lambda = 0f; for (int i = 0; i < pixels.length; i++) { float x =
	 * getDifference(pixels[i], mean); lambda = x * x; } return (float)
	 * Math.sqrt(lambda / (float) pixels.length); } public static float
	 * getMaximumDifference(Color pixel, Color[] pixels) { float max = 0f; for
	 * (int i = 0; i < pixels.length; i++) { float difference =
	 * getDifference(pixel, pixels[i]); if (difference > max) { max =
	 * difference; } } return max; }
	 * 
	 * public static float getMinimumDifference(Color pixel, Color[] pixels) {
	 * float min = 0f; for (int i = 0; i < pixels.length; i++) { float
	 * difference = getDifference(pixel, pixels[i]); if (difference < min) { min
	 * = difference; } } return min; }
	 * 
	 * public static int getIntegerDifference(Color pixel1, Color pixel2) { //
	 * Similar to method above, but uses integers to improve performance.
	 * 
	 * // The difference is expressed as a percentage: 100 is the maximum //
	 * difference, i.e. black and white. 0 means exactly alike, i.e. the // same
	 * colour. For example, the difference between bright red and blue // is the
	 * vector (255, 0, 0-255, 255-255). The ratio of the norm of this // vector
	 * over the maximum distance possible is: 360/510 = 0.71.
	 * 
	 * // ColorSpace colourSpace = new YCbCrColorSpace(); float[] p1Components =
	 * pixel1.getColorComponents(null); float[] p2Components =
	 * pixel2.getColorComponents(null); ColourVector v1 = new
	 * ColourVector(p1Components); ColourVector v2 = new
	 * ColourVector(p2Components);
	 * 
	 * // Compute Euclidian distance between colour vectors: double distance =
	 * v1.distance(v2); double max_distance = Math.sqrt(v1.size());
	 * 
	 * return (int) (distance / max_distance); }
	 */
}
