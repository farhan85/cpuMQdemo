javac -cp lib/sigar/sigar.jar;lib/activemq/activemq-all-5.8.0.jar ProduceCPUData/Producer.java

javac -Xlint -cp lib/activemq/activemq-all-5.8.0.jar RecentCPUData/*.java

javac -cp lib/hsqldb/hsqldb.jar;lib/activemq/activemq-all-5.8.0.jar HistoricalCPUData/*.java