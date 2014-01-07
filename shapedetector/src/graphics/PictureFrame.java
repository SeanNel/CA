package graphics;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class PictureFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	protected PicturePanel picturePanel;

	public PictureFrame(PicturePanel picturePanel) {
		// loadMenuBar();
		this.picturePanel = picturePanel;
		setContentPane(picturePanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("CA");
		// setResizable(false);
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

	public void setImage(BufferedImage image) {
		picturePanel.setPreferredSize(new Dimension(image.getWidth(), image
				.getHeight()));
		picturePanel.setImage(image);
		pack();
	}

	/**
	 * Opens a save dialog box when the user selects "Save As" from the menu.
	 */
	public void actionPerformed(ActionEvent e) {
		// FileDialog chooser = new FileDialog(this,
		// "Use a .png or .jpg extension", FileDialog.SAVE);
		// chooser.setVisible(true);
		// if (chooser.getFile() != null) {
		// picture.save(chooser.getDirectory() + File.separator
		// + chooser.getFile());
		// }
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
}
