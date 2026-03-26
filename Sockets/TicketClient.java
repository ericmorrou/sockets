import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TicketClient {
    public static void main(String[] args) throws InterruptedException {
        String hostname = "localhost";
        int port = 6003; 

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
            }
        }
        
        System.out.println("Starting stress test ON ROOT on port " + port);
        
        Set<Integer> tickets = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executor = Executors.newFixedThreadPool(20);
        
        for (int i = 0; i < 100; i++) {
            final int p = port;
            executor.execute(() -> {
                try (Socket socket = new Socket("localhost", p);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line = in.readLine();
                    if (line != null) {
                        tickets.add(Integer.parseInt(line.trim()));
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Unique tickets: " + tickets.size());
    }
}
