package Part3;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClientSimulator: Simulates multiple concurrent requests to a server
 * and measures response time.
 */
public class ClientSimulator {
    private static final int NUM_CLIENTS = 10;
    private static final String HOST = "localhost";
    private static final int PORT = 8081; // Target port

    public static void main(String[] args) {
        int targetPort = args.length > 0 ? Integer.parseInt(args[0]) : PORT;
        System.out.println("Starting stress test with " + NUM_CLIENTS + " clients to port " + targetPort);
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_CLIENTS; i++) {
            final int clientId = i;
            executor.submit(() -> {
                try (Socket socket = new Socket(HOST, targetPort);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    
                    out.println("GET /index.html HTTP/1.1");
                    out.println("Host: " + HOST);
                    out.println();
                    
                    String line;
                    while ((line = in.readLine()) != null) {
                        // Just consume the response
                    }
                    System.out.println("Client " + clientId + " finished.");
                    
                } catch (IOException e) {
                    System.err.println("Client " + clientId + " error: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("All clients finished in " + (endTime - startTime) + "ms");
    }
}
