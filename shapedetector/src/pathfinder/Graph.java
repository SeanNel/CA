package pathfinder;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class Graph {
	private TreeMap<String, TreeSet<String>> bst;
	private int numEdges;

	public Graph() {
		bst = new TreeMap<String, TreeSet<String>>();
	}

	public void addEdge(String v, String w) {
		// Put v in w's SET and w in v's SET
		if (!bst.containsKey(v))
			bst.put(v, new TreeSet<String>());
		if (!bst.containsKey(w))
			bst.put(w, new TreeSet<String>());
		bst.get(v).add(w);
		bst.get(w).add(v);

		numEdges++;
	}

	public Iterable<String> adjacentTo(String v) {
		return bst.get(v);
	}

	public Iterator<String> vertices() {
		return bst.navigableKeySet().iterator();
	}

	public int V() {
		return bst.size();
	}

	public int E() {
		return numEdges;
	}

	public int degree(String key) {
		TreeSet<String> set = bst.get(key);
		if (set != null) {
			return set.size();
		} else {
			return 0;
		}
	}

	public boolean hasVertex(String key) {
		return bst.get(key) != null;
	}

	public boolean hasEdge(String edge1, String edge2) {
		TreeSet<String> edges = bst.get(edge1);
		for (String edge : edges) {
			if (edge.equals(edge2)) {
				return true;
			}
		}
		return false;
	}
}
