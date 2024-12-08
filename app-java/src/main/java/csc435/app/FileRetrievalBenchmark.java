package csc435.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class BenchmarkWorker implements Runnable {
    private String serverIP;
    private int serverPort;
    private ArrayList<String> clientsDatasetPath;
    private ClientProcessingEngine engine;

    // Constructor
    public BenchmarkWorker(String serverIP, int serverPort, ArrayList<String> clientsDatasetPath) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientsDatasetPath = clientsDatasetPath;
    }

    @Override
    public void run() {
        // Create a ClientProcessingEngine
        engine = new ClientProcessingEngine();

        // Connect the ClientProcessingEngine to the server
        engine.connect(serverIP, serverPort);

        // Index the dataset
        for (String datasetPath : clientsDatasetPath) {
            IndexResult indexResult = engine.indexFiles(datasetPath);
            if (indexResult != null) {
                System.out.println("Indexed dataset at " + datasetPath + ": " +
                        "Execution Time = " + indexResult.executionTime + " seconds, " +
                        "Total Bytes Read = " + indexResult.totalBytesRead);
            }
        }
    }

    public SearchResult search(ArrayList<String> searchTerms) {
        return engine.searchFiles(searchTerms);
    }

    public void disconnect() {
        if (engine != null) {
            engine.disconnect();
        }
    }
}

public class FileRetrievalBenchmark {
    public static void main(String[] args) {
        String serverIP;
        int serverPort;
        int numberOfClients;
        ArrayList<ArrayList<String>> clientsDatasetPaths = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        if (args.length < 3) {
            System.out.println("No command-line arguments provided. Please enter the details:");

            System.out.print("Server IP: ");
            serverIP = scanner.nextLine();

            System.out.print("Server Port: ");
            serverPort = Integer.parseInt(scanner.nextLine());

            System.out.print("Number of Clients: ");
            numberOfClients = Integer.parseInt(scanner.nextLine());

            for (int i = 0; i < numberOfClients; i++) {
                System.out.print("Enter dataset path for client " + (i + 1) + ": ");
                String path = scanner.nextLine();
                ArrayList<String> clientPaths = new ArrayList<>();
                clientPaths.add(path);
                clientsDatasetPaths.add(clientPaths);
            }
        } else {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
            numberOfClients = Integer.parseInt(args[2]);

            for (int i = 3; i < args.length; i++) {
                ArrayList<String> clientPaths = new ArrayList<>();
                clientPaths.add(args[i]);
                clientsDatasetPaths.add(clientPaths);
            }
        }

        long startTime = System.currentTimeMillis();

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<BenchmarkWorker> workers = new ArrayList<>();

        for (int i = 0; i < numberOfClients; i++) {
            BenchmarkWorker worker = new BenchmarkWorker(serverIP, serverPort, clientsDatasetPaths.get(i));
            Thread thread = new Thread(worker);
            threads.add(thread);
            workers.add(worker);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTimeMillis = endTime - startTime;
        double totalTimeSeconds = totalTimeMillis / 1000.0; 
        System.out.println("\n=== Total Execution Time: " + totalTimeSeconds + " seconds ===\n");

        String[][] searchTermsArray = {
                { "Worms" },
                { "distortion", "adaptation" },
                { "at" }
        };

        for (String[] searchTerms : searchTermsArray) {
            System.out.println("=== Searching for: " + String.join(", ", searchTerms) + " ===");
            for (BenchmarkWorker worker : workers) {
                System.out.println("Searching on client/thread " + (workers.indexOf(worker) + 1) + "...");
                getResultForWorker(worker, new ArrayList<>(List.of(searchTerms)));
            }
            System.out.println();
        }

        for (BenchmarkWorker worker : workers) {
            worker.disconnect();
        }

        scanner.close();
    }

    private static void getResultForWorker(BenchmarkWorker worker, ArrayList<String> searchTerms) {
        SearchResult result = worker.search(searchTerms);
        if (result != null) {
            // System.out.println("Search executed in: " + result.excutionTime + "
            // seconds");
            System.out.println(String.format("Search executed in: %.6f seconds", result.excutionTime));

            System.out.println("Documents found: " + result.documentFrequencies.size());
            int limit = Math.min(10, result.documentFrequencies.size());
            System.out.println("Top " + limit + " results:");
            for (DocPathFreqPair res : result.documentFrequencies.subList(0, limit)) {
                System.out.printf(" - %s (%d occurrences)\n", res.documentPath, res.wordFrequency);
            }
            System.out.println("----------------------------");
        } else {
            System.out.println("No results found.");
            System.out.println("----------------------------");
        }
    }
}
