import graphics.Shape;

import java.util.ArrayList;

import std.Picture;
import std.StdDraw;
import ca.edgefinder.CAEdgeFinder;
import ca.noiseremover.CANoiseRemover;
import ca.shapedetector.CAShapeDetector;

public class Client {

	public static void main(String[] args) {
		// new Client("img.png"); // Greyscale image with dithered patterns.
		// new Client("test.png"); // Simple monochrome image.
		// new Client("pipe.png"); // Low contrast image with text.
		// new Client("batman.jpg"); // Low monochrome contrast image.
		new Client("shapes.png"); // Low monochrome contrast image.
	}

	public Client(String path) {
		StdDraw.frame.setTitle("Cellular Automaton");

		Picture pic = new Picture(path);
		StdDraw.setCanvasSize(pic.width(), pic.height());

		pic.setOriginLowerLeft();
		// pic = Filter.greyscale(pic);
		// pic = Filter.monochrome(pic);

		long timer = System.currentTimeMillis();

		float epsilon = 0.08f;
		int r = 4;

		CANoiseRemover caNoiseRemover = new CANoiseRemover(epsilon, 1);
		caNoiseRemover.setPicture(pic);
		for (int i = 0; i < 5 && caNoiseRemover.update(); i++) {
		}
		caNoiseRemover.draw();

		// For batman.jpg:
		// @ e=0.08f, r=1, time was 602ms
		// @ e=0.08f, r=2, time was 993ms
		// @ e=0.08f, r=3, time was 2131ms
		// @ e=0.08f, r=4, time was 3080ms
		// As is to be expected, the performance impact from r increases
		// quadratically.

		// Before using pictureBefore and pictureAfter:
		// After implementing pictureBefore and pictureAfter, I was able to
		// eliminate a loop in the update() method, and we get a performance
		// increase: @ e=0.08f, r=4, time was 2701 ms. (14% increase)

		// Epsilon changes should not make much difference to performance.
		// Parameters of about e=0.05f, r=3 seem to give good results in
		// general.

		CAEdgeFinder caEdgeFinder = new CAEdgeFinder(epsilon, r);
		caEdgeFinder.setPicture(pic);
		for (int i = 0; i < 15 && caEdgeFinder.update(); i++) {
			// Work is done in update() call above.
		}
		caEdgeFinder.finish(); // Displays the monochrome result on screen.

		CAShapeDetector caShapeDetector = new CAShapeDetector(epsilon, r);
		caShapeDetector.setPicture(pic);
		ArrayList<Shape> shapes = caShapeDetector.detectShapes();
		System.out.println(shapes);

		// caCornerFinder.finish(); // Displays the monochrome result on screen.

		System.out.println("Time taken: "
				+ (System.currentTimeMillis() - timer) + " ms");
	}
}