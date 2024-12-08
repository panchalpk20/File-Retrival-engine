package csc435.app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import csc435.app.Message.MessageType;

public class ServerProcessingEngine {
    private IndexStore store;
    private Dispatcher dispatcher;
    private Thread dispThread;
    private boolean terminate = false;
    private List<Thread> workerThreads = new CopyOnWriteArrayList<>();
    private List<IndexWorker> IndexWorkers = new CopyOnWriteArrayList<>();
    private List<Socket> clientSockets = new CopyOnWriteArrayList<>();
    private AtomicInteger clientIdCounter = new AtomicInteger(1);

    public ServerProcessingEngine(IndexStore store) {
        this.store = store;
    }

    public void initialize(int serverPort) {
        // TO-DO create and start the Dispatcher thread
        dispatcher = new Dispatcher(this, serverPort);
        dispThread = new Thread(dispatcher);
        dispThread.start();
    }

    public void spawnWorker(Socket clientSocket) throws IOException {
        // creating index worker for each client connected

        var outStream = new ObjectOutputStream(clientSocket.getOutputStream());
        var inStream = new ObjectInputStream(clientSocket.getInputStream());

        String clientId = "client_" + clientIdCounter.getAndIncrement();

        // System.out.println("Client connected " + clientId);
        Message connectReply = new Message();
        connectReply.clientId = clientId;
        connectReply.message = "Connected successfully";
        outStream.writeObject(connectReply);
        IndexWorker worker = new IndexWorker(clientSocket, store, this, outStream, inStream, clientId);
        Thread workerThread = new Thread(worker);
        IndexWorkers.add(worker);// futher used to get outstream for shutdown

        workerThreads.add(workerThread);
        workerThread.start();
        clientSockets.add(clientSocket);
        // System.out.println("[debug] Worker created for client - " +
        // clientSocket.getInetAddress());
        System.out.print("Server > ");

        //
    }

    public void shutdown11() {
        // disconnet client first
        try {
            for (Socket clientSocket : clientSockets) {
                // Retrieve the worker associated with this client socket
                for (IndexWorker w : IndexWorkers) {
                    if (w.getClientSocket() == clientSocket) {
                        ObjectOutputStream outStream = w.getOutputStream();
                        Message disconnectMessage = new Message();
                        disconnectMessage.clientId = "Server";
                        disconnectMessage.message = "Server is shutting down. Disconnecting...";
                        disconnectMessage.type = MessageType.SERVER_SHUTDOWN;

                        outStream.writeObject(disconnectMessage);
                        outStream.flush();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Client is closed " + e.getLocalizedMessage());
        }
        dispatcher.terminate();
        try {
            dispThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread inturrrupted - ");
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        // disconnet client first
        for (Socket clientSocket : clientSockets) {
            try {
                //sendingg messages to all connected client that server is shutting down
                ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
                Message disconnectMessage = new Message();
                disconnectMessage.clientId = "Server";
                disconnectMessage.message = "Server is shutting down. Disconnecting...";
                disconnectMessage.type = MessageType.SERVER_SHUTDOWN;
                outStream.writeObject(disconnectMessage);
                outStream.flush();
                // clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //wait for one second so that all clients can disconnect from server
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        // Now close all client sockets
        for (Socket clientSocket : clientSockets) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //signal the Dispatcher thread to shutdown
        terminate = true;
        dispatcher.terminate();

        // TO-DO join the Dispatcher and Index Worker threads

        try {
            dispThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread inturrrupted - ");
                e.printStackTrace();
            }
        }
    }


    public ArrayList<String> getConnectedClients() {
        ArrayList<String> clientInfoList = new ArrayList<>();
        for (Socket clientSocket : clientSockets) {
            clientInfoList.add(clientSocket.getRemoteSocketAddress().toString());
        }
        return clientInfoList;
    }

    public void disconnetClient(Socket clientSocket) {
        try {
            clientSocket.close();
            clientSockets.remove(clientSocket);
            System.out.println("Disconnected client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            
        } catch (SocketException e) {
            System.err.println("socket is already closed: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println("Unable to close client connection: " + e.getLocalizedMessage());
        }
    }
}
