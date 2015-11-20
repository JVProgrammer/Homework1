package homework1;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Gregorius Kristian Purwidi
 * @author Zahra Soleymanzadeh
 */
public class ClientMain extends JFrame implements ActionListener {

    Scanner userInput = new Scanner(System.in);

    private String serverAddressInput;
    private String serverPortInput;
    private String msg;
    private int serverPortInt;
    String[] AfterSplit;

    public BlockingQueue queue = new ArrayBlockingQueue(256);

    private final int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    //private JPanel mainPanel;
    //private Label scoreLabel, wordLabel, attemptLabel, guessHistorylbl;
    //private JTextField guessField;
    //private JButton submit;

    String score, newscore, word, availableAttempt, gameStatus, playerInput, currentGuess;
    int scoreInt, newscoreInt, attemptInt;
    String guessHistory = "";

    Boolean isStillPlaying = true, true1 = true, false1 = false;

    
    
           
    private JButton newGamebtn,submitbtn;
    private JLabel showWordlbl,showAttlbl,attemptlbl,wordlbl,showScorlbl,scorlbl,gameStatuslbl,guessHistorylbl;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private javax.swing.JTextField guessedText;
    private javax.swing.JFrame jFram1;

    public static void main(String[] args) throws IOException, InterruptedException {
        new ClientMain();
    }

    // CONSTRUCTOR
    public ClientMain() throws InterruptedException {

        // Ask player
        AskAddressAndPort();

        // Initialize socket thread
        System.out.println("1 Main : Send initialize socket");
        InitializeSocket(); // Just start thread and open connection to server

		// First time. Socket receive message, put to queue
        // then Main take it from queue
        String msga = null;
        msga = ReceiveFromSocket();
        System.out.println("4 Main : Receive : " + msga);

        // Split message and assign to specified variable
        CustomSplitter(msga);
        word = AfterSplit[0];
        availableAttempt = AfterSplit[1];
        score = AfterSplit[2];

        System.out.println(word);
        System.out.println(availableAttempt);

		// Setup first time GUI
        // while (isStillPlaying){
        SetupGui();

    }

    // THE FUNCTIONS
    private void AskAddressAndPort() {
		// TO GET SERVER ADDRESS AND PORT
        // Open dialog box and ask for user input.
        JTextField input1 = new JTextField();
        JTextField input2 = new JTextField();
        String[] options = {"Start New Game!"};
        Object[] message = {"Server address :", input1, "Server port    :", input2,
            "Or just press start to use default value"};
        int option = JOptionPane.showOptionDialog(null, message, "Valkommen till HANGMAN", JOptionPane.NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        // Get the input into variable
        serverAddressInput = input1.getText();
        serverPortInput = input2.getText();

        // If one of the field empty, use default value
        if (serverAddressInput.isEmpty() == true || serverPortInput.isEmpty() == true) {
            serverAddressInput = "localhost";
            serverPortInput = "4444";
        }

        serverPortInt = Integer.parseInt(serverPortInput);
    }

    private void InitializeSocket() {
        // Connect to server by start a thread
        (new Thread(new ClientSocket(serverAddressInput, serverPortInt, queue))).start();

    }

    private void SendToSocket(String msg) {
        // Put data into queue
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
    }

    private String ReceiveFromSocket() {
        // TAKE DATA FROM SOCKET (QUEUE)
        try {
            msg = (String) queue.take();
            queue.clear();
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
        return (msg);
    }

    private void CustomSplitter(String rawString) {
        // SPLIT INTO SEVERAL STRINGS LIMITED BY SPACE
        AfterSplit = rawString.split("#");
    }

    private void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void SendGuess() {
        // Apply space and append to history to be shown
        guessHistory = guessHistory + " " + currentGuess;

        // Send to Server
        System.out.println("1 Main : Send to socket : " + currentGuess);
        SendToSocket(currentGuess);
        Sleep(100);// Sleep to give socket time to take the data
    }

    private void ReceiveUpdateGui() {
        // Receive back update from server
        String msgb = ReceiveFromSocket();
        System.out.println("4 Main : Update fr socket : " + msgb);
        System.out.println("===================================");

        // Do the rest
        CustomSplitter(msgb);
        word = AfterSplit[0];
        availableAttempt = AfterSplit[1];
        newscore = AfterSplit[2];
        newscoreInt = Integer.parseInt(newscore.trim());
        System.out.println("Score : " + scoreInt);
        System.out.println("New score : " + newscoreInt);

        // Update
        scorlbl.setText("Your score now : " + newscoreInt);
        wordlbl.setText("Come on, guess it		: " + word);
        attemptlbl.setText("Your available attempts	: " + availableAttempt);
        guessHistorylbl.setText("You have guessed			: " + guessHistory);
        guessedText.setText("");
    }

    private Boolean isEnd() {
		// int scoreInt = Integer.parseInt(score.trim());
        // int newscoreInt = Integer.parseInt(newscore.trim());
        Boolean result = null;
		// CHECK IF THE GAME REACH END OR NOT
        // Win
        if (newscoreInt != scoreInt) {
            result = true;
        } else if (newscoreInt == scoreInt) {
            result = false;
        }
        return result;
    }

    private Boolean isWin() {
		// int scoreInt = Integer.parseInt(score.trim());
        // int newscoreInt = Integer.parseInt(newscore.trim());
        Boolean result = null;
		// CHECK IF THE GAME REACH END OR NOT
        // Win
        if (newscoreInt > scoreInt) {
            result = true;
        } else if (newscoreInt < scoreInt) {
            result = false;
        }
        return result;
    }

    private void WinAskReplay() {
        String[] options = new String[2];
        options[0] = new String("Yes");
        options[1] = new String("No");
        int res = JOptionPane.showOptionDialog(null, "You Win! Replay?", "Congratulations", 0,
                JOptionPane.INFORMATION_MESSAGE, null, options, null);

        switch (res) {
            case JOptionPane.YES_OPTION:
                SendToSocket("NEWGAME");
                Sleep(100);
                scoreInt++;
                guessHistory = "";
                ReceiveUpdateGui();
                break;
            case JOptionPane.NO_OPTION:
                SendToSocket("ENDGAME"); // Socket will detect this word and close
                // connection
                System.exit(0);
                break;
        }
    }

    private void LoseAskReplay() {
        String[] options = new String[2];
        options[0] = new String("Yes");
        options[1] = new String("No");
        int res = JOptionPane.showOptionDialog(null, "You Lose! Replay?", "Game Over", 0,
                JOptionPane.INFORMATION_MESSAGE, null, options, null);
        switch (res) {
            case JOptionPane.YES_OPTION:
                SendToSocket("NEWGAME");
                Sleep(100);
                scoreInt--;
                guessHistory = "";
                ReceiveUpdateGui();
                break;
            case JOptionPane.NO_OPTION:
                SendToSocket("ENDGAME"); // Socket will detect this word and close
                // connection
                System.exit(0);
                break;
        }
    }

    private void SetupGui() {
        jFram1=new JFrame();
        jFram1.setBounds(0, 0, 600, 800);
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        showWordlbl = new javax.swing.JLabel();
        showAttlbl = new javax.swing.JLabel();
        attemptlbl = new javax.swing.JLabel();
        wordlbl = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        guessHistorylbl = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        guessedText = new javax.swing.JTextField();
        submitbtn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        showScorlbl = new javax.swing.JLabel();
        scorlbl = new javax.swing.JLabel();
        gameStatuslbl = new javax.swing.JLabel();
        newGamebtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        showWordlbl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        showWordlbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showWordlbl.setText(" word :");

        showAttlbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        showAttlbl.setText("Available Attempts");
        showAttlbl.setName(""); // NOI18N

        attemptlbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attemptlbl.setText("attemptlbl");

        wordlbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wordlbl.setText("----------");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(225, 225, 225)
                        .addComponent(showWordlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wordlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(showAttlbl)
                        .addGap(18, 18, 18)
                        .addComponent(attemptlbl)))
                .addContainerGap(214, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attemptlbl)
                    .addComponent(showAttlbl))
                .addGap(59, 59, 59)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showWordlbl)
                    .addComponent(wordlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Wrong Guessed Letters "));

        guessHistorylbl.setText("jLabel1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(guessHistorylbl, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(guessHistorylbl)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Enter a letter or whole word", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        guessedText.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        guessedText.setText("jTextField1");

        submitbtn.setText("Submit");
        submitbtn.setActionCommand("submitBtn");
       /* submitbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitbtnActionPerformed(evt);
            }
        });*/

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(guessedText, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(submitbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guessedText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitbtn))
                .addGap(41, 41, 41))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));

        showScorlbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        showScorlbl.setText("Player Score");

        scorlbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        scorlbl.setText("Scorelbl");

        gameStatuslbl.setText("game status");

        newGamebtn.setText("New Game");
        /*newGamebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGamebtnActionPerformed(evt);
            }
        */

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(showScorlbl)
                        .addGap(18, 18, 18)
                        .addComponent(scorlbl)
                        .addGap(129, 129, 129)
                        .addComponent(gameStatuslbl))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(268, 268, 268)
                        .addComponent(newGamebtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(showScorlbl)
                            .addComponent(scorlbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap(28, Short.MAX_VALUE)
                        .addComponent(gameStatuslbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(newGamebtn))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        
        jFram1.add(jPanel1);
        jFram1.setVisible(true);
        jFram1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    
     /*@Override
     public void actionPerformed(ActionEvent e) {
     // First, get text field and convert all to uppercase
     playerInput = jTextField1.getText();
     currentGuess = playerInput.toUpperCase();

     // Check first if the string is empty
     if (!currentGuess.isEmpty()) {
     SendGuess();
     ReceiveUpdateGui();
     attemptInt = Integer.parseInt(availableAttempt.trim());
     if (isEnd()) {
     if (isWin())
     WinAskReplay();
     else if (!isWin())
     LoseAskReplay();
     }
     } else if (currentGuess.isEmpty()) {
     JOptionPane.showMessageDialog(null, "Please guess a letter or word.");
     }
     }
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        playerInput = guessedText.getText();
        currentGuess = playerInput.toUpperCase();

        // Check first if the string is empty
        if (!currentGuess.isEmpty()) {
            SendGuess();
            ReceiveUpdateGui();
            attemptInt = Integer.parseInt(availableAttempt.trim());
            if (isEnd()) {
                if (isWin()) {
                    WinAskReplay();
                } else if (!isWin()) {
                    LoseAskReplay();
                }
            }
        } else if (currentGuess.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please guess a letter or word.");
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
