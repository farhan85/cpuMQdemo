cpuMQdemo
=========

One java process reads the cpu usage and pushes the result onto a message queue.
Another two java processes reads this info from the queue (one displays data for
the last 10 seconds, the other saves historical data to a database)

Run build.bat (build.sh) to build all the java binaries.

There are separate run_*.bat (run_*.sh) files to run the java programs.

This demo consists of the following java programs:
 - ProduceCPUData: Reads CPU usage and places them onto a messaging queue (every 10 seconds)
 - RecentCPUData: Displays the CPU usage for the last 60 seconds
 - HistoricalCPUData: Stores all historical CPU usage data into a database
