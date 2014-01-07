package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PicturePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected BufferedImage image;
	protected Graphics2D graphics;

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D graphics2D = (Graphics2D) graphics.create();
		graphics2D.drawImage(image, 0, 0, this);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		/* invalidate() does not seem to do anything, so just repaint() */
		repaint();
	}

	public void clear() {
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		repaint();
	}

}
