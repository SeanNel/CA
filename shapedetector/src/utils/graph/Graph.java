package utils.graph;

import java.util.Collection;

public interface Graph<T> {

	public void add(T obj);

	public void connect(T obj1, T obj2);

	// public void disconnect(T v1, T v2);

	public boolean isConnected(T obj1, T obj2);

	public Collection<T> getEdges(T obj);

	public Collection<T> getConnected(T obj);

	public T getRoot(T obj);

	public boolean contains(T obj);

}
