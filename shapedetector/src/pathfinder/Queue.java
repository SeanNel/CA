package pathfinder;

import java.util.LinkedList;

public class Queue<K> extends LinkedList<K> {
	private static final long serialVersionUID = 1L;

	public void enqueue(K key) {
		add(key);
	}

	public K dequeue() {
		return pop();
	}

}
