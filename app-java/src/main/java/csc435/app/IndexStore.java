package csc435.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.*;

// Data structure that stores a document number and the number of times a word/term appears in the document
class DocFreqPair {
    public long documentNumber;
    public long wordFrequency;

    public DocFreqPair(long documentNumber, long wordFrequency) {
        this.documentNumber = documentNumber;
        this.wordFrequency = wordFrequency;
    }
}

public class IndexStore {
    private final Map<Long, String> documentMap;
    private final Map<String, ArrayList<DocFreqPair>> termInvertedIndex;
    private final ReadWriteLock documentMapLock;
    private final ReadWriteLock termInvertedIndexLock;

    public IndexStore() {
        // initialize the DocumentMap and TermInvertedIndex members
        documentMap = new HashMap<>();
        termInvertedIndex = new HashMap<>();
        documentMapLock = new ReentrantReadWriteLock();
        termInvertedIndexLock = new ReentrantReadWriteLock();
    }

    public long putDocument(String documentPath) {
        long documentNumber;

        documentMapLock.writeLock().lock(); // Acquire the lock for writing
        try {
            documentNumber = documentMap.size() + 1;
            documentMap.put(documentNumber, documentPath);
        } finally {
            documentMapLock.writeLock().unlock(); // lock released
        }
        return documentNumber;
    }

    public String getDocument(long documentNumber) {
        documentMapLock.readLock().lock();
        try {
            return documentMap.get(documentNumber);
        } finally {
            documentMapLock.readLock().unlock();
        }
    }

    public void updateIndex(long documentNumber, HashMap<String, Long> wordFrequencies) {
        termInvertedIndexLock.writeLock().lock(); // Acquire lock
        try {
            // Update the TermInvertedIndex with the word frequencies of the specified
            // document
            for (Map.Entry<String, Long> entry : wordFrequencies.entrySet()) {
                String term = entry.getKey();
                Long freq = entry.getValue();
                // Update index if exists else create new entry
                termInvertedIndex
                        .computeIfAbsent(term, k -> new ArrayList<>())
                        .add(new DocFreqPair(documentNumber, freq));
            }
        } finally {
            termInvertedIndexLock.writeLock().unlock(); // lock released
        }
    }

    public ArrayList<DocFreqPair> lookupIndex(String term) {
        termInvertedIndexLock.readLock().lock();
        try {
            return termInvertedIndex.getOrDefault(term, new ArrayList<>());
        } finally {
            termInvertedIndexLock.readLock().unlock();
        }
    }

    public void indexSingleFile(HashMap<String, Long> wordFrequencies, String documentPath) {
        var docNumber = putDocument(documentPath);
        updateIndex(docNumber, wordFrequencies);
        //System.out.println("index sizze "+termInvertedIndex.size());
    }
}
