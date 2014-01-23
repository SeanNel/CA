package graphics;

import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class PictureFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	protected PicturePanel picturePanel;

	public PictureFrame(final PicturePanel picturePanel) {
		loadMenuBar();
		setPanel(picturePanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("CA");
		// setResizable(false);
	}

	public void setPanel(final PicturePanel picturePanel) {
		this.picturePanel = picturePanel;
		setContentPane(picturePanel);
	}

	public void setImage(final BufferedImage image) {
		// picturePanel.setPreferredSize(new Dimension(image.getWidth(), image
		// .getHeight()));
		picturePanel.setImage(image);
		pack();
	}

	/**
	 * Displays the modified image on screen.
	 */
	public void display() {
		setVisible(true);
	}

	public PicturePanel getPicturePanel() {
		return picturePanel;
	}

	/**
	 * Save the picture to a file in a standard image format. The filetype must
	 * be .png or .jpg.
	 */
	public void save(final String name) {
		save(new File(name));
	}

	/**
	 * Save the picture to a file in a standard image format.
	 */
	public void save(final File file) {
		String filename = file.getName();
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);
		suffix = suffix.toLowerCase();
		if (suffix.equals("jpg") || suffix.equals("png")) {
			try {
				ImageIO.write(picturePanel.image, suffix, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Error: filename must end in .jpg or .png");
		}
	}

	protected void loadMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenuItem menuItem1 = new JMenuItem(" Save...   ");
		menuItem1.addActionListener(this);
		menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(menuItem1);
		setJMenuBar(menuBar);
	}

	/**
	 * Opens a save dialog box when the user selects "Save As" from the menu.
	 */
	public void actionPerformed(final ActionEvent e) {
		FileDialog chooser = new FileDialog(this,
				"Use a .png or .jpg extension", FileDialog.SAVE);
		chooser.setVisible(true);
		if (chooser.getFile() != null) {
			save(chooser.getDirectory() + File.separator + chooser.getFile());
		}
	}
}
