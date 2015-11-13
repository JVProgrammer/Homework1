# Homework1

package homework1;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerHandler extends Thread
{
    private Socket client_Socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private String fileName=null;
    private int TrialNum;
    private boolean gameStatus;
    //private int winNum = 0;
    private String selectedWord, dashedWord;
    boolean letterAccepted=false,userWin=false;
    int scoreCounter=0;
    


    // Default Constructor which got clientSpckets and Handle them
    public ServerHandler(Socket clientSocket)    //clientSocket comes from ClientSocket
    {
        this.client_Socket = clientSocket;
        
        try {
            in=new BufferedInputStream(client_Socket.getInputStream());
            out=new BufferedOutputStream(client_Socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void run()
    {
       
         
     
        
       
    }
    
    
    
    public String ChooseWord() 
    {
        // The name of the file to open.
        fileName = "Words.txt";
        String line = null;
        FileReader fileReader;
        BufferedReader bufferedReader = null;
      // reading from words.txt  
     try{
         fileReader=new FileReader(fileName);
         bufferedReader = new BufferedReader(fileReader);
            try {
                line=bufferedReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
     }  catch (FileNotFoundException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    //saving file words at list
    List<String> words = new ArrayList<String>();
    while(line != null) {
        String[] wordsLine = line.split(" ");
        for(String word : wordsLine) {
            words.add(word);
        }
            try {
                line = bufferedReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
       // choosing random words from list
    Random rand = new Random(System.currentTimeMillis());
    String randomWord = words.get(rand.nextInt(words.size()));
     return randomWord;
     
    }
    

    //selectedWord is the word is selected from words.txt
    //dashedWord is what we want to send for client again
    public String wordChecking(String guessedWrd,String selectedWrd,String hideWord)
    {
        
        if(guessedWrd.length()>1)
        {
            if(selectedWrd.equalsIgnoreCase(guessedWrd))
                {
            
                    hideWord=guessedWrd;
                    this.gameStatus=true;
                }
            else
                {
                    this.gameStatus=false;
                }
            
        }
        else if(guessedWrd.length()==1)
        {
            char[] dashedArr=hideWord.toCharArray();
            char[] charWord=selectedWrd.toCharArray();
            for (int i = 0; i < selectedWrd.length(); i+=2) {
                if(charWord[i]==guessedWrd.charAt(0))
                {
                    dashedArr[i*2]=guessedWrd.charAt(0);
                }
            }
           hideWord=dashedArr.toString();
        }
        return hideWord;
    }
         //letterAccepteed=false;
        //userWin=false;
  
           
    public String startNewGame()
    {
      this.selectedWord=ChooseWord();
      this.TrialNum=(ChooseWord().length())+3;
      for(int i=0;i<selectedWord.length();i++)
      {
          dashedWord += "_ ";
      }
      return dashedWord;
    }
   
    
   
         public void sendToClient(byte[] msgToClient)
    {
        
         //writing or send
            try{
                //System.out.println("Sending at top try");
             ObjectOutputStream out =new ObjectOutputStream(this.client_Socket.getOutputStream());
             out.write(msgToClient);
             out.flush();
             ///System.out.println("Object has been sent to client from thread");
             
             } catch (IOException ex) {
                //Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                 System.out.println("IOException while sending object to client");
             }
    
    }
         
         
        
    
    public String receiveMsgFromClient()
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
    public void playGame()
    {
        
    }
    
    
    
    
    
    
}
