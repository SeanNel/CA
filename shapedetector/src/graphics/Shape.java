package graphics;

import java.awt.Color;

import std.Picture;

public class Shape {
	/*
	 * Defining a shape class with a reference picture should be more flexible
	 * than defining specific methods to find each shape, then we could find
	 * shapes of arbitrary complexity. We can render shapes to the
	 * referencePicture before using it or load from a file. Then we compare
	 * boundaries from this picture to those in the target picture. We could
	 * even store the resultant boundaries in some format to speed up future
	 * checks.
	 */

	Picture referencePicture;
	Color bgColour = Color.white;
	Color shapeColour = Color.black;

	int minSize;
	int maxSize;

	public Shape() {
	}

	protected Shape(int width, int height) {
		setPicture(new Picture(width, height));
	}

	public void setPicture(Picture picture) {
		referencePicture = picture;
		minSize = 4;
		if (picture.height() > picture.width()) {
			maxSize = picture.height();
		} else {
			maxSize = picture.width();
		}
	}

	public int width() {
		return referencePicture.width();
	}

	public int height() {
		return referencePicture.height();
	}

	protected void clearImage() {
		for (int i = 0; i < referencePicture.width(); i++) {
			for (int j = 0; j < referencePicture.height(); j++) {
				referencePicture.set(i, j, bgColour);
			}
		}
	}

	public Picture getPicture() {
		return referencePicture;
	}

	public void show() {
		// For debugging.
		referencePicture.show();
	}
}