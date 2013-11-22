import graphics.Filter;
import ca.CAModel;
import ca.edgefinder.CAEdgeFinder;
import ca.noiseremover.CANoiseRemover;

import std.Picture;
import std.StdDraw;

public class Client {

	public static void main(String[] args) {
		// new Client("img.png"); // Greyscale image with dithered patterns.
		// new Client("test.png"); // Simple monochrome image.
		// new Client("pipe.png"); // Low contrast image with text.
		new Client("batman.jpg"); // Low monochrome contrast image.
	}

	public Client(String path) {
		StdDraw.frame.setTitle("Cellular Automaton");

		Picture pic = new Picture(path);
		StdDraw.setCanvasSize(pic.width(), pic.height());

		pic.setOriginLowerLeft();
		// pic = Filter.greyscale(pic);
		// pic = Filter.monochrome(pic);

		long timer = System.currentTimeMillis();

		CANoiseRemover caBlur = new CANoiseRemover(0.08f, 1);
		caBlur.setPicture(pic);
		for (int i = 0; i < 5 && caBlur.update(); i++) {
		}
		caBlur.draw();

		// @ e=0.08f, r=1, time was 602ms
		// @ e=0.08f, r=2, time was 993ms
		// @ e=0.08f, r=3, time was 2131ms
		// @ e=0.08f, r=4, time was 3080ms
		// As is to be expected, the performance impact from r increases
		// quadratically.
		// Epsilon changes don't make much difference.
		// Parameters of about 0.05f, r=3 seem to give good results in general.
		CAEdgeFinder caEdgeFinder = new CAEdgeFinder(0.08f, 4);
		caEdgeFinder.setPicture(pic);
		for (int i = 0; i < 15 && caEdgeFinder.update(); i++) {
			// Work is done in update() call above.
		}
		caEdgeFinder.finish(); // Displays the monochrome result on screen.

		System.out.println("Time taken: "
				+ (System.currentTimeMillis() - timer) + " ms");
	}
}