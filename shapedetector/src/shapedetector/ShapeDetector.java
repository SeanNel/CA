package shapedetector;

import graphics.PictureFrame;
import graphics.SDPanel;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import neighbourhood.Moore;
import neighbourhood.Neighbourhood;
import neighbourhood.VanNeumannCardinal;
import path.SDPath;
import rules.Rule;
import rules.RuleApplicator;
import rules.cell.FindVertexRule;
import rules.cell.GetBearingConcentrationRule;
import rules.cell.PathFinderRule;
import rules.cell.CacheNeighbourhoodRule;
import rules.cell.CellConnectRule;
import rules.cell.EdgeFinderRule;
import rules.cell.NoiseRemoverRule;
import rules.cell.OutlineFinderRule;
import utils.Picture;
import utils.Stopwatch;
import utils.graph.Graph;
import utils.graph.SynchronizedUndirectedGraph;
import ca.Cell;
import ca.ColourLattice;
import ca.Lattice2D;

/**
 * Finds shapes in an image. Accepts bmp, png and jpg images.
 * <p>
 * Usage: <code>CAShapeDetector [image_path] [r] [epsilon]</code>
 * <p>
 * Some images to test with are:
 * <ul>
 * <li>shape_gallery.jpg</li>
 * <li>img.png: Greyscale image with dithered patterns.</li>
 * <li>test1.png: Simple monochrome image.</li>
 * <li>pipe.png: Low contrast image with text.</li>
 * <li>batman.jpg: Low monochrome contrast image.</li>
 * <li>shapes.png: Low monochrome contrast image.</li>
 * </ul>
 * 
 * @author Sean
 */
public class ShapeDetector {
	protected final static int DEFAULT_NUMTHREADS = 8;
	protected final static double DEFAULT_EPSILON = 0.05d;
	protected final static int DEFAULT_R = 1;

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
		double epsilon = DEFAULT_EPSILON;
		int r = DEFAULT_R;
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

		ShapeDetector.debug = debug;
		int numThreads = DEFAULT_NUMTHREADS;
		if (debug) {
			numThreads = 0;
		}

		Stopwatch stopwatch = new Stopwatch();
		ShapeDetector shapeDetector = new ShapeDetector(numThreads);
		System.out.println("Init elapsed time " + stopwatch.time() + " ms");

		try {
			shapeDetector.apply(picture, epsilon, r);
		} catch (Exception e) {
			handleException(e);
		}

		System.out.println("Finished in " + stopwatch.time() + " ms");
	}

	/**
	 * Constructor.
	 */
	public ShapeDetector(final int numThreads) {
		picturePanel = new SDPanel();
		pictureFrame = new PictureFrame(picturePanel);
		pictureFrame.setTitle("CA Shape Detector");

		RuleApplicator.numThreads = numThreads;
		visible = true;
	}

	/**
	 * Runs ShapeDetector with default parameters.
	 * 
	 * @throws Exception
	 */
	public Picture apply(final Picture picture) throws Exception {
		return apply(picture, DEFAULT_EPSILON, DEFAULT_R);
	}

	/**
	 * Runs ShapeDetector.
	 * <p>
	 * TODO: Might instead want to set epsilon dynamically according to the
	 * colour range in the image.
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
	 * @return The processed image displaying where shapes were found.
	 * @throws Exception
	 */
	public Picture apply(final Picture picture, final double epsilon,
			final int r) throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		ColourLattice colourLattice = new ColourLattice(picture);

		int w = picture.width();
		int h = picture.height();
		Lattice2D<Double> doubleLattice = new Lattice2D<Double>(Double.class,
				w, h);
		Neighbourhood colourMoore = new Moore<Color>(colourLattice, r, true);
		Neighbourhood doubleMoore = new Moore<Double>(doubleLattice, 1, false);

		Collection<Cell> cells = new LinkedList<Cell>();
		for (Cell cell : doubleLattice) {
			cells.add(cell);
		}
		Graph<Cell> clusterGraph = new SynchronizedUndirectedGraph<Cell>(cells);
		/**
		 * Maps representative cells (specifically, the root cells of clusters
		 * in blobGraph) to bags of cells containing the outline cells.
		 */
		Map<Cell, Collection<Cell>> outlineMap = new ConcurrentHashMap<Cell, Collection<Cell>>();
		/**
		 * Maps representative cells (specifically, the root cells of clusters
		 * in blobGraph) to bags of cells containing the outline cells.
		 */
		Map<Cell, SDPath> outlinePathMap = new ConcurrentHashMap<Cell, SDPath>();
		// SynchronizedLinkedList<AbstractShape> shapeList = new
		// SynchronizedLinkedList<AbstractShape>();
		stopwatch.print("Load elapsed time ");
		stopwatch.start();

		try {
			applyColourCellRules(r, epsilon, colourLattice, colourMoore);
			applyDoubleCellRules(r, epsilon, colourLattice, doubleLattice,
					doubleMoore, clusterGraph, outlineMap, outlinePathMap);
			/*
			 * Instead of applying rules to clusters and shapes, the code now
			 * attempts to stay to the CA paradigm of sticking to explicit cell
			 * rules. This may turn out to be inefficient, but let's give it a
			 * shot.
			 */
			// applyBlobRules(doubleLattice, blobMap, shapeList);
			// applyShapeRules(shapeList);
		} catch (Exception e) {
			handleException(e);
		}
		stopwatch.print("Rules total elapsed time ");
		stopwatch.start();

		graphics.ShapeFrame.frame.setVisible(false);
		graphics.IdentityFrame.frame.setVisible(false);

		pictureFrame.setImage(colourLattice.getPicture().getImage());
		pictureFrame.setVisible(visible);
		stopwatch.print("Draw result elapsed time ");
		return colourLattice.getPicture();
	}

	protected void applyColourCellRules(int r, double epsilon,
			ColourLattice colourLattice,
			Neighbourhood colourLatticeNeighbourhood) throws Exception {
		List<Rule<Cell>> rules = new LinkedList<Rule<Cell>>();

		// rules.add(new DummyRule<Cell>());
		/*
		 * Optional neighbourhood initialization rules, useful for determining
		 * performance hits due to neighbourhood changes.
		 */
		rules.add(new CacheNeighbourhoodRule<Color>(colourLattice,
				colourLatticeNeighbourhood));

		/*
		 * Noise removal. TODO: It is unclear why its performance has degraded
		 * by a factor of 2-3, since the algorithm has stayed the same.
		 */
		rules.add(new NoiseRemoverRule(colourLattice,
				colourLatticeNeighbourhood, epsilon));

		colourLattice.apply(rules);
	}

	protected void applyDoubleCellRules(int r, double epsilon,
			ColourLattice colourLattice, Lattice2D<Double> doubleLattice,
			Neighbourhood neighbourhood, Graph<Cell> clusterGraph,
			Map<Cell, Collection<Cell>> outlineMap, Map<Cell, SDPath> pathMap)
			throws Exception {

		VanNeumannCardinal<Double> vanNeumannCardinal = new VanNeumannCardinal<Double>(
				doubleLattice);

		List<Rule<Cell>> rules = new LinkedList<Rule<Cell>>();

		/*
		 * Optional neighbourhood initialization rules, useful for determining
		 * performance hits due to neighbourhood changes.
		 */
		rules.add(new CacheNeighbourhoodRule<Double>(doubleLattice,
				neighbourhood));
		rules.add(new CacheNeighbourhoodRule<Double>(doubleLattice,
				vanNeumannCardinal));

		/* Edge/outline finding */
		rules.add(new EdgeFinderRule(colourLattice, doubleLattice,
				neighbourhood, epsilon));
		/*
		 * The graph implementation may require further optimization when
		 * connecting nodes.
		 */
		rules.add(new CellConnectRule<Double>(doubleLattice, clusterGraph,
				vanNeumannCardinal, colourLattice));
		rules.add(new OutlineFinderRule<Double>(doubleLattice, clusterGraph,
				outlineMap, vanNeumannCardinal, colourLattice));
		rules.add(new PathFinderRule(doubleLattice, clusterGraph, outlineMap,
				pathMap, colourLattice));

		/* Gradient finding */
		rules.add(new GetBearingConcentrationRule(doubleLattice, clusterGraph,
				pathMap, 4, colourLattice));

		/*
		 * TODO: locate vertices with a cell rule, defined as: cell becomes
		 * active when it is the maximum value in its neighbourhood, and it
		 * becomes inactive otherwise.
		 */
		// rules.add(new FindVertexRule(doubleLattice, clusterGraph, pathMap,
		// neighbourhood, 4, colourLattice));

		/*
		 * TODO: Join the found vertices to create a new set of paths. Likely
		 * need a a new path map to store them in.
		 */

		/*
		 * TODO: Compare the new paths to the original paths by Hausdorff
		 * distance and add the detected shapes to list of shapes.
		 */

		doubleLattice.apply(rules);
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	public static void handleException(final Exception e) {
		e.printStackTrace();
		System.exit(0);
	}

	/* The 'blob' and 'shape' rules are deprecated. */

	// protected void applyBlobRules(Lattice2D<Double> lattice,
	// Map<Cell, Blob> blobMap, List<AbstractShape> shapeList)
	// throws Exception {
	//
	// List<Rule<Blob>> rules = new LinkedList<Rule<Blob>>();
	// /*
	// * This rule could be optimized to only find the sequence for the found
	// * vertices, instead of all the outline cells.
	// */
	// // rules.add(new ArrangeOutlineRule<Double>(lattice, blobMap));
	// // rules.add(new BlobIdentifierRule<Double>(shapeList));
	//
	// // rules.add(new BlobDisplayRule(blobMap,
	// // graphics.ShapeFrame.panel));
	// // rules.add(new BlobDrawRule(picturePanel));
	//
	// // ***
	// // rules.add(new GetRadialDistancesRule(lattice, blobMap));
	//
	// /*
	// * Attempt to eliminate the blob generated by the background. (Assumes
	// * the top-left corner is part of the background.)
	// */
	// blobMap.remove(blobMap.get(lattice.getCell(0, 0)));
	//
	// RuleApplicator<Blob> ruleExecutor = new RuleApplicator<Blob>();
	// for (Rule<Blob> rule : rules) {
	// ruleExecutor.apply(blobMap.values(), rule);
	// }
	// }
	//
	// protected void applyShapeRules(List<AbstractShape> shapeList)
	// throws Exception {
	// List<Rule<AbstractShape>> rules = new LinkedList<Rule<AbstractShape>>();
	// // rules.add(new ShapeDisplayRule(this, ShapeFrame.frame));
	// rules.add(new ShapeDrawRule(picturePanel));
	//
	// System.out.println("Number of shapes: " + shapeList.size());
	// // System.out.println("Detected shapes: ");
	//
	// RuleApplicator<AbstractShape> ruleExecutor = new
	// RuleApplicator<AbstractShape>();
	// for (Rule<AbstractShape> rule : rules) {
	// ruleExecutor.apply(shapeList, rule);
	// }
	// }
}
