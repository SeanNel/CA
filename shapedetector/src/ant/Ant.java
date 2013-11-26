package ant;

import graphics.ColourCompare;
import graphics.Pixel;

import java.awt.Color;
import java.util.Stack;

import std.Picture;
import std.StdDraw;
import vectors.CartesianVector;
import vectors.PolarVector;

/*
 * TODO: If ant spends too much time in one area (gets stuck), place it on an unexplored bit.
 * Take average of position to determine where it gets stuck.
 * Take average of other ants average positions to find places to move it to.
 */

public class Ant {
	protected Hive hive;
	protected CartesianVector position;
	protected PolarVector direction;
	protected Stack<Pixel> trail = new Stack<Pixel>();
	protected int bounces = 0;
	protected int bounce_limit;
	protected float tolerance;
	protected double sampleArea = 4.0;
	public static double LEFT = Math.PI / 4.0;
	public static double RIGHT = -Math.PI / 4.0;
	public static double BACK = Math.PI;
	public Color antColour = new Color(255, 0, 0, 128); // Red
	public Color feelerColour = new Color(128, 255, 0, 32); // Green
	public Color starboardColour = new Color(128, 128, 255, 32); // Blue

	public Color avgTrailColour;

	public Ant(Hive hive) {
		this.hive = hive;
		position = new CartesianVector();
		direction = new PolarVector();

		// This should be built upon in Hive.
		bounce_limit = 6;
		// Might want to set this dynamically according to the colour range in
		// the image.
		tolerance = 0.3f;
	}

	public Ant(Hive hive, CartesianVector position, PolarVector direction) {
		this.hive = hive;
		place(position, direction);
	}

	public void place(CartesianVector position, PolarVector direction) {
		this.position = position;
		this.direction = direction;
		avgTrailColour = sampleCurrentPosition();
		addToTrail(avgTrailColour);
	}

	public void move() {
		if (bounces > bounce_limit) {
			return;
		}

		// Move one unit in the current direction.
		position = position.add(direction); // .unitVector()

		if (outOfBounds() && trail.size() > 25) {
			bounces++;
		}

		Color currentSample = sampleCurrentPosition();
		addToTrail(currentSample);
		// System.out.println(currentSample + ">" + position);

		navigate();
	}

	protected void addToTrail(Color sample) {
		int x = position.getIntX();
		int y = position.getIntY();
		trail.push(new Pixel(sample, x, y));

		Color[] colours = { avgTrailColour, sample };
		avgTrailColour = ColourCompare.averageColour(colours);
	}

	protected void navigate() {
		float pBow = ColourCompare.getMatch(sampleDirection(0.0),
				avgTrailColour);

		float pStarboard = ColourCompare.getMatch(sampleDirection(RIGHT),
				avgTrailColour);
		float pPort = ColourCompare.getMatch(sampleDirection(LEFT),
				avgTrailColour);

		if (trail.size() < 25) {
			return;
		}
		// System.out.println("pPort: " + pPort + ", pStarboard: " +
		// pStarboard);

		if (pBow < 1.0 - tolerance) {
			PolarVector mirror = direction.getPerpendicular();
			// double angleOfAttack = Math.asin(pPort - pStarboard) / 2.0;
			double angleOfAttack = (pPort - pStarboard) * Math.PI;
			mirror = mirror.rotate(angleOfAttack);
			direction = direction.reflect(mirror);
			// System.out.println("Bounce");
		} else {
			// double bearing = pStarboard - pPort;
			// turn(bearing * LEFT);
		}
	}

	protected boolean outOfBounds() {
		Picture picture = hive.getPicture();

		int x = position.getIntX();
		int y = position.getIntY();

		if (x < 0) {
			position.setX(0);
			direction = direction.flipX();
			return true;
		} else if (x >= picture.width()) {
			position.setX(picture.width() - 1);
			direction = direction.flipX();
			return true;
		}

		if (y < 0) {
			position.setY(0);
			direction = direction.flipY();
			return true;
		} else if (y >= picture.height()) {
			position.setY(picture.height() - 1);
			direction = direction.flipY();
			return true;
		}

		return false;
	}

	protected void turn(double theta) {
		direction = direction.rotate(theta);
	}

	protected Color sampleCurrentPosition() {
		int x = position.getIntX();
		int y = position.getIntY();
		return hive.getPicture().get(x, y);
	}

	protected Color sampleDirection(double theta) {
		// System.out.println(direction + ">" + direction.rotate(LEFT) + ">"
		// + position.add(direction.rotate(LEFT)));
		PolarVector direction = this.direction.rotate(theta);
		CartesianVector vector = position.add(direction.multiply(sampleArea));
		drawBox(vector, sampleArea, feelerColour);
		return sample(vector, sampleArea);
	}

	// Sample (a square) area around a point.
	protected Color sample(CartesianVector position, double sampleArea) {
		Picture picture = hive.getPicture();

		// Starting position.
		int x = (int) Math.round(position.getX() - sampleArea / 2.0);
		int y = (int) Math.round(position.getY() - sampleArea / 2.0);

		// Ending position.
		int x2 = (int) Math.round(position.getX() + sampleArea / 2.0);
		int y2 = (int) Math.round(position.getY() + sampleArea / 2.0);

		if (x < 0) {
			x = 0;
		} else if (x >= picture.width()) {
			x = picture.width();
		}
		if (y < 0) {
			y = 0;
		} else if (y >= picture.height()) {
			y = picture.height();
		}

		if (x2 < 0) {
			x2 = 0;
		} else if (x2 >= picture.width()) {
			x2 = picture.width();
		}
		if (y2 < 0) {
			y2 = 0;
		} else if (y2 >= picture.height()) {
			y2 = picture.height();
		}

		int p = 0;
		Color[] pixels = new Color[(x2 - x) * (y2 - y)];
		for (int i = x; i < x2; i++) {
			for (int j = y; j < y2; j++) {
				pixels[p++] = picture.get(i, j);
			}
		}

		// Return average of all the sampled pixels.
		Color sample = ColourCompare.averageColour(pixels);
		return sample;
	}

	protected Color averageTrailColour() {
		Pixel[] pixels = new Pixel[trail.size()];
		pixels = trail.toArray(pixels);
		Color[] colours = new Color[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			colours[i] = pixels[i].colour;
		}
		return ColourCompare.averageColour(colours);
	}

	public void draw() {
		drawBox(position, 1.0, antColour);
	}

	public void drawBox(CartesianVector position, double r, Color colour) {
		StdDraw.setPenColor(colour);
		Picture picture = hive.getPicture();
		double x = position.getX() / (double) picture.width();
		double y = position.getY() / (double) picture.height();
		double w = r / picture.width();
		double h = r / picture.height();
		StdDraw.rectangle(x, y, h / 2.0, w / 2.0);
	}

	// TODO
	// protected Color trailMeanVariation() {
	// Pixel[] pixels = new Pixel[trail.size()];
	// pixels = trail.toArray(pixels);
	// Color[] colours = new Color[pixels.length];
	// for (int i = 0; i < pixels.length; i++) {
	// colours[i] = pixels[i].colour;
	// }
	// return averageColor(colours);
	// }

	// public static void main(String[] args) {
	// Ant ant = new Ant(new Hive(new Picture("pipe.png")));
	//
	// Color white = new Color(255, 255, 255);
	// Color offwhite = new Color(255, 240, 240);
	// Color offwhite2 = new Color(255, 225, 255);
	// Color red = new Color(255, 0, 0);
	// Color blue = new Color(0, 0, 255);
	// Color grey = new Color(128, 128, 128);
	// Color black = new Color(0, 0, 0);
	// System.out.println(ant.getMatch(red, blue));
	// System.out.println(ant.getMatch(red, grey));
	// }
}
