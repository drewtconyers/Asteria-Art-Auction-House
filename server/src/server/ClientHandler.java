/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler processes the individual clients
 */
public class ClientHandler implements Runnable {
    private Server server;
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;

    /**
     * Constructor for ClientHandler
     * @param server
     * @param clientSocket
     */
    protected ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        try{
            fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            toClient = new PrintWriter(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends information about request to client
     * @param arg
     */
    public void sendToClient(String arg) {
        System.out.println("Sending to client: " + arg);
        toClient.println(arg);
        toClient.flush();
    }

    /**
     * Main method to process requests
     */
    @Override
    public void run() {
        String input;
        try {
            while((input = fromClient.readLine()) != null) {
                System.out.println("New request from client, processing request...");
                String request = server.processRequest(input);
                switch (request) {
                    case "Upload Complete":
                        uploadArt();
                        break;

                }
            }
        } catch (IOException e) {
            System.out.println("A client has disconnected.");
        }
    }

    /**
     * Processes new artwork in database
     */
    public void uploadArt() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        sendToClient(gson.toJson("Upload Complete."));
        System.out.println();
    }


}
