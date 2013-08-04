package RecentCPUData;

/**
 * Generic class of a pair of objects.
 */
public class Pair<T1, T2> {

	/**
	 * The first object.
	 */
	private T1 firstObj;
	
	/**
	 * The first object.
	 */
	private T2 secondObj;

	public Pair(T1 firstObj, T2 secondObj) {
		this.firstObj = firstObj;
		this.secondObj = secondObj;
	}
	
	public T1 getFirst() {
		return firstObj;
	}
	
	public T2 getSecond() {
		return secondObj;
	}

}