package csc435.app;

public class FileRetrievalServer
{
    public static void main( String[] args )
    {
        // TO-DO change server port to a non-privileged port from args[0]
        int serverPort = 1; // read from cmd line arg

        if (args.length != 1) {
            System.out.println("Missing serrver port in command line argument");
            // serverPort = 1234;
            return;
        }

        try {
            serverPort = Integer.parseInt(args[0]);
            if (serverPort <= 1024 || serverPort > 65535) {
                throw new IllegalArgumentException("Port number should be between 1025 and 65535.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Port numbeer should be integer.");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        IndexStore store = new IndexStore();
        ServerProcessingEngine engine = new ServerProcessingEngine(store);
        ServerAppInterface appInterface = new ServerAppInterface(engine);
        
        // create a thread that creates and server TCP/IP socket and listenes to connections
        engine.initialize(serverPort);   // creates dispatcher threadd

        // read commands from the user
        appInterface.readCommands();
    }
}
