package ProduceCPUData;

import java.io.*;
import java.util.*;
import java.lang.*;
import org.hyperic.sigar.*;
import javax.jms.*;
import org.apache.activemq.*;

/**
 * Takes CPU usage measurements and pushes them onto the message queue.
 */
public class Producer {

	/**
	 * URL of the JMS server (which is run on localhost).
	 */
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	/**
	 * The name of the queue the CPU usage data/messages will be sent to.
	 */
	public static final String CPU_MQ_NAME = "CPU_MQ";
	
	/**
	 * The MQ Session object.
	 */
	private Session session;

	/**
	 * The {@code MessageProducer} object used for sending messages to the queue.
	 */
	private MessageProducer producer;


	/**
	 * Creates and initialises the queue.
	 */
	public Producer() throws JMSException {
		// Get JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        final Connection connection = connectionFactory.createConnection();
        connection.start();

        // Use a non-transactional session object.
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the queue
        Destination destination = session.createQueue(CPU_MQ_NAME);

        // Create a MessageProducer is used for sending messages (as opposed
        // to MessageConsumer which is used for receiving them)
        producer = session.createProducer(destination);

		// Create a shutdown hook, since the user has to press ctrl-c to shutdown
		// this process. We need to shutdown gracefully (i.e. close the connection first)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Received ctrl-c. Shutting down connections");
				try {
					connection.close();
				} catch (JMSException e) {
					System.out.println("Error occurred when closing connection: " + e.getMessage());
				}
			}
		});
	}

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

				// Send 'cpuUsage' into the message queue
				ObjectMessage message = session.createObjectMessage(new Double(cpuUsage));
				producer.send(message);
				System.out.println("CPU Usage: " + cpuUsage );
				
				// Wait 10 seconds before measuring the CPU usage again
				Thread.sleep(10000);
			}
		}
		catch (SigarException e) {
			System.out.println("Error occurred when trying to get the CPU usage data: " + e.getMessage());
		}
		catch(InterruptedException e) {
			System.out.println("Error occurred when sleeping this thread: " + e.getMessage());
		}
		catch (JMSException e) {
			System.out.println("Error occurred when creating/sending a message to the message queue: " + e.getMessage());
		}
    }

    public static void main(String[] args) throws Exception {
		System.out.println("Press ctrl-c to stop this program");
		Producer prod = new Producer();
		prod.run();
	}

} 
