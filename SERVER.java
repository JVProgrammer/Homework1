
package homework1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{
    private ServerSocket serverSocket=null;
    private static final int port=1234;
    boolean listening = true;

    public static void main(String[] args) throws IOException
    {
        
       new Server();

    }
    
    public Server()
    {
        
        
       connection_crt();
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

