package csc435.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import csc435.app.Message.MessageType;

public class IndexWorker implements Runnable {
    private IndexStore store;

    // public IndexWorker(IndexStore store) {
    // this.store = store;
    // }

    private Socket clientSocket;
    private ServerProcessingEngine engine;
    private final String clientId;

    // private PrintWriter outStream;
    // private BufferedReader inStream;

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public IndexWorker(Socket clientSocket, IndexStore store, ServerProcessingEngine engine,
            ObjectOutputStream outStream, ObjectInputStream inStream, String clientId) {
        this.clientSocket = clientSocket;
        this.store = store;
        this.engine = engine;
        this.outStream = outStream;
        this.inStream = inStream;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            Message message;
            while ((message = (Message) inStream.readObject()) != null) {

                if (message.type == MessageType.QUIT) {
                    engine.disconnetClient(clientSocket);
                    break;
                }

                // if the message is an INDEX REQUEST, then
                if (message.type == MessageType.INDEX_REQUEST) {
                    // send reply message to the client

                    store.indexSingleFile(message.wordFrequencies, "[" + clientId + "] " + message.documentPath);
                    Message indexReply = new Message();
                    indexReply.type = MessageType.INDEX_REPLY;
                    indexReply.message = "Indexing completed";
                    outStream.writeObject(indexReply);
                    // TODDO send indexReply
                    continue;
                }

                if (message.type == MessageType.SEARCH_REQUEST) {
                    List<String> termsList = message.searchTerms;
                    HashMap<Long, Long> fileOccurances = new HashMap<>();

                    for (String termString : termsList) {
                        var singleTermOccurance = store.lookupIndex(termString);
                        for (var entry : singleTermOccurance) {
                            var currDocNumber = entry.documentNumber;
                            var newFreq = entry.wordFrequency;

                            Long currentFrequency = fileOccurances.getOrDefault(currDocNumber, 0L);
                            boolean allTermsPresent = true;
                            for (String queryTerm : termsList) {
                                // look for remaining terms in same document
                                var termOcc = store.lookupIndex(queryTerm);
                                var termFound = false;
                                for (DocFreqPair docs : termOcc) {
                                    if (docs.documentNumber == currDocNumber) {
                                        termFound = true;
                                        break;
                                    }
                                }
                                if (!termFound) {
                                    allTermsPresent = false;
                                    break;
                                }
                            }
                            if (allTermsPresent) {
                                fileOccurances.put(currDocNumber, currentFrequency + newFreq);
                            }
                        }

                    }

                    // sort the document and frequency pairs and keep only the top 10
                    ArrayList<Entry<Long, Long>> sortedResults = new ArrayList<>(fileOccurances.entrySet());
                    sortedResults.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                    ArrayList<DocPathFreqPair> finalDocumentFrequencies = new ArrayList<>();
                    for (Map.Entry<Long, Long> entry : sortedResults) {
                        Long docNo = entry.getKey();
                        Long occurrences = entry.getValue();
                        finalDocumentFrequencies.add(new DocPathFreqPair(store.getDocument(docNo), occurrences));
                    }

                    Message searchRepply = new Message();
                    searchRepply.type = MessageType.SEARCH_REPLY;
                    searchRepply.documentFrequencies = finalDocumentFrequencies;
                    outStream.writeObject(searchRepply);
                    continue;
                }

                Message invalidMessage = new Message();
                invalidMessage.type = MessageType.INVALID_REQ;
                invalidMessage.message = "invalid command";
                outStream.writeObject(invalidMessage);
            }

        } catch (SocketException e) {
            System.err.println("Connection is closed with client " + clientId + ": " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println("error communicating with client " + clientId + ": " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid message from client " + clientId + ": " + e.getLocalizedMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred with client " + clientId + ": " + e.getLocalizedMessage());
        }

    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }
    
    public ObjectOutputStream getOutputStream() {
        return this.outStream;
    }
}
