package RecentCPUData;

import javax.jms.Session;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.Destination;
import javax.jms.TextMessage;
import javax.jms.MessageConsumer;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Takes CPU usage measurements and pushes them onto the message queue.
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
	 * The {@code MessageConsumer} object used for receiving messages to the queue.
	 */
	private MessageConsumer consumer;


	/**
	 * Creates and initialises the queue.
	 */
	public Consumer() throws JMSException {
		// Get JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        final Connection connection = connectionFactory.createConnection();
        connection.start();

        // Use a non-transactional session object.
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the queue
        Destination destination = session.createQueue(CPU_MQ_NAME);

        // Create a MessageConsumer for receiving the messages
        consumer = session.createConsumer(destination);

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
					System.out.println("Received message '" + textMessage.getText() + "'");
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
