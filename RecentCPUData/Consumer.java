package RecentCPUData;

import javax.jms.Session;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.Destination;
import javax.jms.TextMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Receives CPU usage measurements, displays (last minute of data) and pushes
 * them onto another message queue to be collected by another process (for storing
 * all historical data).
 */
public class Consumer {

	/**
	 * URL of the JMS server (which is run on localhost).
	 */
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	/**
	 * The name of the queue the CPU usage data/messages will be sent to.
	 */
	public static final String CPU_MQ_NAME = "CPU_MQ";
	
	/**
	 * A second queue is used to send the CPU data to another process which
	 * will store all the historical data.
	 */
	public static final String CPU_MQ_HIST_NAME = "CPU_MQ_HIST";

	/**
	 * The {@code MessageConsumer} object used for receiving messages to the queue.
	 */
	private MessageConsumer consumer;
	
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
	public Consumer() throws JMSException {
		// Get JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        final Connection connection = connectionFactory.createConnection();
        connection.start();

        // Use a non-transactional session object.
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the queue
        Destination destination = session.createQueue(CPU_MQ_NAME);

        // Create a MessageConsumer for receiving the messages
        consumer = session.createConsumer(destination);
		
        // Create a MessageProducer for resending messages to the second queue
        Destination historicalDataQueueDest = session.createQueue(CPU_MQ_HIST_NAME);
        producer = session.createProducer(historicalDataQueueDest);

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
		try {
			while (true) {
				// Wait for the next message
				Message message = consumer.receive();
				
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					
					// TODO: Process the received data
					System.out.println("Received message '" + textMessage.getText() + "'");

					// Send the data to the historical data queue
					producer.send(textMessage);
				}
			}
		}
		catch (JMSException e) {
			System.out.println("Error occurred when creating/sending a message to the message queue: " + e.getMessage());
		}
    }

    public static void main(String[] args) throws Exception {
		System.out.println("Press ctrl-c to stop this program");
		Consumer cons = new Consumer();
		cons.run();
	}

} 
