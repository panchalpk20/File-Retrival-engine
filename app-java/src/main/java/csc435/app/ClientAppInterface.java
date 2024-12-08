package csc435.app;

import java.lang.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClientAppInterface {
    private ClientProcessingEngine engine;

    public ClientAppInterface(ClientProcessingEngine engine) {
        this.engine = engine;
    }

    public void readCommands() {
        // implement the read commands method
        Scanner sc = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("Client > ");

            // read from command line
            command = sc.nextLine();

            // if the command is quit, terminate the program
            if (command.compareTo("quit") == 0) {
                // disconnect form server
                engine.disconnect();
                break;
            }

            // if the command begins with connect, connect to the given server
            if (command.toLowerCase().startsWith("connect") && command.trim().length() > 7 ) {
                // TO-DO parse command cand call connect on the processing engine
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    String serverIP = parts[1];
                    int serverPort;
                    try {
                        serverPort = Integer.parseInt(parts[2]);
                        engine.connect(serverIP, serverPort);
                    } catch (NumberFormatException e) {
                        System.out.println("port number must be a valid integer.");
                    }
                } else {
                    System.out.println("invalid command, try: connect <server_ip> <server_port>");
                }
                continue;
            }

            // if the command begins with index, index the files from the specified
            // directory
            if (command.toLowerCase().startsWith("index")&& command.trim().length() > 5) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    String directoryPath = parts[1];
                    IndexResult res = engine.indexFiles(directoryPath);
                    if (res == null) {
                        continue;
                    }
                    long totalBytes = res.totalBytesRead;
                    double totalTime = res.executionTime;
                    System.out.println("Completed indexing "+ totalBytes +" in " + String.format("%.2f", totalTime) + " seconds");
                } else {
                    System.out.println("invalid command, try: index <directory_path>");
                }
                continue;
            }

            // if the command begins with search, search for files that matches the query
            if (command.toLowerCase().startsWith("search")&& command.trim().length() > 6) {

                ArrayList<String> termList = new ArrayList<>(Arrays.asList(command.split(" ")));
                termList.removeIf(term -> term.equalsIgnoreCase("search") || term.equals("AND"));
                SearchResult res = engine.searchFiles(termList);

                if (res == null || res.documentFrequencies.isEmpty() || res.documentFrequencies == null) {
                    System.out.println("No results found.");
                    continue;
                } else {
                    System.out.println("Top 10 out of " + res.documentFrequencies.size());
                    int limit = Math.min(10, res.documentFrequencies.size());
                    for (DocPathFreqPair result : res.documentFrequencies.subList(0, limit)) {
                        System.out.println(result.documentPath + " (" + result.wordFrequency + " occurrences)");
                    }
                }

                System.out.println("Completed search in " + String.format("%.2f", res.excutionTime)+ " seconds");

                continue;
            }

            System.out.println("unrecognized command!");
        }

        sc.close();
    }
}
