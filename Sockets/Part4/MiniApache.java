package Part4;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Minimal HTTP Server (Simulator of Apache).
 * Reads configuration from server.conf and serves files from www/.
 */
public class MiniApache {
    private static int PORT = 8080;
    private static String WEB_ROOT = "www";

    public static void main(String[] args) {
        loadConfig();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MiniApache started on port " + PORT);
            System.out.println("Serving files from: " + new File(WEB_ROOT).getAbsolutePath());
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("server.conf")) {
            prop.load(input);
            PORT = Integer.parseInt(prop.getProperty("port", "8080"));
            WEB_ROOT = prop.getProperty("root", "www");
        } catch (IOException ex) {
            System.out.println("Configuration file not found, using defaults.");
        }
    }

    private static void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            String line = in.readLine();
            if (line == null) return;
            
            System.out.println("Request: " + line);
            StringTokenizer tokenizer = new StringTokenizer(line);
            String method = tokenizer.nextToken();
            String fileRequested = tokenizer.nextToken();

            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            }

            File file = new File(WEB_ROOT, fileRequested.substring(1));
            if (file.exists() && !file.isDirectory()) {
                byte[] fileData = readFile(file);
                writer.println("HTTP/1.1 200 OK");
                writer.println("Server: MiniApache/1.0");
                writer.println("Content-type: text/html");
                writer.println("Content-length: " + fileData.length);
                writer.println(); 
                writer.flush();
                out.write(fileData);
                out.flush();
            } else {
                String error404 = "<html><body><h1>404 Not Found</h1></body></html>";
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-type: text/html");
                writer.println("Content-length: " + error404.length());
                writer.println();
                writer.println(error404);
                writer.flush();
            }

        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private static byte[] readFile(File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fileIn = new FileInputStream(file)) {
            fileIn.read(fileData);
        }
        return fileData;
    }
}
