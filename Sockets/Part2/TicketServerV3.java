package Part2;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Solution 3: Ticket Server using a Thread Pool.
 */
public class TicketServerV3 {
    private static int ticketCounter = 0;

    public static void main(String[] args) {
        int port = 6002;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server V3 (Thread Pool) listening on port " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new ClientTask(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    private static class ClientTask implements Runnable {
        private Socket socket;

        public ClientTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                int ticket = ++ticketCounter;
                out.println("Your Ticket ID is: " + ticket);
                System.out.println("Issued ticket: " + ticket + " (by " + Thread.currentThread().getName() + ")");
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
