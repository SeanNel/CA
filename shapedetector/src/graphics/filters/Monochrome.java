package graphics.filters;

import java.awt.Color;

import std.Picture;

/**
 * Monochrome filter. Reduces image to black and white only (without dithering).
 * 
 * @author Sean
 */
public class Monochrome extends ImageFilter {
	public static Picture apply(final Picture picture) {
		Picture output = new Picture(picture.width(), picture.height());
		output.setOriginUpperLeft();
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				int c = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
				if (c > 127) {
					c = 255;
				} else {
					c = 0;
				}
				output.set(x, y, new Color(c, c, c));
			}
		}
		return output;
	}
}
