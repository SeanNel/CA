package graphics.filters;

import graphics.ColourCompare;

import java.awt.Color;

import std.Picture;

/**
 * Posterize filter (reduces the number of distinct colours).
 * 
 * @author Sean
 */
public class Posterize {
	/**
	 * Applies the filter to the specified image.
	 * 
	 * @param picture Image to apply the filter to.
	 * @param n Number of intervals per colour channel.
	 * @return Filtered image.
	 */
	public static Picture apply(final Picture picture, final int n) {
		long timer = System.currentTimeMillis();

		float[][] range = ColourCompare.getRange(picture);
		float[][] intervals = new float[range[0].length][n];
		for (int i = 0; i < intervals.length; i++) {
			for (int j = 0; j < n; j++) {
				intervals[i][j] = range[1][i] - range[0][i];
				intervals[i][j] = (intervals[i][j] / (float) (n - 1)) * j;
				intervals[i][j] += range[0][i];
			}
		}

		Picture output = new Picture(picture.width(), picture.height());
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				float[] components = pixel.getColorComponents(null);
				for (int i = 0; i < intervals.length; i++) {
					components[i] = ColourCompare.findInterval(components[i], intervals[i]);
				}
				output.set(x, y, new Color(pixel.getColorSpace(), components,
						1f));
			}
		}

		System.out.println("Posterize() time: "
				+ (System.currentTimeMillis() - timer) + " ms");

		return output;
	}

}
