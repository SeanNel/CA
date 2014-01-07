package helpers;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;

public class Input {
	public static void waitForSpace() {
		final CountDownLatch latch = new CountDownLatch(2);
		KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
			// Anonymous class invoked from EDT
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					latch.countDown();
				return false;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(dispatcher);
		try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // current thread waits here until countDown() is called
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(dispatcher);
	}
}
