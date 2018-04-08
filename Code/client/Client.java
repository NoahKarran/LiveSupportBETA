/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    
    //constructer
    public Client(String host){
        super("Live Support");
        serverIP = host;
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
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(500,500);
        setVisible(true);
    }
    
    //connect to server
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\nYou have ended the chat. \nWe hope you have solved your issue!");
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally{
            closeCrap();
        }
    }
    //connect to server
    private void connectToServer() throws IOException{
        showMessage("Attempting connection . . . \n");
        connection = new Socket(InetAddress.getByName(serverIP), 54205);
        showMessage("Connected to:"+ connection.getInetAddress().getHostAddress() );
        
    }
    //setup streams to send and recive messgae
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nYou are now connected with one of our experts!\n");
    }
    
    //while chatting with noah
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n"+ message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\nMessage Unknown");
            }
        }while(!message.equals("SERVER - end"));
               
    }
    //close the streams and sockets
    private void closeCrap(){
        showMessage("\n Sorry theres no helpers available right  now! \n Try again later.");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
    }catch(IOException ioException){
        ioException.printStackTrace();
     }
        
   }
    
    
    
    //send message to server
    private void sendMessage(String message){
        try{ 
           
            output.writeObject("CLIENT - "+ message);
            output.flush();
            showMessage("\n"+ message);
        }catch(IOException ioException){
            chatWindow.append("\n Something Went Wrong!!!");
        }
    }
    
    //Update gui to see messgae and able to type
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(m);
                    }
                }
        );
    }
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
