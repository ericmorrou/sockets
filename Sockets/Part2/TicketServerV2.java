package Part2;

import java.io.*;
import java.net.*;

/**
 * Solution 2: Multi-threaded Ticket Server (Thread per client).
 * Each client connection is handled by a new thread.
 */
public class TicketServerV2 {
    private static int ticketCounter = 0;

    public static void main(String[] args) {
        int port = 6001;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server V2 (Multi-threaded) listening on port " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                // Concurrent access to ticketCounter is NOT safe here, but we implement it as is for comparison
                int ticket = ++ticketCounter;
                out.println("Your Ticket ID is: " + ticket);
                System.out.println("Issued ticket: " + ticket + " (by thread " + getName() + ")");
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
