package graphics;

import java.awt.Color;

import std.Picture;

public class Filter {
	public static Picture greyscale(Picture picture) {
		Picture greypic = new Picture(picture.width(), picture.height());
		greypic.setOriginLowerLeft();
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
		greypic.setOriginLowerLeft();
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				Color pixel = picture.get(x, y);
				int c = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
				if (c > 126) {
					c = 255;
				} else {
					c = 0;
				}
				greypic.set(x, y, new Color(c, c, c));
			}
		}
		return greypic;
	}
}
