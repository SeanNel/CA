package graphics;

import java.awt.Color;

import std.Picture;

public class Shape {
	/*
	 * Suppose we find shapes by having each cell determine whether it is at the
	 * centroid of some shape. One way of doing this is by trying to fit
	 * possible shapes and sizes at the hypothetical position, then comparing
	 * the fit to the original image.
	 */
	/*
	 * Defining a shape class with a reference picture should be more flexible
	 * than defining specific methods to find each shape, then we could find
	 * shapes of arbitrary complexity. This method should allow the program to
	 * find overlapping shapes as well, or some shapes that are cropped at the
	 * edge of the image. It will not be able to detect rotated shapes yet. It
	 * would be a good idea to specify size ranges of the expected shape,
	 * otherwise there are infinitely many possible shapes to test against.
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

	public float compare(Picture targetPicture, int x, int y) {
		/*
		 * Determine whether this shape occurs in the target picture, at (x,y).
		 * Returns probability of a match. Instead of doing direct pixel
		 * comparisons, we may want to take average values around each point.
		 * Currently this does not test against different sized images (or
		 * rotated ones).
		 */
		int comparisons = 0;
		int matches = 0;
		x -= referencePicture.width() / 2;
		y -= referencePicture.height() / 2;

		for (int i = 0; i < referencePicture.width()
				&& x + i < referencePicture.width(); i++) {
			for (int j = 0; j < referencePicture.height()
					&& y + j < targetPicture.height(); j++) {
				Color referencePixel = referencePicture.get(i, j);
				if (referencePixel.equals(shapeColour)) {
					comparisons++;
					if (x + i >= 0 && y + j >= 0) {
						Color targetPixel = targetPicture.get(x + i, y + j);
						if (targetPixel.equals(shapeColour)) {
							matches++;
						}
					}
				}
			}
		}
		return (float) matches / (float) comparisons;
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
