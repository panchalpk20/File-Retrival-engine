package csc435.app;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1"; // Replace with your server IP
        int serverPort = 1234; // Replace with your server port

        // Hardcoded dataset paths for 1, 2, and 4 clients
        String[][][] datasetPaths = {
                { // 1 client configuration
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\1_client\\client_1"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\1_client\\client_1"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\1_client\\client_1"
                        }
                },
                { // 2 clients configuration
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\2_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\2_clients\\client_2"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\2_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\2_clients\\client_2"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\2_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\2_clients\\client_2"
                        }
                },
                { // 4 clients configuration
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\4_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\4_clients\\client_2",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\4_clients\\client_3",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset1_client_server\\dataset1_client_server\\4_clients\\client_4"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\4_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\4_clients\\client_2",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\4_clients\\client_3",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset2_client_server\\dataset2_client_server\\4_clients\\client_4"
                        },
                        {
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\4_clients\\client_1",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\4_clients\\client_2",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\4_clients\\client_3",
                                "C:\\Users\\OS\\Desktop\\data0\\dataset3_client_server\\dataset3_client_server\\4_clients\\client_4"
                        }
                }
        };

        // Run benchmarks for each client configuration
        for (int numClients = 0; numClients < datasetPaths.length; numClients++) {
            runBenchmark(serverIP, serverPort, datasetPaths[numClients], numClients + 1);
        }
    }

    private static void runBenchmark(String serverIP, int serverPort, String[][] datasetPaths, int numClients) {
        long startTime = System.currentTimeMillis();

        System.out.println("\n=== Running Benchmark for " + numClients + " Client(s) ===");

        // Create BenchmarkWorker for each dataset and run them sequentially
        for (String[] datasetPath : datasetPaths) {
            BenchmarkWorker worker = new BenchmarkWorker(serverIP, serverPort, new ArrayList<>(List.of(datasetPath)));
            worker.run();
        }

        long endTime = System.currentTimeMillis();
        System.out.println(
                "\n=== Total Execution Time for " + numClients + " Client(s): " + (endTime - startTime) + " ms ===\n");

        // Example search terms
        String[][] searchTermsArray = {
                { "Worms" },
                { "distortion", "adaptation" },
                { "at" }
        };

        for (String[] searchTerms : searchTermsArray) {
            System.out.println("=== Searching for: " + String.join(", ", searchTerms) + " ===");
            BenchmarkWorker worker = new BenchmarkWorker(serverIP, serverPort, new ArrayList<>());
            worker.run(); // This could be adjusted if you want to re-run or disconnect
            getResultForWorker(worker, new ArrayList<>(List.of(searchTerms)));
            worker.disconnect();
            System.out.println();
        }
    }

    private static void getResultForWorker(BenchmarkWorker worker, ArrayList<String> searchTerms) {
        SearchResult result = worker.search(searchTerms);
        if (result != null) {
            System.out.printf("Search executed in: %.6f seconds\n", result.excutionTime);
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
