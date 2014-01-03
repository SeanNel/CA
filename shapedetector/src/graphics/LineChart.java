package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

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

public class LineChart extends JFrame {
	private static final long serialVersionUID = 1L;

	public JFreeChart chart;

	public LineChart(XYIntervalSeriesCollection dataset) {
		ValueAxis xAxis = new NumberAxis("X");
		ValueAxis yAxis = new NumberAxis("Y");

		XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
		chart = new JFreeChart("Chart", JFreeChart.DEFAULT_TITLE_FONT, plot,
				true);

		chart.getXYPlot().setDataset(dataset);

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

	public XYIntervalSeries getSeries(double[] f, String label) {
		XYIntervalSeries f1Series = new XYIntervalSeries(label);

		for (int i = 0; i < f.length; i++) {
			f1Series.add(i, i, i, f[i], f[i], f[i]);
		}

		return f1Series;
	}
}
