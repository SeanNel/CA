package ca.shapedetector;

import exceptions.CAException;
import graphics.PictureFrame;
import graphics.SDPanel;
import helpers.Stopwatch;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import std.Picture;
import ca.CA;
import ca.Cell;
import ca.lattice.Lattice;
import ca.lattice.PictureLattice;
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
	protected final static double DEFAULT_EPSILON = 0.05d;
	protected final static int DEFAULT_R = 1;

	protected ShapeList shapeList;
	protected final double epsilon;
	protected final int r;
	protected final int numThreads;

	protected CA<Color> ca;
	protected PictureLattice lattice;
	/** Maps cells to blobs. */
	protected BlobMap<Color> blobMap;
	/** A list of shapes found. */

	/** A frame for displaying the output image. */
	protected final PictureFrame pictureFrame;
	/** A frame for displaying the output image. */
	protected final SDPanel picturePanel;
	/** Signals that the CA should display its results in a window. */
	protected final boolean visible;

	/**
	 * When true, displays the shape being processed, distribution graphs and
	 * other debug info while running. Unpredictable output when running in
	 * parallel mode, so forces single-threaded processing.
	 */
	public static boolean debug;

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
	public static void main(final String[] args) {
		String path;
		double epsilon = 0.05d;
		int r = 1;
		boolean debug = false;

		if (args.length == 0) {
			System.out
					.println("Please specify a path to the image to process.");
			return;
		} else {
			path = args[0];
		}
		if (args.length > 1) {
			epsilon = Double.parseDouble(args[1]);
		}
		if (args.length > 2) {
			r = Integer.parseInt(args[2]);
		}
		if (args.length > 3) {
			debug = Boolean.parseBoolean(args[3]);
		}

		Picture picture = new Picture(path);

		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);
		// picture = Posterize.apply(picture, 3);

		Stopwatch stopwatch = new Stopwatch();
		ShapeDetector shapeDetector = new ShapeDetector(epsilon, r,
				CA.DEFAULT_NUMTHREADS, debug);
		shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
	}

	/**
	 * Constructor with default parameters.
	 */
	public ShapeDetector() {
		this(DEFAULT_EPSILON, DEFAULT_R, CA.DEFAULT_NUMTHREADS, false);
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
	 * @param numThreads
	 * @param debug
	 */
	/*
	 * Might instead want to set epsilon dynamically according to the colour
	 * range in the image.
	 */
	public ShapeDetector(final double epsilon, final int r,
			final int numThreads, final boolean debug) {
		this.epsilon = epsilon;
		this.r = r;
		this.numThreads = numThreads;
		ShapeDetector.debug = debug;

		picturePanel = new SDPanel();
		pictureFrame = new PictureFrame(picturePanel);
		pictureFrame.setTitle("CA Shape Detector");
		visible = true;
	}

	/**
	 * Sets picture to process and initializes cell lattice.
	 * 
	 * @param picture
	 *            Picture to process.
	 * @throws CAException
	 */
	public void setPicture(final Picture picture) throws CAException {
		int w = picture.width();
		int h = picture.height();

		lattice = new PictureLattice(picture);
		List<Rule<Cell<Color>>> rules = new LinkedList<Rule<Cell<Color>>>();
		try {
			shapeList = new ShapeList(this);
			blobMap = new BlobMap<Color>(this, shapeList);

			Neighbourhood<Color> neighbourhoodModel = new Moore<Color>(lattice,
					r);
			// rules.add(new DummyRule(lattice, neighbourhoodModel));
			// rules.add(new GatherNeighboursRule(lattice, neighbourhoodModel));
			rules.add(new NoiseRemoverRule(lattice, neighbourhoodModel, epsilon));
			/* Optional step */
			rules.add(new EdgeFinderRule(lattice, neighbourhoodModel, epsilon));
			rules.add(new BlobAssociationRule<Color>(lattice,
					neighbourhoodModel, blobMap));
			// rules.add(new PrintBlobAssociationRule(lattice,
			// neighbourhoodModel, blobMap));
			rules.add(new BlobMergeRule(lattice, blobMap, epsilon));
			// rules.add(new ShapeAssimilatorRule(lattice, neighbourhoodModel,
			// blobMap));
			rules.add(new OutlineFinderRule<Color>(lattice, neighbourhoodModel,
					blobMap));
		} catch (CAException e) {
			handleException(e);
		} finally {
			ca = new CA<Color>(lattice, rules, numThreads);
		}

		pictureFrame.setImage(lattice.getResult().getImage());
		blobMap.clear(w, h);
		shapeList.clear();
	}

	/**
	 * Runs the cell, blob and shape rules to detect shapes in the target
	 * picture.
	 * 
	 * @param picture
	 * @return
	 */
	public Picture apply(final Picture picture) {
		try {
			setPicture(picture);
		} catch (CAException e) {
			handleException(e);
		}
		pictureFrame.setVisible(visible);

		try {
			ca.apply();
			blobMap.apply();
			shapeList.apply();
		} catch (CAException e) {
			handleException(e);
		}

		graphics.ShapeFrame.frame.setVisible(false);
		graphics.IdentityFrame.frame.setVisible(false);
		graphics.LineChartFrame.frame.setVisible(false);

		pictureFrame.setVisible(true);
		return ((PictureLattice) ca.getLattice()).getResult();
	}

	/**
	 * Gets the BlobMap.
	 * 
	 * @return
	 */
	public BlobMap<Color> getblobMap() {
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
	protected void handleException(final CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Gets the cell lattice.
	 * 
	 * @return
	 */
	public Lattice<Color> getLattice() {
		return lattice; //ca.getLattice();
	}

	/**
	 * Gets the CA.
	 * 
	 * @return
	 */
	public CA<Color> getCA() {
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
