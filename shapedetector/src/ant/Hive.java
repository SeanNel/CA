package ant;

import std.Picture;
import vectors.CartesianVector;
import vectors.PolarVector;

public class Hive {
	// Interface for a collection of Ants.

	private Ant[] ants;
	private Picture picture;

	public Hive(Picture picture, int n) {
		this.picture = picture;
		ants = new Ant[n];

		// TODO: create additional ants and make them go in different directions
		// depending on how many ants we have.

		ants[0] = new Ant(this);
		Ant ant1 = ants[0];

		CartesianVector position = new CartesianVector(0.0, 0.0); // picture.height()
																	// - 1
		PolarVector direction = new PolarVector(1.0, Math.PI / 4.0);
		ant1.place(position, direction);
	}

	public void update() {
		for (int i = 0; i < ants.length; i++) {
			ants[i].move();
		}
	}

	public void draw() {
		for (int i = 0; i < ants.length; i++) {
			ants[i].draw();
		}
	}

	public Picture getPicture() {
		return picture;
	}
}
