package RecentCPUData;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements a fixed-size list. When a new item is added
 * to this list, the earliest added item is removed if requird to maintain
 * the size. This list operates somewhat similar to a circular list.
 */
public class FixedSizeList<T> {
	
	/**
	 * The maximum size of this list.
	 */
	private int maxSize;
	private LinkedList<T> list;
	
	public FixedSizeList(int size) {
		this.maxSize = size;
		list = new LinkedList<T>();
	}
	
	public void add(T item) {
		// Maintain the maximum size of this list.
		if (list.size() == maxSize) {
			list.removeFirst();
		}
		
		list.add(item);
	}
	
	public List<T> getData() {
		return list;
	}
	
}