
package homework1;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerHandler extends Thread
{
    public static final String INPUT_FILE="C:\\Users\\MyPro\\Documents\\NetBeansProjects\\Homework1\\src\\homework1";
    private Socket client_Socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private String fileName=null;
    private int TrialNum;
    private int gameScore;
    //private int winNum = 0;
    private String selectedWord, CurrentWord;
    boolean letterAccepted=false,userWin=false;
    int scoreCounter=0;
    


    // Default Constructor which got clientSpckets and Handle them
    public ServerHandler(Socket clientSocket)    //clientSocket comes from ClientSocket
    {
        this.client_Socket = clientSocket;   
    }

    public void run() 
    {     
        try {
            in=new BufferedInputStream(client_Socket.getInputStream());
            out=new BufferedOutputStream(client_Socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            playGame();
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String ChooseWord() throws IOException 
    {
        File file;
        FileInputStream  fileStream;      
        FileReader fileReader = null;
        BufferedReader bufferedReader;

        Random rand=new Random();
        int firstLine = 0;
        int lastLine = 25143;
        int R = rand.nextInt(lastLine-firstLine) + firstLine;
        
        String line = null;    
        // reading from words.txt   
        file = new File("Words.txt");
        try {
            fileStream=new FileInputStream(file);
            fileReader = new FileReader(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bufferedReader = new BufferedReader(fileReader);
        for (int i=0;i<R;i++) {
            line = bufferedReader.readLine();
        }   
        line = bufferedReader.readLine(); 
        System.out.println("Line :" + R + " "+line);

            return line;
    }
    
    
    //selectedWord is the word is selected from words.txt
    //dashedWord is what we want to send for client again
    public String wordChecking(String guessedWrd,String selectedWrd,String currentWrd)
    {
        String returnValue = "";
        if(guessedWrd.length()>1)     {
            if(selectedWrd.equalsIgnoreCase(guessedWrd)) {
                returnValue = "WIN";
                //gameScore++;
            } 
            else {
                returnValue = currentWrd;
            }
        }
        else if(guessedWrd.length()==1) {
            char[] charWord=selectedWrd.toCharArray();
            char[] CurrentArr=currentWrd.toCharArray();
            
            //Just to update the dashes
            for (int i = 0; i < selectedWrd.length(); i++) {
                if(charWord[i]==guessedWrd.charAt(0))   {   
                   CurrentArr[i] =guessedWrd.charAt(0);
                }
            }
            
            //To know win or not
            if(CurrentArr.toString()==selectedWrd)  {   
                returnValue = "WIN";
            }
            else {
                returnValue = CurrentArr.toString();
            }       
        }
        return returnValue;
    }        
    
         //letterAccepteed=false;
        //userWin=false;
  
           
    public String startNewGame() throws IOException
    {
      CurrentWord ="";
      selectedWord=ChooseWord();
      TrialNum=(selectedWord.length())+3;
      for(int i=0;i<selectedWord.length();i++)
      {
          CurrentWord += "-";
      }
      
      
        System.out.println("Cur word : "+CurrentWord);
       String outputToClient="ABC"+" "+String.valueOf(TrialNum);
        System.out.println("our to Client : "+outputToClient);
      return outputToClient;
    }
   
    
   
         public void sendToClient(String msgToClient)
    {
        
         //writing or send
            try{
             byte[] outputToClient=msgToClient.getBytes();
             ObjectOutputStream out =new ObjectOutputStream(this.client_Socket.getOutputStream());
             out.write(outputToClient);
             out.flush();
             ///System.out.println("Object has been sent to client from thread");
             
             } catch (IOException ex) {
                //Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                 System.out.println("IOException while sending object to client");
             }
            
            
    
    }
         
         
    public void playGame() throws IOException 
    {
        //if it is the first time that client calls server
        String firstTime=startNewGame();
        sendToClient(firstTime);
        
        while(true)
        {
            String inputFrClient=receiveFromClient();
            //we want to check the input from client
            String checkedWord=wordChecking(inputFrClient, selectedWord, CurrentWord);
            sendToClient(checkedWord);
            if ("WIN".equals(checkedWord) || "LOSE".equals(checkedWord)) break;            
        }
        //GAME END HERE
    }
         
        
    
    public String receiveFromClient()
    {
        
            String msgCln=null;
            byte[] bt = new byte[256];
            int bytesRead = 0;
            int n;
        try {
            while ((n = in.read(bt, bytesRead, 64)) != -1) {
                bytesRead += n;
                if (bytesRead == 256) {
                    break;
                }
                if (in.available() == 0) {
                    break;
                }
            }  
        }

        catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
    
                
                msgCln=new String(bt);
                msgCln=msgCln.substring(0,bytesRead);
                return msgCln;  //word which is received from Client
                
    } 

    private ServerHandler() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}
