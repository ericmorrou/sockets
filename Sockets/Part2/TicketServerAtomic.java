package Part2;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ticket Server using 'AtomicInteger' to ensure ticket uniqueness.
 */
public class TicketServerAtomic {
    private static AtomicInteger ticketCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        int port = 6004;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server (Atomic) listening on port " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        int ticket = ticketCounter.incrementAndGet();
                        out.println(ticket);
                        System.out.println("Issued ticket (Atomic): " + ticket);
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
