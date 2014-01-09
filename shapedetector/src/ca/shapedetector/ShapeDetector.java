package ca.shapedetector;

import exceptions.CAException;
import graphics.PictureFrame;
import graphics.SDPanel;
import helpers.Stopwatch;

import java.util.LinkedList;

import std.Picture;
import ca.CA;
import ca.neighbourhood.Moore;
import ca.neighbourhood.Neighbourhood;
import ca.rules.cell.BlobAssociationRule;
import ca.rules.cell.CellRule;
import ca.rules.cell.EdgeFinderRule;
import ca.rules.cell.NoiseRemoverRule;
import ca.rules.cell.OutlineFinderRule;
import ca.rules.cell.ShapeFinderRule;

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
public class ShapeDetector extends CA {
	protected BlobMap blobMap;
	protected ShapeList shapeList;
	
	public static boolean debug = true;

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

		shapeDetector.pictureFrame.setVisible(true);
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
	public ShapeDetector(float epsilon, int r) {
		super();
		blobMap = new BlobMap();
		shapeList = new ShapeList();
		/*
		 * Might instead want to set epsilon dynamically according to the colour
		 * range in the image.
		 */
		try {
			loadRules(r, epsilon);
		} catch (CAException e) {
			handleException(e);
		}

		SDPanel panel = new SDPanel();
		pictureFrame = new PictureFrame(panel);
		pictureFrame.setTitle("CA Shape Detector");
	}

	@Override
	public void setPicture(Picture picture) {
		super.setPicture(picture);
		blobMap.load(this);
		shapeList.load(this);
	}

	protected void loadRules(int r, float epsilon) throws CAException {
		Neighbourhood neighbourhoodModel = new Moore(lattice, r);

		cellRules = new LinkedList<CellRule>();
		// cellRules.add(new CADummyRule(lattice));
		// cellRules.add(new CAGatherNeighboursRule(lattice));
		cellRules
				.add(new NoiseRemoverRule(lattice, neighbourhoodModel, epsilon));
		/* Optional step */
		cellRules.add(new EdgeFinderRule(lattice, neighbourhoodModel, epsilon));
		cellRules.add(new BlobAssociationRule(lattice, neighbourhoodModel,
				blobMap));
		// cellRules.add(new PrintBlobAssociationRule(lattice,
		// neighbourhoodModel, blobMap));
		cellRules.add(new ShapeFinderRule(lattice, blobMap, epsilon));
		// cellRules.add(new CAShapeAssimilatorRule(lattice, neighbourhoodModel,
		// blobMap));
		cellRules.add(new OutlineFinderRule(lattice, neighbourhoodModel,
				blobMap));
	}

	@Override
	public Picture apply(Picture picture) {
		super.apply(picture);
		try {
			blobMap.update();
			shapeList.update();
		} catch (CAException e) {
			handleException(e);
		}

		graphics.ShapeFrame.frame.setVisible(false);
		graphics.IdentityFrame.frame.setVisible(false);
		graphics.LineChartFrame.frame.setVisible(false);

		return getResult();
	}

	public BlobMap getblobMap() {
		return blobMap;
	}

	public ShapeList getShapeList() {
		return shapeList;
	}
}
