package bag;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of a linked list that offers O(1) time for insertion
 * operations, and for the addAll() method in particular.
 * <p>
 * Since this list is designed for merging two lists into one while discarding
 * the other, making changes to the discarded list is acceptable.
 * <p>
 * UNDER DEVELOPMENT; REQUIRES CODING & TESTING.
 * <p>
 * Both ArrayList and LinkedList give poor performance when adding two lists
 * together as is necessary for merging shapes, because they convert the list to
 * an array. Here is code from the API:
 * <p>
 * public boolean addAll(int index, Collection<? extends E> c) {
 * checkPositionIndex(index); Object[] a = c.toArray();
 * <p>
 * When c is another bag, we could add all of its items instantly by pointing
 * this Bag's last node to the first node of c.
 * 
 * @author Sean
 */
public class Bag<Item> implements List<Item> {
	private Node<Item> first;
	private Node<Item> last;
	private int size;

	/**
	 * Constructor.
	 */
	Bag() {
		first = new Node<Item>();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param l
	 *            Bag to copy.
	 */
	Bag(Bag<Item> l) {
		first = l.first;
		last = l.last;
		size = l.size;
	}

	/**
	 * Adds all the items from the specified list to this list.
	 * <p>
	 * When c is another bag, we can add all of its items instantly by pointing
	 * this Bag's last node to the first node of c.
	 */
	@Override
	public boolean addAll(Collection<? extends Item> c) {
		if (c instanceof Bag) {
			/* Voodoo goes here. */
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Item> c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Adds item to end of list.
	 */
	@Override
	public boolean add(Item e) {
		// TODO
		Node<Item> n = last;
		n.next = new Node<Item>();
		n.item = e;
		n.index = size;
		last = n.next;
		size++;
		return true;
	}

	/**
	 * Adds item at position.
	 */
	@Override
	public void add(int index, Item element) {
		// TODO
		int i = 0;
		Node<Item> n = first;

		for (; i++ < index && n.next != null; n = n.next)
			;

		Node<Item> next_n = new Node<Item>();
		next_n.next = n.next;
		next_n.item = n.item;
		next_n.index = n.index;

		n.next = next_n;
		n.item = element;
		n.index = index;

		size++;
		/* Updates node indexes occurring after current index. */
		n = n.next;
		for (; n != null; n = n.next) {
			n.index++;
		}
		last = n;
	}

	/** 
	 * Returns the item at index.
	 * 
	 * @param index
	 * @return
	 */
	public Item get(int index) {
		int i = 0;
		Node<Item> n = first;

		if (index < 0)
			index = 0;
		if (index > size - 1)
			index = size - 1;
		for (; i++ < index && n.next != null; n = n.next)
			;
		return n.item;
	}

	/**
	 * Replaces the item at index.
	 * 
	 * @param item
	 * @param index
	 */
	public void set(Item item, int index) {
		int i = 0;
		Node<Item> n = first;
		for (; i++ < index && n.next != null; n = n.next)
			;
		n.item = item;
	}

	/** 
	 * Returns the number of items in the list.
	 */
	public int size() {
		return size;
	}

	/** 
	 * Returns true if the list is empty.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/** 
	 * Prints all the items of the list in order.
	 */
	public void printList() {
		for (Node<Item> n = first; n != null && n.next != null; n = n.next) {
			System.out.println("Node: " + n);
			System.out.println("Node index: " + n.index);
			System.out.println("Node item: " + n.item);
			System.out.println("Node next: " + n.next);
			System.out.println();
		}
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<Item> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<Item> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<Item> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item remove(int index) {
		// TODO
		if (size == 0)
			return null;

		Item removedItem = null;
		int i = 0;
		Node<Item> n = first;

		if (index < 1) {
			first = first.next;
		} else {
			for (; i++ < index - 1 && n.next != null; n = n.next)
				;
			removedItem = n.next.item;
			n.next = n.next.next;
		}

		size--;
		if (size == 0) {
			/*
			 * In the event that we delete all the nodes, we need to reset the
			 * first node before using it again.
			 */
			first = new Node<Item>();
		} else {
			/* Updates node indexes occurring after current index. */
			n = n.next;
			for (; n != null; n = n.next) {
				n.index--;
			}
		}
		return removedItem;
	}

	@Override
	public boolean remove(Object o) {
		// TODO
		if (size == 0)
			return false;

		int i = 0;
		Node<Item> n = first;

		if (n == o) {
			first = first.next;
		} else {
			for (; (Object) i != o && n.next != null; n = n.next)
				;
			n.next = n.next.next;
		}

		size--;
		if (size == 0) {
			/*
			 * In the event that we delete all the nodes, we need to reset the
			 * first node before using it again.
			 */
			first = new Node<Item>();
		} else {
			/* Updates node indexes occurring after current index. */
			n = n.next;
			for (; n != null; n = n.next) {
				n.index--;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item set(int index, Item element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
