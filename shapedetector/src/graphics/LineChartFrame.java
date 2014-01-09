package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import math.discrete.dbl.DiscreteFunctionDouble;

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

	protected LineChartFrame(XYIntervalSeriesCollection dataset) {
		setTitle("Chart");
		ValueAxis xAxis = new NumberAxis("X");
		ValueAxis yAxis = new NumberAxis("Y");

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
		SynchronizedChart chart = new SynchronizedChart("Chart",
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);

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

	public static void displayData(final DiscreteFunctionDouble... f) {
		synchronized (dataset) {
			dataset.removeAllSeries();
			int i = 0;
			for (DiscreteFunctionDouble s : f) {
				dataset.addSeries(frame.getSeries(s.toArray(), "f" + i++
						+ "(x)"));
			}
		}
		frame.setVisible(true);
	}

	public static void displayDifferentialData(DiscreteFunctionDouble f) {
		f = f.clone();
		synchronized (dataset) {
			dataset.removeAllSeries();
			dataset.addSeries(frame.getSeries(f, "f(x)"));

			f = f.derivative();
			dataset.addSeries(frame.getSeries(f, "d/dx f(x)"));

			f = f.derivative();
			dataset.addSeries(frame.getSeries(f, "d^2/dx^2 f(x)"));
		}
		frame.setVisible(true);

		/* for debugging */
		frame.repaint();
	}

	public XYIntervalSeries getSeries(DiscreteFunctionDouble f, String label) {
		return getSeries(f.toArray(), label);
	}

	public XYIntervalSeries getSeries(double[] f, String label) {
		XYIntervalSeries f1Series = new XYIntervalSeries(label);

		for (int i = 0; i < f.length; i++) {
			f1Series.add(i, i, i, f[i], f[i], f[i]);
		}
		return f1Series;
	}
}
