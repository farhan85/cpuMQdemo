package RecentCPUData;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

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
	public static final String CPU_TOPIC_NAME = "CPU_TOPIC";
	
	/**
	 * The list of the previous (fixed number) of received CPU usage data.
	 */
	FixedSizeList<CpuInfo> previousCpuInfo;


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

		// Create the topic
		Topic topic = session.createTopic(CPU_TOPIC_NAME);

        // Create a MessageConsumer for receiving the messages
        //consumer = session.createConsumer(destination);
		MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new CpuUsageMessageListener());
		
		// Store a minute's worth of data
		previousCpuInfo = new FixedSizeList<CpuInfo>(6);

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
		
		while (true) {
			try {
				System.in.read();
			} catch (IOException e) {
			}
		}
	}
	
	public void printData() {
		for (CpuInfo info : previousCpuInfo.getData()) {
			System.out.print("(" + info.getTimestamp() + "," + info.getCpuUsage() + ") ");
		}
		System.out.println();
	}

    public static void main(String[] args) throws Exception {
		Consumer cons = new Consumer();
		cons.run();
	}

	/**
	 * When it receives a new message containing the total CPU usage (as a percentage),
	 * it prints the message on screen.
	 */
	private class CpuUsageMessageListener implements MessageListener {

		/**
		 * Prints the received message on screen.
		 * @param messsage The recieved message.
		 */
		public void onMessage(Message message) {
			try {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String[] usageData = textMessage.getText().split(",");
					
					try {
						long timestamp = Long.parseLong(usageData[0]);
						double usage = Double.parseDouble(usageData[1]);
						previousCpuInfo.add(new CpuInfo(timestamp, usage));
						printData();
					} catch (NumberFormatException e) {
						System.out.println("Error. Could not convert either " + usageData[0] + " to a long or " + usageData[1] + " to a double");
					}
				}
			} catch (JMSException e) {
				System.out.println("Error occurred when creating/sending a message to the message queue: " + e.getMessage());
			}
		}
	}

} 
