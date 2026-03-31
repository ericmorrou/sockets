package Part3;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ApacheSimulator: Simulates Apache's Multi-Processing Module (blocking threads).
 */
public class ApacheSimulator {
    private static final int PORT = 8081;
    private static final AtomicInteger connectionCount = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Apache Simulator running on port " + PORT + " (Thread-per-connection)");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket socket) {
        int id = connectionCount.incrementAndGet();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String line = in.readLine();
            System.out.println("Apache: Thread handling connection " + id + " for request: " + (line != null ? line : "empty"));

            // Artificial Delay to simulate process work
            Thread.sleep(1000); 

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("Hi from Apache-style simulator! Connection number: " + id);
            
            System.out.println("Apache: Thread finished connection " + id);
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Apache: Connection " + id + " error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
