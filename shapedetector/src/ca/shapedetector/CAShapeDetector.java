package ca.shapedetector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import std.Picture;
import ca.CA;
import ca.CACell;
import ca.Stopwatch;
import ca.rules.blob.CABlobIdentifierRule;
import ca.rules.blob.CABlobRule;
import ca.rules.cacell.CABlobAssociationRule;
import ca.rules.cacell.CACellRule;
import ca.rules.cacell.CAEdgeFinderRule;
import ca.rules.cacell.CANoiseRemoverRule;
import ca.rules.cacell.CAOutlineFinderRule;
import ca.rules.cacell.CAShapeFinderRule;
import ca.rules.shape.SDShapeDrawRule;
import ca.rules.shape.SDShapeRule;
import ca.shapedetector.shapes.SDShape;

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
public class CAShapeDetector extends CA {
	/** Table mapping cells to blobs. */
	protected CABlob[][] shapeAssociations;
	/** Set of unique blobs. */
	protected Set<CABlob> blobs;
	/** List of detected shapes. */
	protected List<SDShape> shapes;
	/** Processes to apply to each blob in sequence. */
	public List<CABlobRule> blobRules;
	/** Processes to apply to each SDShape in sequence. */
	public List<SDShapeRule> shapeRules;

	public static final JFrame shapeFrame = new JFrame();
	public static final JFrame identityFrame = new JFrame(); 

	private class blobSorter implements Comparable<blobSorter> {
		CABlob blob;

		public blobSorter(CABlob blob) {
			this.blob = blob;
		}

		@Override
		public int compareTo(blobSorter arg0) {
			double x1 = (blob.getBoundaries()[0][1] - blob.getBoundaries()[0][0]) / 2.0;
			double y1 = (blob.getBoundaries()[1][1] - blob.getBoundaries()[1][0]) / 2.0;

			double x2 = (arg0.blob.getBoundaries()[0][1] - arg0.blob
					.getBoundaries()[0][0]) / 2.0;
			double y2 = (arg0.blob.getBoundaries()[1][1] - arg0.blob
					.getBoundaries()[1][0]) / 2.0;

			if (x1 == x2 && y1 == y2) {
				return 0;
			} else if (y1 < y2) {
				return -1;
			} else {
				if (x1 < x2) {
					return -1;
				} else {
					return 1;
				}
			}
		}

	}

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
		CAShapeDetector shapeDetector = new CAShapeDetector(epsilon, r);
		picture = shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
		shapeDetector.draw();
	}

	protected void createGUI() {
		super.createGUI();
		frame.setTitle("CA Shape Detector");
		
//		shapeFrame.setLocation(0, 0);
//		identityFrame.setLocation(0, 200);
	}

	protected Set<CABlob> sortBlobs(Set<CABlob> shapes) {
		blobSorter[] shapeSorter = new blobSorter[shapes.size()];
		Iterator<CABlob> iterator = shapes.iterator();
		for (int i = 0; i < shapes.size(); i++) {
			shapeSorter[i] = new blobSorter(iterator.next());
		}
		Arrays.sort(shapeSorter);
		Set<CABlob> sortedShapes = new LinkedHashSet<CABlob>();
		for (blobSorter s : shapeSorter) {
			sortedShapes.add(s.blob);
		}
		return sortedShapes;
	}

	public CAShapeDetector(float epsilon, int r) {
		super(epsilon, r);
		// neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD;

		drawOnModelUpdate = true;
		// drawOnCellUpdate = true;
	}

	@Override
	public void setPicture(Picture picture) {
		super.setPicture(picture);
		shapeAssociations = new CABlob[picture.width()][picture.height()];
		// blobs = Collections.synchronizedSet(new HashSet<CAblob>(
		// lattice.length * lattice[0].length));
		blobs = new HashSet<CABlob>(lattice.length * lattice[0].length);
	}

	protected void loadRules() {
		cellRules = new LinkedList<CACellRule>();
		// cellRules.add(new CADummyRule(this));
		// cellRules.add(new CAGatherNeighboursRule(this));
		cellRules.add(new CANoiseRemoverRule(this));
		/* Optional step */
		cellRules.add(new CAEdgeFinderRule(this));
		cellRules.add(new CABlobAssociationRule(this));
		cellRules.add(new CAShapeFinderRule(this));
		// cellRules.add(new CAShapeAssimilatorRule(this));
		cellRules.add(new CAOutlineFinderRule(this));

		blobRules = new LinkedList<CABlobRule>();
		blobRules.add(new CABlobIdentifierRule(this));
		// blobRules.add(new CABlobDisplayRule(this));
		// blobRules.add(new CABlobDrawRule(this));

		shapeRules = new LinkedList<SDShapeRule>();
//		shapeRules.add(new SDShapeDisplayRule(this));
		 shapeRules.add(new SDShapeDrawRule(this));
	}

	@Override
	public Picture apply(Picture picture) {
		super.apply(picture);
		shapes = new LinkedList<SDShape>();

		updateBlobs();
		updateShapes();

		return pictureAfter;
	}

	protected void updateBlobs() {
		/*
		 * Attempt to eliminate the blob generated by the background. (Assumes
		 * the top-left corner is part of the background.)
		 */
		blobs.remove(shapeAssociations[0][0]);

		/*
		 * To ease debugging, sort the shapes in some kind of order instead of
		 * at random.
		 */
		blobs = sortBlobs(blobs);

		CABlobRule currentRule;
		Iterator<CABlobRule> ruleIterator = blobRules.iterator();
		while (ruleIterator.hasNext()) {
			currentRule = ruleIterator.next();

			for (CABlob blob : blobs) {
				currentRule.update(blob);
			}
			System.out.println(currentRule + ", elapsed time: "
					+ ruleStopwatch.time() + " ms");
		}
	}

	protected void updateShapes() {
		System.out.println("Number of shapes: " + shapes.size());
		// System.out.println("Detected shapes: ");

		SDShapeRule currentRule;
		Iterator<SDShapeRule> ruleIterator = shapeRules.iterator();
		while (ruleIterator.hasNext()) {
			currentRule = ruleIterator.next();

			for (SDShape shape : shapes) {
				currentRule.update(shape);
			}
			System.out.println(currentRule + ", elapsed time: "
					+ ruleStopwatch.time() + " ms");
		}
	}

	/**
	 * Merges two cells' shapes together.
	 * 
	 * @param cell1
	 *            1st cell to merge with.
	 * @param cell2
	 *            2nd cell to merge with.
	 * @return The resulting blob.
	 */
	public synchronized CABlob mergeCells(CACell cell1, CACell cell2) {
		return mergeBlobs(getBlob(cell1), getBlob(cell2));
	}

	/**
	 * Merges 2 blobs together.
	 * 
	 * @see CAblobMergerThread
	 * @param shape1
	 *            1st blob to merge with.
	 * @param shape2
	 *            2st blob to merge with.
	 * @return The resulting blob.
	 */
	protected CABlob mergeBlobs(CABlob shape1, CABlob shape2) {
		/* NB */
		if (shape1 == shape2) {
			return null;
		}
		synchronized (shape1) {
			synchronized (shape2) {
				/*
				 * Improves efficiency by merging the smaller shape into the
				 * larger shape.
				 */
				CABlob newShape;
				CABlob oldShape;
				if (shape1.getArea() > shape2.getArea()) {
					newShape = shape1;
					oldShape = shape2;
				} else {
					newShape = shape2;
					oldShape = shape1;
				}

				for (CACell cell : oldShape.getAreaCells()) {
					setBlob(cell, newShape);
				}
				/* Must be removed before merging. */
				// synchronized (blobs) {
				blobs.remove(oldShape);
				// }
				newShape.merge(oldShape);
				return newShape;
			}
		}
	}

	/**
	 * Gets the shape associated with the specified cell.
	 * 
	 * @param cell
	 *            A cell belonging to a shape.
	 * @return The shape associated with the specified cell.
	 */
	public CABlob getBlob(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		return shapeAssociations[coordinates[0]][coordinates[1]];
	}

	/**
	 * Sets the shape associated with the specified cell.
	 * 
	 * @param cell
	 *            A cell belonging to a shape.
	 * @param shape
	 *            The shape associated with the specified cell.
	 */
	public void setBlob(CACell cell, CABlob shape) {
		int[] coordinates = cell.getCoordinates();
		shapeAssociations[coordinates[0]][coordinates[1]] = shape;
	}

	/**
	 * Gets the list of shapes found.
	 * 
	 * @return List of shapes found.
	 */
	public List<SDShape> getShapes() {
		return shapes;
	}

	public void addShape(SDShape shape) {
		shapes.add(shape);
	}

	public void addBlob(CABlob blob) {
		synchronized (blobs) {
			blobs.add(blob);
		}
	}
}
