cpuMQdemo
=========

One java process reads the cpu usage and pushes the result onto a message queue. Another two java processes reads this info from the queue (one displays data for the last 10 seconds, the other saves historical data to a database)
