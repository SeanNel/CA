package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import std.Picture;

public class Rectangle extends Shape {

	public Rectangle(int width, int height) {
		super(width + 1, height + 1);
		draw();
	}

	protected void draw() {
		clearImage();
		Graphics2D graphics = referencePicture.getImage().createGraphics();
		graphics.setColor(shapeColour);
		graphics.drawRect(0, 0, referencePicture.width(),
				referencePicture.height());
	}

	public static void main(String[] args) {
		Rectangle shape = new Rectangle(200, 100);
		Picture picture = new Picture(300, 200);
		clearPicture(picture);
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(Color.black);
		graphics.drawRect(0, 0, shape.width(), shape.height());

		picture.show();
		System.out.println(shape.compare(picture, 100, 50));
		System.out.println(shape.compare(picture, 80, 80));
	}

	protected static void clearPicture(Picture picture) {
		Color bgColour = Color.white;
		for (int i = 0; i < picture.width(); i++) {
			for (int j = 0; j < picture.height(); j++) {
				picture.set(i, j, bgColour);
			}
		}
	}
}
