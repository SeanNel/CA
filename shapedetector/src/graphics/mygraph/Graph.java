package graphics.mygraph;

import java.awt.Graphics2D;
import java.util.ArrayList;

import std.Picture;

/**
 * 
 * @author Sean
 */
public class Graph {
	protected double x = 0.0;
	protected double y = 0.0;
	protected double width = 1.0;
	protected double height = 1.0;

	protected double x_offset = 0.1;
	protected double y_offset = 0.1;

	protected double max = 0.0;

	protected ArrayList<Series> series;
	protected String currency_symbol = "$";
	protected boolean render_points = false;

	protected Picture picture;

	// ...

	public Graph(Picture picture) {
		series = new ArrayList();
		this.picture = picture;
	}

	public void move(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void resize(double w, double h) {
		width = w;
		height = h;
	}

	public void setCurrency(String c) {
		currency_symbol = c;
	}

	// protected static void sumData() {
	// for (int i = 0; i < accounts.size(); i++) {
	// double val = accounts.get(i).getValue();
	// String msg = "Time: " + t + ", Amount: " + currency_symbol + val;
	//
	// JOptionPane.showMessageDialog(null, msg);
	// }
	// }

	public void addSeries(Series s) {
		series.add(s);
	}

	// protected void addData(ArrayList<Double> series, double value) {
	// series.add(value);
	// }

	public void render() {
		for (int i = 0; i < series.size(); i++) {
			max = series.get(i).getMax();
		}

		for (int i = 0; i < series.size(); i++) {
			renderSeries(series.get(i));
		}

		for (int i = 0; i < series.size(); i++) {
			renderKey(series.get(i), i);
		}
	}

	protected void renderSeries(Series<Double> s) {
		double w = (width - x_offset) / s.size();
		double h = (height - y_offset) / max;

		double last_x = x + x_offset;
		double last_y = y + y_offset;

		drawXScale(s);
		drawYScale(s);
		plot(s, w, h, last_x, last_y);
	}

	protected void plot(Series s, double w, double h, double last_x,
			double last_y) {
		double r = .003;
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(s.colour);

		// Draw data points
		for (int i = 0; i < s.size(); i++) {
			double new_x = x + (i * w) + x_offset;
			double new_y = y + ((Double) s.get(i) * h) + y_offset; // .doubleValue()

			if (render_points)
				graphics.drawRect((int) new_x, (int) new_y, (int) r, (int) r);
			graphics.drawLine((int) last_x, (int) last_y, (int) new_x,
					(int) new_y);

			last_x = new_x;
			last_y = new_y;
		}
	}

	protected void drawXScale(Series s) {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(s.colour);

		int scale_num = 6;
		double w = (width - x_offset) / scale_num;
		for (int i = 0; i <= scale_num; i++) {
			int val = Math.round(i * s.size() / scale_num);
			graphics.drawString(Integer.toString(val),
					(int) (x + i * w + x_offset), (int) (y - y_offset - .10));
		}
	}

	protected void drawYScale(Series s) {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(s.colour);

		int scale_num = 6;
		double h = (height - y_offset) / scale_num;
		for (int i = 0; i <= scale_num; i++) {
			double val = ((i * max / scale_num)); // + series.get(0)
			val = Math.round(val * 10.0) / 10.0;
			graphics.drawString(Double.toString(val),
					(int) (x + x_offset - .07), (int) (y + i * h + y_offset));
		}
	}

	protected void renderKey(Series s, int i) {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(s.colour);

		double h = .1;
		double yy = y_offset + (series.size() * h);
		graphics.drawString(s.label, (int) (x + x_offset + width),
				(int) (y_offset + yy + (h * i)));
	}
}
