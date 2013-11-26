package pathfinder;

import java.util.Stack;
import java.util.TreeMap;

public class PathFinder {
	/*
	 * TODO: use PathFinder to find *any* path from a cell around a loop back to
	 * the cell. We should probably define a direction (e.g. clockwise) and
	 * limit vertices to join cells that continue in that direction. If we find
	 * some closed loop we can guarantee that we have found a shape, not
	 * necessarily one we are looking for.
	 */

	private TreeMap<String, String> prev;
	private TreeMap<String, Integer> dist;

	public PathFinder(Graph g, String s) {
		// Use BFS to compute distances and previous node on shortest path from
		// s to each vertex.
		prev = new TreeMap<String, String>();
		dist = new TreeMap<String, Integer>();
		Queue<String> q = new Queue<String>();
		q.enqueue(s);
		dist.put(s, 0);
		while (!q.isEmpty()) {
			// Process next vertex on queue.
			String v = q.dequeue();
			for (String w : g.adjacentTo(v)) {
				// Check whether distance is already known.
				if (!dist.containsKey(w)) {
					// Add to queue and save shortest-path information.
					q.enqueue(w);
					dist.put(w, 1 + dist.get(v));
					prev.put(w, v);
				}
			}
		}
	}

	public int distanceTo(String key) {
		Integer d = dist.get(key);
		if (d == null) {
			return -1;
		} else {
			return d;
		}
	}

	public Iterable<String> pathTo(String key) {
		Stack<String> path = new Stack<String>();
		while (dist.containsKey(key)) {
			path.push(key);
			key = prev.get(key);
		}
		return path;
	}
}
