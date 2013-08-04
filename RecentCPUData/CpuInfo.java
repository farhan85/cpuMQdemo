package RecentCPUData;

/**
 * Contains CPU information at a given time.
 */
public class CpuInfo {

	/**
	 * The time at which this data object applies to.
	 */
	private long timestamp;

	/**
	 * The CPU usage
	 */
	private double usage;

	/**
	 * Create a new {@code CpuInfo} object containing the given CPU usage value at the
	 * specified time.
	 */
	public CpuInfo(long timestamp, double usage) {
		this.timestamp = timestamp;
		this.usage = usage;
	}
	
	/**
	 * Gets the time at which this {@code CpuInfo} object refers to.
	 * @return The time at which this {@code CpuInfo} object refers to.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Gets the CPU usage at the time this {@code CpuInfo} object referes to.
	 * @return The CPU usage at the time this {@code CpuInfo} object referes to.
	 */
	public double getCpuUsage() {
		return usage;
	}

}