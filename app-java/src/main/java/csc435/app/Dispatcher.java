package csc435.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Dispatcher implements Runnable {
    private ServerProcessingEngine engine;
    private int port = -1;
    // Flag for terminating the dispatcher
    private boolean terminate = false;
    ServerSocket serverSocket;

    public Dispatcher(ServerProcessingEngine engine, int port) {
        this.engine = engine;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started and listening on port " + port);

            while (!terminate) {
                try {

                    Socket clientSocket = serverSocket.accept(); // Accept new connections
                    System.out.println(
                            "New client connected ... " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    engine.spawnWorker(clientSocket);
                } catch (SocketException e) {
                    System.err.println("Connection is closed with client");
                } catch (IOException e) {
                    if (terminate) {
                        System.out.println("Server is shutting down.");
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Perform cleanup if necessary when terminating
            System.out.println("Dispatcher terminated");
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Socket is closing...");
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminate = true;
    }
}
