package csc435.app;

public class FileRetrievalClient
{
    public static void main(String[] args)
    {
        //no access to index store
        ClientProcessingEngine engine = new ClientProcessingEngine();
        ClientAppInterface appInterface = new ClientAppInterface(engine);
        
        // read commands from the user

        appInterface.readCommands();
    }
}
