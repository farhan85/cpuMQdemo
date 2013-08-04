package ProduceCPUData;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Takes CPU usage measurements and pushes them onto the message topic.
 */
public class Producer {

	/**
	 * URL of the JMS server (which is run on localhost).
	 */
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	/**
	 * The name of the queue/topic the CPU usage data/messages will be sent to.
	 */
	public static final String CPU_TOPIC_NAME = "CPU_TOPIC";

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

		// Create the topic
		Topic topic = session.createTopic(CPU_TOPIC_NAME);

        // Create a MessageProducer for sending messages
		producer = session.createProducer(topic);

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
		System.out.println("Press ctrl-c to stop this program");
        Sigar sigar = new Sigar();

		try {
			while (true) {
				// Get current CPU usage
				CpuPerc perc = sigar.getCpuPerc();
				double cpuUsage = perc.getCombined();
				long tNow = System.currentTimeMillis();

				// Send the timestamp and cpuUsage to the message queue
				TextMessage message = session.createTextMessage(tNow + "," + cpuUsage);
				producer.send(message);
				System.out.println("t: " + tNow + ", CPU Usage: " + cpuUsage);
				
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
		Producer prod = new Producer();
		prod.run();
	}

} 
