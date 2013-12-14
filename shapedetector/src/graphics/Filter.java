package graphics;

import java.awt.Color;

import std.Picture;

public class Filter {
	public static Picture greyscale(Picture picture) {
		Picture greypic = new Picture(picture.width(), picture.height());
		greypic.setOriginUpperLeft();
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				int c = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
				greypic.set(x, y, new Color(c, c, c));
			}
		}
		return greypic;
	}

	public static Picture monochrome(Picture picture) {
		Picture greypic = new Picture(picture.width(), picture.height());
		greypic.setOriginUpperLeft();
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				int c = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
				if (c > 127) {
					c = 255;
				} else {
					c = 0;
				}
				greypic.set(x, y, new Color(c, c, c));
			}
		}
		return greypic;
	}

	public static Picture posterize(Picture picture, int n) {
		// n is the number of intervals per colour channel.
		long timer = System.currentTimeMillis();

		float[][] range = findRange(picture);
		float[][] intervals = new float[range[0].length][n];
		for (int i = 0; i < intervals.length; i++) {
			for (int j = 0; j < n; j++) {
				intervals[i][j] = range[1][i] - range[0][i];
				intervals[i][j] = (intervals[i][j] / (float) (n - 1)) * j;
				System.out.println(intervals[i][j]);
				intervals[i][j] += range[0][i];
			}
		}

		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				float[] components = pixel.getColorComponents(null);
				for (int i = 0; i < intervals.length; i++) {
					components[i] = findInterval(components[i], intervals[i]);
				}
				picture.set(x, y, new Color(pixel.getColorSpace(), components,
						1f));
			}
		}

		System.out.println("Posterize() time: "
				+ (System.currentTimeMillis() - timer) + " ms");

		return picture;
	}

	/**
	 * Returns the colour minima and maxima of the picture.
	 * 
	 * @param picture
	 * @return An array of arrays, {minima, maxima} each consisting of an array
	 *         of colour components.
	 */
	// Each of those contains an array of colour components.
	protected static float[][] findRange(Picture picture) {
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

	/** Returns the upper bound on the interval in which component occurs.
	 * 
	 * @param component
	 * @param intervals
	 * @return
	 */
	protected static float findInterval(float component, float[] intervals) {
		for (int i = 0; i < intervals.length; i++) {
			// System.out.println(component + ": " + intervals[i]);
			if (component <= intervals[i]) {
				return intervals[i];
			}
		}
		return 0f;
	}
}
