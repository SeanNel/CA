package graphics;

import java.awt.Color;

public class Pixel {
	public int x;
	public int y;
	public Color colour;

	public Pixel(Color colour, int x, int y) {
		this.colour = colour;
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "(Pixel) x=" + x + ", y=" + y + ", colour:" + colour; 
	}
}
