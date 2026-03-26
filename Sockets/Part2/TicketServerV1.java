package Part2;

import java.io.*;
import java.net.*;

/**
 * Solution 1: Single-threaded Ticket Server.
 * Demonstrates that if one client holds the connection (e.g. via telnet), 
 * other clients are blocked.
 */
public class TicketServerV1 {
    private static int ticketCounter = 0;

    public static void main(String[] args) {
        int port = 6000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server V1 (Single-threaded) listening on port " + port);
            
            while (true) {
                // This version handles one client at a time sequentially.
                // If a client doesn't disconnect, accept() won't be called again.
                try (Socket socket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    
                    System.out.println("Client connected: " + socket.getInetAddress());
                    
                    // Artificial delay to simulate processing or to allow telnet to occupy it
                    Thread.sleep(2000); 
                    
                    int ticket = ++ticketCounter;
                    out.println("Your Ticket ID is: " + ticket);
                    System.out.println("Issued ticket: " + ticket);
                    
                } catch (Exception e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
