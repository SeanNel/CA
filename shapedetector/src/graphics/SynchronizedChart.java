package graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

/* A chart that is synchronized on its dataset. */
public class SynchronizedChart extends JFreeChart {
	private static final long serialVersionUID = 1L;

	public SynchronizedChart(String title, Font titleFont, Plot plot,
			boolean createLegend) {
		super(title, titleFont, plot, createLegend);
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor,
			ChartRenderingInfo info) {
		synchronized (this.getXYPlot().getDataset()) {
			super.draw(g2, chartArea, anchor, info);
		}
	}
}
