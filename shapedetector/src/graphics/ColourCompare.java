package graphics;

import java.awt.Color;

import vectors.ColourVector;

public class ColourCompare {

	// TODO: replace RGB colour space with YCbCr:
	// The YCbCr colour space is the digital equivalent of YUV, and
	// represents colours with a model of human perception.
	public static float getMatch(Color pixel1, Color pixel2) {
		// The match is defined as a similarity percentage: 0f means no
		// similarity, i.e. black and white. 1f means exactly similar, i.e. the
		// same colour. For example, the difference between bright red and blue
		// is the vector (255, 0, 0-255, 255-255). The similarity is the ratio
		// of the norm of this vector over the maximum distance possible:
		// 360/510 = 0.71.

		// ColorSpace colourSpace = new YCbCrColorSpace();
		float[] p1Components = pixel1.getColorComponents(null);
		float[] p2Components = pixel2.getColorComponents(null);
		ColourVector v1 = new ColourVector(p1Components);
		ColourVector v2 = new ColourVector(p2Components);

		// Compute Euclidian distance between colour vectors:
		double distance = v1.distance(v2);
		double max_distance = Math.sqrt(v1.size());

		float match = 1f - (float) distance / (float) max_distance;
		return match;
	}

	public static Color averageColour(Color[] pixels) {
		int r = 0, g = 0, b = 0, a = 0;
		for (int i = 0; i < pixels.length; i++) {
			r += pixels[i].getRed();
			g += pixels[i].getGreen();
			b += pixels[i].getBlue();
			a += pixels[i].getAlpha();
		}
		r = (int) Math.round((double) r / (double) pixels.length);
		g = (int) Math.round((double) g / (double) pixels.length);
		b = (int) Math.round((double) b / (double) pixels.length);
		a = (int) Math.round((double) a / (double) pixels.length);
		return new Color(r, g, b, a);
	}
}
