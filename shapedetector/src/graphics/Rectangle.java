package graphics;

import java.awt.Graphics2D;

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
}
