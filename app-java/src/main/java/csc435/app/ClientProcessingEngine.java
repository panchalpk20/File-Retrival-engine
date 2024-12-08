package csc435.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import csc435.app.Message.MessageType;

class IndexResult {
    public double executionTime;
    public long totalBytesRead;

    public IndexResult(double executionTime, long totalBytesRead) {
        this.executionTime = executionTime;
        this.totalBytesRead = totalBytesRead;
    }
}

class DocPathFreqPair implements Serializable {
    public String documentPath;
    public long wordFrequency;

    public DocPathFreqPair(String documentPath, long wordFrequency) {
        this.documentPath = documentPath;
        this.wordFrequency = wordFrequency;
    }
}

class SearchResult {
    public double excutionTime;
    public ArrayList<DocPathFreqPair> documentFrequencies;

    public SearchResult(double executionTime, ArrayList<DocPathFreqPair> documentFrequencies) {
        this.excutionTime = executionTime;
        this.documentFrequencies = documentFrequencies;
    }
}

public class ClientProcessingEngine {
    // TO-DO keep track of the connection (socket)
    // craete variable to keep track of sockect and intialize in connect method
    // below
    private Socket socket;
    // private PrintWriter outStream;
    // private BufferedReader inStream;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    String clientId;

    public ClientProcessingEngine() {
    }

    public IndexResult indexFiles(String folderPath) {
        if (outStream == null && inStream == null) {
            System.out.println("Unable to perform indexing, Not connected to any server");
            return null;
        }
        IndexResult result = new IndexResult(0.0, 0);
        // TO-DO get the start time
        long startTime = System.nanoTime();
        long totalBytes = 0L;

        if (Paths.get(folderPath).isAbsolute()) {
            folderPath = Paths.get(folderPath).toAbsolutePath().normalize().toString();
        }

        ArrayList<File> subDirectories = new ArrayList<>();
        File mainFolder = new File(folderPath);
        subDirectories.add(mainFolder);
        ArrayList<File> finalFilesList = new ArrayList<>();

        while (!subDirectories.isEmpty()) {
            File currDirectory = subDirectories.remove(0);
            File[] flist = currDirectory.listFiles();
            if (flist != null) {
                for (File file : flist) {
                    if (file.isFile()) {
                        finalFilesList.add(file);
                        totalBytes += file.length();
                    } else {
                        subDirectories.add(file);
                    }
                }
            }
        }

        if (finalFilesList.isEmpty()) {
            System.out.println("No files at location " + folderPath);
            return null;
        }

        for (File file : finalFilesList) {
            HashMap<String, Long> wordFrequencies = new HashMap<>();
            wordFrequencies = ExtractWordFreq(file, totalBytes);
            Message indexRequest = new Message();
            indexRequest.type = MessageType.INDEX_REQUEST;
            indexRequest.documentPath = file.getPath();
            indexRequest.clientId = "clientId";
            indexRequest.wordFrequencies = wordFrequencies;
            // System.out.println("Index Req = " + indexRequest.documentPath);
            // System.out.println();
            try {
                outStream.writeObject(indexRequest);
                outStream.flush();
            } catch (SocketException e) {
                System.err.println("Indexing incomplete, Connection is closed with server please connect again");
                break;
            } catch (IOException e) {
                System.err.println("Indexing incomplete, error communicating with server: " + e.getLocalizedMessage());
                break;
            } catch (Exception e) {
                System.err.println("Indexing incomplete, An unexpected error occurred with client: " + e.getLocalizedMessage());
                break;
            }
        }

        ArrayList<Message> list = new ArrayList<>();
        for (int i = 0; i < finalFilesList.size(); i++) {
            try {
                Message response = (Message) inStream.readObject(); // Assuming input is an ObjectInputStream
                // System.out.println("[debug] size of index res " + response.message);
                // //indexing complted
                list.add(response);
            } catch (SocketException e) {
                System.err.println("Connection is closed with server please connect again");
            } catch (IOException e) {
                System.err.println("error communicating with server: " + e.getLocalizedMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Invalid message from client: " + e.getLocalizedMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred with client: " + e.getLocalizedMessage());
            }
        }

        // System.out.println("[debug] res list size " + list.size());

        long stopTime = System.nanoTime();
        double executionTime = (stopTime - startTime) / 1000000000.0; // Convert to milliseconds
        result.executionTime = executionTime;
        result.totalBytesRead = totalBytes;

        return result;

    }

    private HashMap<String, Long> ExtractWordFreq(File file, long totalBytes) {
        HashMap<String, Long> wordFrequencies = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[^\\p{IsAlphabetic}\\p{IsDigit}]+");
                for (String word : words) {
                    if (word.length() > 2) {
                        wordFrequencies.put(word,
                                wordFrequencies.getOrDefault(word, 0L) + 1);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return wordFrequencies;
    }

    public SearchResult searchFiles(ArrayList<String> terms) {
        if (outStream == null && inStream == null) {
            System.out.println("Unable to perform, Not connected to any server");
            return null;
        }
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        long startTime = System.nanoTime();

        // Prepare SEARCH REQUEST message
        Message searchRequest = new Message();
        searchRequest.type = MessageType.SEARCH_REQUEST;
        searchRequest.clientId = "client 1";
        searchRequest.searchTerms = terms;
        System.out.println("Searching for " + terms);

        try {

            // outStream.println(searchRequest);
            outStream.writeObject(searchRequest);
            outStream.flush();

            Message response = (Message) inStream.readObject();
            System.out.println("search result size " + response.documentFrequencies.size());
            result.documentFrequencies = response.documentFrequencies;

        } catch (SocketException e) {
            System.err.println("Connection is closed with server please connect again");
        } catch (IOException e) {
            System.err.println("error communicating with server: " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid message from client: " + e.getLocalizedMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred with client: " + e.getLocalizedMessage());
        }

        long stopTime = System.nanoTime();
        result.excutionTime = (stopTime - startTime) / 1000000000.0;

        return result;
    }

    // public void connect(String serverIP, String serverPort) {
    public void connect(String serverIP, int serverPort) {
        try {
            socket = new Socket(serverIP, serverPort);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            Message serverConnectResponce = (Message) inStream.readObject();
            clientId = serverConnectResponce.clientId;
            System.out.println("Connected to server at " + serverIP + ":" + serverPort);
            System.out.println("Received Client ID: " + clientId);

            // new Thread(this::listenForMessages).start();

        } catch (java.net.ConnectException e) {
            System.err.println("unable to connect to server at " + serverIP + ":" + serverPort + ". " +
                    "Connection refused. Please check if the server is running and the IP/port are correct.");
        } catch (IOException e) {
            System.err.println("Unable to connect to server at " + serverIP + ":" + serverPort + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid server response object to connect request, unable to parse client ID - " +
                    e.getLocalizedMessage());
        }
    }

    private boolean terminate;

    private void listenForMessages() {
        try {
            Message message = (Message) inStream.readObject();
            if (message.type.equals(MessageType.SERVER_SHUTDOWN)) {
                System.out.println("Received shutdown message from server " + message.message);
                while (!terminate) {
                    System.out.println("Server is shutting down. Disconnecting...");
                    terminate = true;
                    disconnect();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (terminate) {
                System.out.println("Client disconnected.");
            } else {
                e.printStackTrace();
            }
        }
    }

    // public void disconnect() {
    // try {
    // if (outStream != null) {
    // var quitMessage = new Message();
    // quitMessage.type = MessageType.QUIT;
    // quitMessage.message = "Disconnecting from server";
    // outStream.writeObject(quitMessage);
    // outStream.flush();
    // }
    // if (socket != null) {
    // socket.close();
    // System.out.println("Disconnected from server.");
    // }
    // } catch (SocketException e) {
    // System.err.println("SocketException: " + e.getLocalizedMessage());
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    public void disconnect() {
        try {
            if (outStream != null) {
                var quitMessage = new Message();
                quitMessage.type = MessageType.QUIT;
                quitMessage.message = "Disconnecting from server";
                outStream.writeObject(quitMessage);
                outStream.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending quit message: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    System.out.println("Disconnected from server.");
                }
            } catch (Exception e) {
                System.err.println("Error closing socket: " + e.getLocalizedMessage());
            }
        }
    }

}
