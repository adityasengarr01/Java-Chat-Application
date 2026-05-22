 import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

// Client connection starts here
public class Client {
    JFrame frame;
    JTextArea chatArea;
    JTextField messageField;
    JButton sendButton;

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    String username;

    public Client() {
        frame = new JFrame();

        username = JOptionPane.showInputDialog(frame, "Enter your name:");
        frame.setTitle(username);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        chatArea.setBackground(new Color(230, 240, 255));

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 16));

        sendButton = new JButton("Send");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.getViewport().setBackground(new Color(230, 240, 255));

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setSize(500, 500);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        connectToServer();
    }

    void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            chatArea.append("Connected to server\n");

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> receiveMessages()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendMessage() {
        try {
            String msg = messageField.getText();

            dos.writeUTF(username + ": " + msg);
            chatArea.append("You: " + msg + "\n");

            messageField.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void receiveMessages() {
        try {
            String msg;
            while ((msg = dis.readUTF()) != null) {
                if (!msg.startsWith(username + ":")) {
                    chatArea.append(msg + "\n");
                }
            }
        } catch (Exception e) {
            chatArea.append("Connection closed\n");
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
