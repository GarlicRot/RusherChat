package org.rusherchat.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rusherchat.windows.ChatWindow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final Logger LOGGER = LogManager.getLogger(ChatClient.class);

    private final String serverAddress;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String username; // The username of the player
    private final ChatWindow chatWindow; // Reference to the ChatWindow

    // Constructor to initialize server address, port, username, and ChatWindow reference
    public ChatClient(String serverAddress, int port, String username, ChatWindow chatWindow) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.username = username;
        this.chatWindow = chatWindow;
    }

    // Method to connect to the server
    public void connect() throws Exception {
        socket = new Socket(serverAddress, port); // Create a socket connection to the server
        out = new PrintWriter(socket.getOutputStream(), true); // Initialize the output stream
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Initialize the input stream

        // Send the username immediately upon connecting
        out.println(username);

        new Thread(new Listener()).start(); // Start a new thread to listen for incoming messages
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        out.println(message); // Send the message to the server as-is
    }

    // Method to disconnect from the server
    public void disconnect() throws Exception {
        socket.close(); // Close the socket connection
    }

    // Listener class to handle incoming messages from the server
    private class Listener implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) { // Read messages from the server
                    LOGGER.info("Received from server: {}", message); // Log the received message
                    chatWindow.displayMessage(message); // Display the message in the ChatWindow
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred while listening for messages", e); // Log any errors that occur
            }
        }
    }
}
