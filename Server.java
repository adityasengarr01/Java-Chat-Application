 import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
  // Server starts here
    JFrame frame;
    JTextArea chatArea;
    JTextField messageField;
    JButton sendButton;

    ServerSocket serverSocket;
    Vector<DataOutputStream> clientOutputs = new Vector<>();

    public Server() {
        frame = new JFrame("Server");

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setSize(500, 500);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        startServer();
    }

    void startServer() {
        try {
            serverSocket = new ServerSocket(1234);
            chatArea.append("Server started...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                chatArea.append("Client connected\n");

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                clientOutputs.add(dos);

                new Thread(() -> handleClient(socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleClient(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String msg;
            while ((msg = dis.readUTF()) != null) {
                chatArea.append(msg + "\n");
                broadcast(msg);
            }

        } catch (Exception e) {
            chatArea.append("Client disconnected\n");
        }
    }

    void broadcast(String msg) {
        try {
            for (DataOutputStream dos : clientOutputs) {
                dos.writeUTF(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendMessage() {
        String msg = messageField.getText();
        chatArea.append("Server: " + msg + "\n");
        broadcast("Server: " + msg);
        messageField.setText("");
    }

    public static void main(String[] args) {
        new Server();
    }
}
