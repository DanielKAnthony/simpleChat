// This class implements the modification required in Exercise 2
// to a give a global messaging feature to the server-side user
import common.*;
import client.*;
import ocsf.server.*;
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

    EchoServer echoServer;

    /**
     * Constructs a ServerConsole instance.
     * @param host host address
     * @param port port to listen on
     */
    public ServerConsole(String host, int port){
        echoServer = new EchoServer(port);
        try{
            echoServer.listen(); 
        }catch(IOException e){
            System.out.println("Error2: could not start server");
        }

        try{
            client = new ChatClient(host,port,this);
        }catch(IOException e){
            System.out.println("Error1: could not start server");
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

                if(message.startsWith("#")){
                    handleClientFunctions(message);
                    continue;
                }

                // avoid sending message while server is closed
                // which would terminate the program by default
                if(client == null) continue;
                if(!client.isConnected()){
                    System.out.println(
                        "Failed to send message - server is not connected");
                }

                client.handleMessageFromClientUI("SERVER MSG> " + message);
            }
        }catch (Exception e) {
          System.out.println
            ("Unexpected error while reading from console!");
        }
    }

    private void handleClientFunctions(String msg){
        if(msg.equals("#quit")){
            try{
                echoServer.close();
            }catch(IOException e){
                System.out.println("Error: could not close server");
            }

        }else if(msg.equals("#stop")){
            if(!echoServer.isListening()){
                System.out.println("Server already stopped");
                return;
            }
            echoServer.stopListening();
            
        }else if(msg.equals("#close")){
            echoServer.stopListening();
            client = null;
            try{
                
                echoServer.close();
            }catch(IOException e){}

            System.out.println("Server closed");
            
        }else if(msg.startsWith("#setport")){
            if(echoServer.isListening()){
                System.out.println("Cannot set port while server is listening");
                return;
            }

            if(msg.split(" ").length < 2){
                System.out.println("1 argument required: #setport <port>");
                return;
            }

            try{
                int pt = Integer.parseInt(msg.split(" ")[1]);
                echoServer.setPort(pt);
                System.out.println("Port set to " + echoServer.getPort());
            }catch(NumberFormatException e){
                System.out.println("Error: <port> argument must be an integer");
            }

        }else if(msg.equals("#start")){
            if(echoServer.isListening()){
                System.out.println("Server is already listening for connections");
                return;
            }

            try{
                echoServer.listen();
            }catch(IOException e){
                System.out.println("Could not start server");
            }

        }else if(msg.equals("#getport")){
            System.out.println("Server running on port " + echoServer.getPort());
            
        }else
            System.out.println("Invalid command: '"+msg+"' is not recognized");
      }

    /**
     * Implemention of ChatIF abstract method.
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

        ServerConsole chat = new ServerConsole(host, port);
        chat.accept();  //Enable server-side messaging
  }
}