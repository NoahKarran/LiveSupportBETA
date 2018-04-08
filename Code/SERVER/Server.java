
package chat;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    
    //constructor
    public Server(){
        super("Customer Help");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.SOUTH);
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        add(new JScrollPane(chatWindow));
        setSize(500,500);
        setVisible(true);
    }
    
    //Set up and run the server
    public void startRunning(){
        try{
           server = new ServerSocket(6789, 100);
           while(true){
               try{
                   waitForConnection();
                   setupStreams();
                   whileChatting();
               }catch(EOFException eofException){
                   showMessage("\n The connection has been ended.\n I hope we helped solve your issue!");
               }finally{
                   closeCrap();  
           }
           }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
        
    }
    //wait for connection, then display conncetion info
    private void waitForConnection() throws IOException{
        showMessage("Waiting for a customer to request help . .  . \n");
        connection = server.accept();
        showMessage("Now connected to "+connection.getInetAddress().getHostName());
        
    }
    //get stream to send and recive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("");
    }
    
    //during the chat conversation
    private void whileChatting() throws IOException{
        String message = "";
        sendMessage(message);
        ableToType(true);
        do{
            try{
               message = (String) input.readObject();
               showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Could you say that again?");
            }
         }while(!message.equals("CLIENT - end")); 
    }
    
    //close streams and sockets after you are done chatting
    public void closeCrap(){
        showMessage("The client has ended the chat.");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    //send message to client
    private void sendMessage(String message){
        try{
             
            
            output.writeObject("SERVER - "+message);
            output.flush();
            showMessage("\n "+message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR: MESSAGE CANT BE SENT!");
            
        }
        
        
         
    }
    //update chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }
                }
        );
    }
    //let user type stuff
    private void ableToType(final boolean tof){
         SwingUtilities.invokeLater(
               new Runnable(){
                   public void run(){
                       userText.setEditable(tof);
                   }
               }
        );
        
    }
}
