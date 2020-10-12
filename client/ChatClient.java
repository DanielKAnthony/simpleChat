// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;
import java.net.Socket;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      //Check for special commands
      if(message.startsWith("#")){
        handleClientFunctions(message);
        return;
      }
      
      sendToServer(message);

      // attempt to create a temporary socket object -- if it
      // fails, then terminate the client
      try{
        Socket temp = new Socket(getHost(),getPort());
        temp.close();
      }catch(Exception e){
        connectionException(e);
      }

    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleClientFunctions(String msg){
    if(msg.equals("#quit")){
      disconnectFromServer(); //see method definition below this method
      System.out.println("Quitting the program");
      quit();

    }else if(msg.equals("#logoff")){
      disconnectFromServer();

    }else if(msg.startsWith("#sethost") || msg.startsWith("#setport")){

      boolean isPort = msg.startsWith("#setport");

      if(isConnected()){
        System.out.println("You must log out (#logoff) before setting a " +
        (isPort ? "port" : "host"));
        return;
      }

      if(msg.split(" ").length < 2){
        System.out.println("1 argument required: " + 
        (isPort ? "#setport <port> - No port specified" :
        "#sethost <host> - No host specified"));
        return;
      }

      if(!isPort){
        String newHost = msg.split(" ")[1];
        setHost(newHost);
        System.out.println("Set host to "+newHost);
        return;
      }

      try{
        int newPort = Integer.parseInt(msg.split(" ")[1]);
        setPort(newPort);
        System.out.println("Set port to "+Integer.toString(newPort));
      }catch(NumberFormatException e){
        System.out.println("Error: port must be an integer");
      }

    }else if(msg.equals("#login")){
      
      if(isConnected()){
        System.out.println(
          "You first must log out (#logoff) before you can log in again");
          return;
      }

      try{
        openConnection();
      }catch(IOException e){
        System.out.println("Couldn't connect to the server. Try again.");
      }
      
    }else if(msg.equals("#gethost") || msg.equals("#getport")){
      boolean isPort = msg.equals("#getport");

      System.out.println("Current " + (isPort ? "port: " + getPort() :
      "host: " + getHost()));

    }else
      System.out.println("Invalid command: '"+msg+"' is not recognized");
  }

  private void disconnectFromServer(){
    try{
      System.out.println("\nDisconnecting from the server...");
      closeConnection();
      System.out.println("Disconnected successfully");

    }catch(IOException e){
      System.out.println("Unable to disconnect from server");
    }
  }

  @Override
  protected void connectionException(Exception exception){
    System.out.println("The server has shut down. Quitting.");
    quit();
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
