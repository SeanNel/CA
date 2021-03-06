package graphics.filters;

import java.awt.Color;

import std.Picture;

public class Greyscale extends ImageFilter {
	public static Picture greyscale(final Picture picture) {
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

}
