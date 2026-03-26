package Part2;

import java.io.*;
import java.net.*;

/**
 * Ticket Server using 'synchronized' to ensure ticket uniqueness.
 */
public class TicketServerSynchronized {
    private static int ticketCounter = 0;

    public static synchronized int getNextTicket() {
        return ++ticketCounter;
    }

    public static void main(String[] args) {
        int port = 6003;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server (Synchronized) listening on port " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        int ticket = getNextTicket();
                        out.println(ticket);
                        System.out.println("Issued ticket (Sync): " + ticket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try { socket.close(); } catch (IOException ignored) {}
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
