import std.Picture;
import std.StdDraw;
import ca.Stopwatch;
import ca.edgefinder.CAEdgeFinder;
import ca.noiseremover.CANoiseRemover;
import ca.shapedetector.CAShapeDetector;

/**
 * Finds shapes in an image.
 * <p>
 * Usage: CAClient <image_path>
 * 
 * @author Sean
 */
public class CAClient {
	/** Useful for determining the performance of this program. */
	protected Stopwatch stopwatch;

	/**
	 * Applies CAClient to image given as argument on the command line. Some
	 * images to test with are:
	 * <p>
	 * img.png: Greyscale image with dithered patterns.
	 * <p>
	 * test1.png: Simple monochrome image.
	 * <p>
	 * pipe.png: Low contrast image with text.
	 * <p>
	 * batman.jpg: Low monochrome contrast image.
	 * <p>
	 * shapes.png: Low monochrome contrast image.
	 * 
	 * @param args
	 *            Path to image. Accepts bmp, png and jpg images.
	 */
	public static void main(String[] args) {
		CAClient client = new CAClient();
		client.apply(args[0]);
	}

	/**
	 * Removes noise from specified image then detects shapes in the image.
	 * 
	 * @param path
	 *            Path to image. Accepts bmp, png and jpg images.
	 */
	public void apply(String path) {
		StdDraw.frame.setTitle("Cellular Automaton");
		stopwatch = new Stopwatch();

		Picture picture = new Picture(path);
		StdDraw.setCanvasSize(picture.width(), picture.height());
		picture.setOriginUpperLeft();

		picture = preparePicture(picture);
		picture = findEdges(picture);
		picture = detectShapes(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
		StdDraw.picture(0.5, 0.5, picture.getImage());
	}

	/**
	 * Removes noise from the specified picture and renders it in an appropriate
	 * format to find edges and shapes in it.
	 * 
	 * @param picture
	 *            Picture to remove noise from.
	 * @return Picture with noise removed.
	 */
	protected Picture preparePicture(Picture picture) {
		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);

		CANoiseRemover caNoiseRemover = new CANoiseRemover(0.1f, 2);
		picture = caNoiseRemover.apply(picture);

		// picture = Posterize.apply(picture, 3);
		return picture;
	}

	/**
	 * Finds the edges (outlines) in the picture by rendering them as black
	 * pixels against a white background.
	 * <p>
	 * May soon be deprecated since the current detectShapes(picture) does not
	 * require this.
	 * 
	 * @param picture
	 *            Picture with shapes to find the edges of.
	 * @return Monochrome picture of the edges found.
	 */
	protected Picture findEdges(Picture picture) {
		CAEdgeFinder caEdgeFinder = new CAEdgeFinder(0.05f, 2);
		picture = caEdgeFinder.apply(picture);

		/* May need to take join fractured edges and shrink thick outlines. */
		// CAOutlineFinder caOutlineFinder = new CAOutlineFinder(0.1f, 2);
		// pic = caOutlineFinder.apply(pic);
		return picture;
	}

	/**
	 * Detects the shapes in an image.
	 * 
	 * @param picture
	 *            Picture to detect shapes in.
	 * @return Picture displaying where shapes were found.
	 */
	protected Picture detectShapes(Picture picture) {
		CAShapeDetector caShapeDetector = new CAShapeDetector(0.05f);
		picture = caShapeDetector.apply(picture);
		return picture;
	}
}