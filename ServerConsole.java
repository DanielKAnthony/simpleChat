// This class implements the modification required in Exercise 2
// to a give a global messaging feature to the server-side user
import common.*;
import client.*;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements the ChatIF user-interface functionality
 * for the server-side end user to communicate with all connected clients.
 * Its structure mimics the ClientConsole implementation of ChatIF.
 * @author Daniel Krohn-Anthony
 */
public class ServerConsole implements ChatIF{

    //Class variables *************
    /**
     * Default port for the server to listen on.
     * Can be overridden with command line arg.
     */
    final public static int DEFAULT_PORT = 5555;
    /**
     * ChatClient object to call the display() method
     */
    ChatClient client;
    /**
     * Scanner object to receive server-side user input
     */
    Scanner serverIO;

    /**
     * Constructs a ServerConsole instance.
     * @param host host address
     * @param port port to listen on
     */
    public ServerConsole(String host, int port){
        try{
            client = new ChatClient(host,port,this);
        }catch(IOException e){
            System.out.println("Error: could not start server");
        }
        serverIO = new Scanner(System.in);
    }
    
    /**
     * Listens for end user input. Sends messages with a 
     * concatonated SERVER MSG> string to notify users that the
     * message orginates from the server.
     */
    void accept(){
        try{
            String message;

            while(true){
                message = serverIO.nextLine();
                client.handleMessageFromClientUI("SERVER MSG> " + message);
            }
        }catch (Exception e) {
          System.out.println
            ("Unexpected error while reading from console!");
        }
    }

    /**
     * Implemantion of ChatIF abstract method.
     * Displays all messages sent to the server on the console.
     * This method only behaves in a local scope, and is why the
     * SERVER MSG> string was added in the accept() method.
     */
    @Override
    public void display(String message){
        System.out.println("> " + message);
    }

    /**
     * This method starts the server and the server-side UI.
     * @param args an optional port argument to specify the port number
     * on which both the server and server console will listen.
     */
    public static void main(String[] args){
        String host = "localhost";
        int port = 0;

        try
        {
            port = Integer.parseInt(args[0]);
        }catch(Exception e){
            port = DEFAULT_PORT;
        }

        EchoServer.main(new String[] {Integer.toString(port)}); // start server
        
        ServerConsole chat = new ServerConsole(host, port);
        chat.accept();  //Enable server-side messaging
  }
}