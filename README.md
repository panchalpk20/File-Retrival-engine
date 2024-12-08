

### Requirements

For implementing your solution in Java you will need to have Java 21.x and Maven 3.8.x installed on your systems.
On Ubuntu 24.04 LTS you can install Java and Maven using the following commands:

```
sudo apt install maven openjdk-21-jdk
```

### Setup

#### How to run application
### Java solution
#### How to build/compile

To build the Java solution use the following commands:
```
cd app-java
mvn compile
mvn package
```

#### How to run application

To run the Java server (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer <port>
```

To run the Java client (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
```

To run the Java benchmark (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark <server IP> <server port> <number of clients> [<dataset path>]
```

example
```
$ java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark 127.0.0.1 12345 2 /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1 /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2
```

#### Interactive Mode:
If you do not provide command-line arguments in benchamark program, the program will prompt you for the following information:

* **Server IP :**  Enter the server's IP address.
* **Server Port :** Enter the port number the server is listening on.
* **Number of Clients:** Specify how many clients to run.
* **Dataset Path:** For each client, enter the path to the dataset file to index.

#### Example (2 clients and 1 server)

**Step 1:** start the server:

Server
```
$ java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 12345
Server started and listening on port 12345
Server >

```
sometimes server won't print `server>` on console, but it will still allow to type command on server side, like list

**Example**
```
Server > New client connected ... /127.0.0.1:52520
Server > New client connected ... /127.0.0.1:52521
Server > New client connected ... /127.0.0.1:52522
Server > New client connected ... /127.0.0.1:52523
Server > Disconnected client /127.0.0.1:52520
Disconnected client /127.0.0.1:52523
Disconnected client /127.0.0.1:52522
Disconnected client /127.0.0.1:52521
list
Currently connected clients :
```


**Step 2:** start the clients and connect them to the server:

Client 1
```
$ java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
Client > connect 127.0.0.1 12345
Connected to server at 127.0.0.1:12345
Received Client ID: client_1
Client >
```

Client 2
```
$ java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
Client > connect 127.0.0.1 12345
Connected to server at 127.0.0.1:12345
Received Client ID: client_2
Client >
```

**Step 3:** list the connected clients on the server:

Server
```
Server > list
Currently connected clients :
/127.0.0.1:34176
/127.0.0.1:41304
```

**Step 4:** index files from the clients:

Client 1
```
Client > index /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1
Completed indexing 68383239 bytes in 7.37 seconds
Client >
```

Client 2
```
Client > index /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2
Completed indexing 65864138 bytes in in 5.20 seconds
```

**Step 5:** search files from the clients:

Client 1
```
Client > search at
Searching for [at]
search result size 0
No results found.
Completed search in 0.21 seconds
```
Client 1
```
Client > search Worms
Searching for [Worms]
search result size 13
Top 10 out of 13
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document1043.txt (4 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt (4 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document10383.txt (3 occurrences)
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/Document1091.txt (3 occurrences)
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderB/Document10991.txt (2 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10657.txt (1 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10340.txt (1 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder2/Document101.txt (1 occurrences)
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder5/folderA/Document10689.txt (1 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document1051.txt (1 occurrences)
Completed search in 0.19 seconds
```

Client 2
```
Client > search distortion AND adaptation
Searching for [distortion, adaptation]
search result size 4
Top 10 out of 4
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderC/Document10998.txt (6 occurrences)
[client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10516.txt (3 occurrences)
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11157.txt (2 occurrences)
[client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11159.txt (2 occurrences)
Completed search in 0.14 seconds
```

**Step 6:** close and disconnect the clients:

Client 1
```
Client > quit
Disconnected from server.
```

Client 2
```
Client > quit
Disconnected from server.
```

**Step 7:** close the server:

Server
```
> quit
Server is shutting down.
Dispatcher terminated
```

#### Example (benchmark with 2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 12345
> Server started and listening on port 12345
Server >
```

**Step 2:** start the benchmark:

Benchmark
```
purushottam@ubu27:~/assignments/4/csc-435-pa4-panchalpk20/app-java$ java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark
No command-line arguments provided. Please enter the details:
Server IP: 127.0.0.1
Server Port: 12345
Number of Clients: 2
Enter dataset path for client 1: /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1
Enter dataset path for client 2: /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2
Connected to server at 127.0.0.1:12345
Connected to server at 127.0.0.1:12345
Received Client ID: client_1
Received Client ID: client_2
Indexed dataset at /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2: Execution Time = 91.711842602 seconds, Total Bytes Read = 65864138
Indexed dataset at /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1: Execution Time = 96.174543793 seconds, Total Bytes Read = 68383239

=== Total Execution Time: 96511 ms ===

=== Searching for: Worms ===
Searching on client/thread 1...
Searching for [Worms]
search result size 13
Search executed in: 0.653815533 seconds
Documents found: 13
Top 10 results:
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt (4 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document1043.txt (4 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/Document1091.txt (3 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document10383.txt (3 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderB/Document10991.txt (2 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11116.txt (1 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt (1 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document1051.txt (1 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder2/Document101.txt (1 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10705.txt (1 occurrences)
----------------------------
Searching on client/thread 2...
Searching for [Worms]
search result size 13
Search executed in: 0.20832426 seconds
Documents found: 13
Top 10 results:
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt (4 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document1043.txt (4 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/Document1091.txt (3 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder3/Document10383.txt (3 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderB/Document10991.txt (2 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11116.txt (1 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt (1 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document1051.txt (1 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder2/Document101.txt (1 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10705.txt (1 occurrences)
----------------------------

=== Searching for: distortion, adaptation ===
Searching on client/thread 1...
Searching for [distortion, adaptation]
search result size 4
Search executed in: 0.107518945 seconds
Documents found: 4
Top 4 results:
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderC/Document10998.txt (6 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10516.txt (3 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11159.txt (2 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11157.txt (2 occurrences)
----------------------------
Searching on client/thread 2...
Searching for [distortion, adaptation]
search result size 4
Search executed in: 0.213477957 seconds
Documents found: 4
Top 4 results:
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder7/folderC/Document10998.txt (6 occurrences)
 - [client_1] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_1/folder4/Document10516.txt (3 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11159.txt (2 occurrences)
 - [client_2] /home/purushottam/assignments/dataset_pa4/dataset1_client_server/2_clients/client_2/folder8/Document11157.txt (2 occurrences)
----------------------------

=== Searching for: at ===
Searching on client/thread 1...
Searching for [at]
search result size 0
Search executed in: 0.109619026 seconds
Documents found: 0
Top 0 results:
----------------------------
Searching on client/thread 2...
Searching for [at]
search result size 0
Search executed in: 0.070808321 seconds
Documents found: 0
Top 0 results:
----------------------------

Disconnected from server.
Disconnected from server.
purushottam@ubu27:~/assignments/4/csc-435-pa4-panchalpk20/app-java$

```

**Step 3:** close the server:

Server
```
> quit
Server is shutting down.
Dispatcher terminated
```
