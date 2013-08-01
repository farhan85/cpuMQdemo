package ProduceCPUData;

import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

import org.hyperic.sigar.*;

/**
 * Takes CPU usage measurements and pushes them onto the message queue.
 */
public class Producer {

	/**
	 * Takes periodic measurements of the total CPU usage (as a percentage)
	 * and pushes them onto the message queue.
	 */
    public void run() {
        Sigar sigar = new Sigar();

		try {
			while (true) {
				// Get current CPU usage
				CpuPerc perc = sigar.getCpuPerc();
				double cpuUsage = perc.getCombined();
				
				// TODO. Send 'cpuUsage' into a message queue
				System.out.println("cpuUsage: " + cpuUsage);
				
				// Wait 10 seconds before measuring the CPU usage again
				Thread.sleep(10000);
			}
		} catch (SigarException e) {
			System.out.println("Error occurred when trying to get the CPU usage data: " + e.getMessage());
		}
		catch(InterruptedException e) {
			System.out.println("Error occurred when sleeping this thread: " + e.getMessage());
		}
    }

    public static void main(String[] args) throws Exception {
		System.out.println("Press ctrl-c to stop this program");
		Producer prod = new Producer();
		prod.run();
	}

} 
