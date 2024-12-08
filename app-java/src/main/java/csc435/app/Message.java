package csc435.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

    public enum MessageType {
        INDEX_REQUEST,
        INDEX_REPLY,
        SEARCH_REQUEST,
        SEARCH_REPLY,
        QUIT,
        END_OF_SEARCH, 
        INVALID_REQ, SERVER_SHUTDOWN
    }

    public MessageType type;
    public String documentPath;
    public String clientId;
    public String message;
    public HashMap<String, Long> wordFrequencies;
    public ArrayList<String> searchTerms;
    public ArrayList<DocPathFreqPair> documentFrequencies; // For search reply results

    @Override
    public String toString() {
        return "Message [type=" + type +
                ", documentPath=" + documentPath +
                ", clientId=" + clientId +
                ", wordFrequencies=" + wordFrequencies +
                ", searchTerms=" + searchTerms +
                ", documentFrequencies=" + documentFrequencies + "]";
    }
}
