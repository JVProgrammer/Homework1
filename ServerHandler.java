
package homework1;
import java.awt.event.WindowEvent;
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
    private int trialNum;
    private int gameScore=0;
    private String selectedWord, CurrentWord,inputFrClient,updateToClient=null;
    
    boolean gameStatus=false;
    boolean isStillPlaying = true,isInGame=true;
    
    
    
    char[] charWord,CurrentArr,guessedChar;
    


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
        while(isStillPlaying){
            System.out.println("PLAY GAME START==========================");
            playGame();         
        }

        try {
            in.close();
            out.close();
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
             bufferedReader.readLine();
        }   
        line = bufferedReader.readLine(); 
        System.out.println("Line :" + R + " "+line);

            return line;
    }
    
    
    //selectedWord is the word is selected from words.txt
    //dashedWord is what we want to send for client again
    
    public void wordChecking()
    {
        System.out.println("Input fr client :"+inputFrClient);
        if(inputFrClient.length()>1)     {
            if(selectedWord.equalsIgnoreCase(inputFrClient)) {
                gameStatus=true;  // wins
                gameScore++;
            } 
            else {
                updateToClient = inputFrClient;
                trialNum--;
            }
        }
        else if(inputFrClient.length()==1) {
            charWord=selectedWord.toCharArray();
            CurrentArr=CurrentWord.toCharArray();
            guessedChar = inputFrClient.toCharArray();
            System.out.println("ENTER THIS SECTION " + guessedChar[0]);
            
            //Just to update the dashes
            for (int i = 0; i < selectedWord.length(); i++) {
                if(Character.toUpperCase(charWord[i])==guessedChar[0])   {   
                    CurrentArr[i] =guessedChar[0];
                }
                System.out.println(charWord[i]+" "+CurrentArr[i]+" "+guessedChar[0]);
                updateToClient=new String(CurrentArr);
            }
            CurrentWord=new String(CurrentArr);
            
            if(CurrentWord.equalsIgnoreCase(selectedWord))  {   
                gameStatus=true;
                gameScore++;
                
            }
            else{
                trialNum--;
            }
            }
                
        
        System.out.println("Update value: "+updateToClient);
        
    }        

           
    public void startNewGame() throws IOException
    {
      CurrentWord ="";
      selectedWord=ChooseWord();
      trialNum=(selectedWord.length())+3;
      for(int i=0;i<selectedWord.length();i++)
      {
          CurrentWord += "-";
      }
      
        sendToClient(CurrentWord);
     
     
    }
   
    
   
        public void sendToClient(String msgToClient)
        {
            System.out.println("SendToClient " + msgToClient);
         //writing or send
            try{
            String delimiter ="#";
            updateToClient=msgToClient+delimiter+trialNum+delimiter+gameScore;
            byte[] outputToClient=(updateToClient).getBytes();
             
            
             out.write(outputToClient);
             out.flush();
             ///System.out.println("Object has been sent to client from thread");
             
             } catch (IOException ex) {
                //Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                 System.out.println("IOException while sending object to client");
             }
            
            
    
    }
         
         
    public void playGame() 
    {
         
        try {
            //if it is the first time that client calls server
            startNewGame();
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        while(isInGame)
        {
            inputFrClient=receiveFromClient();
            System.out.println("Guess from client : "+inputFrClient);
            //we want to check the input from client
            //trialNum--;
            if (inputFrClient=="NEWGAME") {
                try {
                    //isStillPlaying=true;
                    isInGame=false;
                    in.close();
                    out.close();
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (inputFrClient=="ENDGAME") {
                try {
                    isStillPlaying=false;
                    isInGame=false;
                    in.close();
                    out.close();
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                wordChecking();
                sendToClient(updateToClient);
            }

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
                System.out.println(msgCln);
                return msgCln;  //word which is received from Client
                
    } 


  
}
