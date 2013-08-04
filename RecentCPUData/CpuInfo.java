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

	public CpuInfo(long timestamp, double usage) {
		this.timestamp = timestamp;
		this.usage = usage;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public double getCpuUsage() {
		return usage;
	}

}