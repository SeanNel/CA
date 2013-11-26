package ant;

import java.awt.Image;
import javax.swing.ImageIcon;

import std.Picture;
import std.StdDraw;

public class Ants {
	// This class only works as a client to demo the Hive class.
	
	private Image image;
	private Hive hive;

	public static void main(String[] args) {
		// new Ants("img.png");
		new Ants("pipe.png");
	}

	public Ants(String path) {
		StdDraw.frame.setTitle("Ants");
		Picture pic = new Picture(path);
		pic.setOriginLowerLeft();

		image = new ImageIcon(path).getImage();
		hive = new Hive(pic, 1);

		StdDraw.setCanvasSize(pic.width(), pic.height());
		StdDraw.picture(0.5, 0.5, image);
		refresh();

		while (true) {
			executeStep();
		}
	}

	public void refresh() {
		hive.draw();
	}

	public void executeStep() {
		hive.update();
		refresh();
	}
}