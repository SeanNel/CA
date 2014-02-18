package utils.graph;

import java.util.Collection;

/**
 * A synchronized, undirected graph.
 * 
 * @author Sean
 * 
 * @param <T>
 */
public class SynchronizedUndirectedGraph<T> extends UndirectedGraph<T> {

	public SynchronizedUndirectedGraph() {
		super();
	}

	public SynchronizedUndirectedGraph(Collection<T> elements) {
		super(elements);
	}

	@Override
	public void add(T element) {
		synchronized (this) {
			super.add(element);
		}
	}

	@Override
	public void connect(T obj1, T obj2) {
		synchronized (this) {
			super.connect(obj1, obj2);
		}
	}

	protected Node getRootNode(Node obj) {
		synchronized (this) {
			return super.getRootNode(obj);
		}
	}

	@Override
	public boolean isConnected(T obj1, T obj2) {
		synchronized (this) {
			return super.isConnected(obj1, obj2);
		}
	}

	@Override
	public T getRoot(T obj) {
		synchronized (this) {
			return super.getRoot(obj);
		}
	}

	@Override
	public Collection<T> getEdges(T obj) {
		synchronized (this) {
			return super.getEdges(obj);
		}
	}

	@Override
	public Collection<T> getConnected(T obj) {
		synchronized (this) {
			return super.getConnected(obj);
		}
	}
}
