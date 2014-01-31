package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import math.DiscreteFunction;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

public class LineChartFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
	public final static LineChartFrame frame = new LineChartFrame(dataset);

	protected JFreeChart chart;

	protected LineChartFrame(final XYIntervalSeriesCollection dataset) {
		setTitle("Chart");
		ValueAxis xAxis = new NumberAxis("X");
		ValueAxis yAxis = new NumberAxis("Y");

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
		chart = new SynchronizedChart("Chart", JFreeChart.DEFAULT_TITLE_FONT,
				plot, true);

		ChartTheme currentTheme = new StandardChartTheme("JFree");
		currentTheme.apply(chart);

		XYToolTipGenerator toolTipGenerator = null;
		// toolTipGenerator =
		// StandardXYToolTipGenerator.getTimeSeriesInstance();

		XYURLGenerator urlGenerator = null;
		urlGenerator = new StandardXYURLGenerator();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		renderer.setBaseToolTipGenerator(toolTipGenerator);
		renderer.setURLGenerator(urlGenerator);
		plot.setRenderer(renderer);

		// plot.setForegroundAlpha(0.3f);
		plot.setBackgroundPaint(new Color(250, 250, 250));
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);

		setPreferredSize(new Dimension(640, 480));
		ChartPanel panel = new ChartPanel(chart);
		// chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		setContentPane(panel);
		pack();

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		setLocation(screenSize.width / 5, screenSize.height / 5);
	}

	public JFreeChart getChart() {
		return chart;
	}

	public static void displayData(final double x0, final double x1,
			final UnivariateFunction... f) {
		int n = (int) Math.ceil(x1 - x0);
		displayData(x0, x1, n, f);
	}

	public static void displayData(final double x0, final double x1,
			final int n, final UnivariateFunction... f) {
		synchronized (dataset) {
			dataset.removeAllSeries();
			int i = 0;
			for (UnivariateFunction s : f) {
				dataset.addSeries(frame.getSeries(s, "f" + i++ + "(x)", x0, x1,
						n));
			}
		}
		frame.setVisible(true);
	}

	// public static void displayDifferentialData(final double x0,
	// final double x1, final UnivariateDifferentiableFunction f) {
	// int n = (int) Math.ceil(x1 - x0);
	//
	// UnivariateDifferentiableFunction df = new Differential(f);
	// UnivariateDifferentiableFunction df2 = new Differential(df, n);
	//
	// synchronized (dataset) {
	// dataset.removeAllSeries();
	// dataset.addSeries(frame.getSeries(f, "f(x)", x0, x1, n));
	// dataset.addSeries(frame.getSeries(df, "d/dx f(x)", x0, x1, n));
	// dataset.addSeries(frame.getSeries(df2, "d^2/dx^2 f(x)", x0, x1, n));
	// }
	// frame.setVisible(true);
	//
	// /* for debugging */
	// frame.repaint();
	// }

	/* f is not sampled at the upper bound */
	public XYIntervalSeries getSeries(final UnivariateFunction f,
			final String label, final double x0, final double x1, final int n) {
		if (n < 1) {
			return new XYIntervalSeries(label);
		}
		double[] samples = FunctionUtils.sample(f, x0, x1, n);
		return getSeries(DiscreteFunction.getAbscissae(x0, x1, n), samples,
				label);
	}

	public XYIntervalSeries getSeries(final double[] abscissae,
			final double[] ordinates, final String label) {
		XYIntervalSeries f1Series = new XYIntervalSeries(label);

		for (int i = 0; i < ordinates.length; i++) {
			double x = abscissae[i];
			double y = ordinates[i];
			f1Series.add(x, x, x, y, y, y);
		}
		return f1Series;
	}

	public static void displayData(final double[]... f) {
		synchronized (dataset) {
			dataset.removeAllSeries();
			int i = 0;
			for (double[] s : f) {
				dataset.addSeries(frame.getSeries(s, "Series " + i++));
			}
		}
		frame.setVisible(true);
	}

	public XYIntervalSeries getSeries(final double[] ordinates,
			final String label) {
		XYIntervalSeries f1Series = new XYIntervalSeries(label);

		for (int i = 0; i < ordinates.length; i++) {
			double x = i;
			double y = ordinates[i];
			f1Series.add(x, x, x, y, y, y);
		}
		return f1Series;
	}
}
