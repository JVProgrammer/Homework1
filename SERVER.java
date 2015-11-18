
package homework1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{
    private ServerSocket serverSocket=null;
    private static final int port=4444;
    boolean listening = true;

    public static void main(String[] args) throws IOException
    {   
       new Server();
    }
    
    public Server()
    {
       /* try {
            //String content = new Scanner(new File("Words.txt")).useDelimiter("\\Z").next();
            String content = new Scanner(new File("Words.txt")).next();
            System.out.println(content);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
       connection_crt();
       Listener_crt();
    }
    
    public void connection_crt(){   
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
      
   }
   // This method listen to clients 
   public void Listener_crt(){
       try {
       while(listening)
           
       {
           
               Socket clientSocket=serverSocket.accept();
               System.out.println("Client connected");
               new Thread(new ServerHandler(clientSocket)).start();
               
             
           } 
           serverSocket.close();
       }
           
           catch (IOException ex) {
              System.err.println("Could not listen on port: " + port);
              System.exit(1);
           }
           
           
       }
   }

