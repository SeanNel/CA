package graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.Plot;

/* A chart that is synchronized on its dataset. */
public class SynchronizedChart extends JFreeChart {
	private static final long serialVersionUID = 1L;

	public SynchronizedChart(final String title, final Font titleFont,
			final Plot plot, final boolean createLegend) {
		super(title, titleFont, plot, createLegend);
		StandardChartTheme theme = new StandardChartTheme("Theme");
		theme.apply(this);
	}

	@Override
	public void draw(final Graphics2D g2, final Rectangle2D chartArea,
			final Point2D anchor, final ChartRenderingInfo info) {
		synchronized (this.getXYPlot().getDataset()) {
			super.draw(g2, chartArea, anchor, info);
		}
	}
}
