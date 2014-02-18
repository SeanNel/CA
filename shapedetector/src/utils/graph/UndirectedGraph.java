package utils.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * An undirected graph. (Requires external synchronization for multi-threading.)
 * <p>
 * When connecting two groups of nodes, the current implementation takes the
 * smaller group and places it directly under the root node of the larger group,
 * i.e. it is a weighted QuickFind implementation.
 * 
 * @author Sean
 * 
 * @param <T>
 */
public class UndirectedGraph<T> implements Graph<T> {

	/**
	 * A node that may have many children but only one parent.
	 * 
	 * @author Sean
	 * 
	 * @param <Value>
	 */
	class Node {
		private final T value;
		private Node parent;
		private Collection<Node> children;
		/**
		 * Stores the number of children under this node. Note that this value
		 * is only updated for root nodes.
		 * <p>
		 * May be more efficient to use branch depth, but maybe the size becomes
		 * useful later.
		 */
		private int size;

		public Node(final Node parent, final T value) {
			this.parent = parent;
			this.value = value;
			children = new LinkedList<Node>();
			size = 1;
		}

		// Node addChild(final T child) {
		// size++;
		//
		// Node childNode = new Node(this, child);
		// children.add(childNode);
		// return childNode;
		// }

		void addChild(Node childNode) {
			/* Weighted comparison. */
			Node parentNode;
			if (size > childNode.size) {
				parentNode = this;
			} else {
				parentNode = childNode;
				childNode = this;
			}

			parentNode.size += childNode.size;
			childNode.parent = parentNode;
			parentNode.children.add(childNode);
		}
	}

	protected final Map<T, Node> elements;

	/** Creates a flexible graph (elements may be added later.) */
	public UndirectedGraph() {
		this.elements = new HashMap<T, Node>();
	}

	/**
	 * Creates a graph consisting of the fixed collection of elements specified.
	 */
	public UndirectedGraph(Collection<T> elements) {
		this.elements = new HashMap<T, Node>(elements.size(), 2f);
		for (T element : elements) {
			this.elements.put(element, new Node(null, element));
		}
	}

	@Override
	public void add(T element) {
		/* Replaces elements if they exist already */
		elements.put(element, new Node(null, element));
	}

	@Override
	public void connect(T obj1, T obj2) {
		if (obj1 == obj2) {
			return;
		}

		Node node1 = elements.get(obj1);
		Node node2 = elements.get(obj2);

		Node root1 = getRootNode(node1);
		Node root2 = getRootNode(node2);

		if (root1 == root2) {
			return;
		}
		root1.addChild(root2);
	}

	protected Node getRootNode(Node node) {
		if (node == null) {
			return null;
		}
		/* Walks up the tree. */
		while (node.parent != null) {
			node = node.parent;
		}
		return node;
	}

	@Override
	public boolean isConnected(T obj1, T obj2) {
		Node node1 = elements.get(obj1);
		Node node2 = elements.get(obj2);

		Node root1 = getRootNode(node1);
		Node root2 = getRootNode(node2);
		// return root1.getValue() == root2.getValue();
		return root1 == root2;
	}

	@Override
	public T getRoot(T obj) {
		Node node = elements.get(obj);
		if (node == null) {
			return null;
		} else {
			return getRootNode(node).value;
		}
	}

	/**
	 * Gets all the elements directly connected to the specified element.
	 */
	@Override
	public Collection<T> getEdges(T obj) {
		Node node = elements.get(obj);
		Collection<Node> childNodes = node.children;
		if (childNodes.isEmpty()) {
			return Collections.emptyList();
		} else {
			Collection<T> children = new LinkedList<T>();
			for (Node n : childNodes) {
				children.add(n.value);
			}
			return children;
		}
	}

	/**
	 * Gets all the (unordered) elements connected to the specified element
	 * (excluding itself).
	 */
	@Override
	public Collection<T> getConnected(T obj) {
		Node node = elements.get(obj);
		Node root = getRootNode(node);
		Collection<T> children = new LinkedList<T>();
		Collection<Node> childNodes = root.children;

		while (!childNodes.isEmpty()) {
			Collection<Node> grandChildren = new LinkedList<Node>();
			for (Node n : childNodes) {
				children.add(n.value);
				grandChildren.addAll(n.children);
			}
			childNodes = grandChildren;
		}
		return children;
	}

	@Override
	public boolean contains(T obj) {
		return elements.containsKey(obj);
	}

	// public Set<T> keySet() {
	// return elements.keySet();
	// }
}
