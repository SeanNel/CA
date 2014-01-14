package ca.shapedetector;

import exceptions.CAException;
import graphics.PictureFrame;
import graphics.SDPanel;
import helpers.Stopwatch;

import java.util.LinkedList;
import java.util.List;

import std.Picture;
import ca.CA;
import ca.Cell;
import ca.Debug;
import ca.lattice.Lattice;
import ca.lattice.CellLattice2D;
import ca.neighbourhood.Moore;
import ca.neighbourhood.Neighbourhood;
import ca.rules.Rule;
import ca.rules.cell.*;

/**
 * Finds shapes in an image. Accepts bmp, png and jpg images.
 * <p>
 * Usage: CAShapeDetector <image_path>
 * <p>
 * Some images to test with are:
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
 * @author Sean
 */
public class ShapeDetector {
	protected final CA<Cell> ca;
	protected CellLattice2D lattice;
	/** Maps cells to blobs. */
	protected BlobMap blobMap;
	/** A list of shapes found. */
	protected ShapeList shapeList;

	/** A frame for displaying the output image. */
	protected final PictureFrame pictureFrame;
	/** A frame for displaying the output image. */
	protected final SDPanel picturePanel;
	/** Signals that the CA should display its results in a window. */
	protected final boolean visible;

	/**
	 * Applies shape detector to image given as argument on the command line.
	 * 
	 * @param path
	 *            Path to image. Accepts bmp, png and jpg images.
	 * @param epsilon
	 *            The difference threshold expressed as a fraction.
	 * @param r
	 *            Search radius. Determines the size of the neighbourhood.
	 */
	public static void main(String[] args) {
		String path;
		float epsilon = 0.05f;
		int r = 1;

		if (args.length == 0) {
			System.out
					.println("Please specify a path to the image to process.");
			return;
		} else {
			path = args[0];
		}
		if (args.length > 1) {
			epsilon = Float.parseFloat(args[1]);
		}
		if (args.length > 2) {
			r = Integer.parseInt(args[2]);
		}

		Picture picture = new Picture(path);

		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);
		// picture = Posterize.apply(picture, 3);

		Stopwatch stopwatch = new Stopwatch();
		ShapeDetector shapeDetector = new ShapeDetector(epsilon, r);
		shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
	}

	/**
	 * Constructor.
	 * 
	 * @see graphics.ColourCompare
	 * @param epsilon
	 *            The difference threshold expressed as a fraction. Determines
	 *            how neighbourhood cells affect this cell's state. Low values
	 *            mean that small differences between cells are ignored.
	 * 
	 * @param r
	 *            Search radius. Determines the size of the cell neighbourhood.
	 */
	/*
	 * Might instead want to set epsilon dynamically according to the colour
	 * range in the image.
	 */
	public ShapeDetector(float epsilon, int r) {
		picturePanel = new SDPanel();
		pictureFrame = new PictureFrame(picturePanel);
		pictureFrame.setTitle("CA Shape Detector");
		visible = true;

		lattice = new CellLattice2D();
		shapeList = new ShapeList(this);
		blobMap = new BlobMap(this, shapeList);

		List<Rule<Cell>> rules = new LinkedList<Rule<Cell>>();
		try {
			Neighbourhood neighbourhoodModel = new Moore(lattice, r);
			// rules.add(new DummyRule(lattice, neighbourhoodModel));
			// rules.add(new GatherNeighboursRule(lattice, neighbourhoodModel));
			rules.add(new NoiseRemoverRule(lattice, neighbourhoodModel, epsilon));
			/* Optional step */
			rules.add(new EdgeFinderRule(lattice, neighbourhoodModel, epsilon));
			rules.add(new BlobAssociationRule(lattice, neighbourhoodModel,
					blobMap));
			// rules.add(new PrintBlobAssociationRule(lattice,
			// neighbourhoodModel, blobMap));
			rules.add(new ShapeFinderRule(lattice, blobMap, epsilon));
			// rules.add(new ShapeAssimilatorRule(lattice, neighbourhoodModel,
			// blobMap));
			rules.add(new OutlineFinderRule(lattice, neighbourhoodModel,
					blobMap));
		} catch (CAException e) {
			handleException(e);
		}

		int numThreads = CA.DEFAULT_NUMTHREADS;
		if (Debug.debug) {
			numThreads = 1;
		}
		ca = new CA<Cell>(lattice, rules, numThreads);
	}

	/**
	 * Sets picture to process and initializes cell lattice.
	 * 
	 * @param picture
	 *            Picture to process.
	 */
	public void setPicture(Picture picture) {
		int w = picture.width();
		int h = picture.height();

		lattice.load(picture);
		pictureFrame.setImage(lattice.getResult().getImage());
		blobMap.clear(w, h);
		shapeList.clear();
	}

	public Picture apply(Picture picture) {
		setPicture(picture);
		pictureFrame.setVisible(visible);

		ca.apply(picture);
		try {
			blobMap.update();
			shapeList.update();
		} catch (CAException e) {
			handleException(e);
		}

		graphics.ShapeFrame.frame.setVisible(false);
		graphics.IdentityFrame.frame.setVisible(false);
		graphics.LineChartFrame.frame.setVisible(false);

		pictureFrame.setVisible(true);
		return ca.getResult();
	}

	/**
	 * Gets the BlobMap.
	 * 
	 * @return
	 */
	public BlobMap getblobMap() {
		return blobMap;
	}

	/**
	 * Gets the ShapeList.
	 * 
	 * @return
	 */
	public ShapeList getShapeList() {
		return shapeList;
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	protected void handleException(CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	public Lattice<Cell> getLattice() {
		return ca.getLattice();
	}

	public CA<Cell> getCA() {
		return ca;
	}

	/**
	 * Gets the panel displaying the CA output.
	 * 
	 * @return
	 */
	public PictureFrame getPictureFrame() {
		return pictureFrame;
	}

	/**
	 * Gets the panel displaying the CA output.
	 * 
	 * @return
	 */
	public SDPanel getPicturePanel() {
		return picturePanel;
	}

}
