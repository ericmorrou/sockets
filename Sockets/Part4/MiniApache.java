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
    private static final String LOG_FILE = "server.log";

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

            String firstLine = in.readLine();
            if (firstLine == null) return;
            
            // --- LOGGING ---
            logRequest(socket.getInetAddress().toString(), firstLine);
            System.out.println("Request: " + firstLine);

            StringTokenizer tokenizer = new StringTokenizer(firstLine);
            String method = tokenizer.countTokens() > 0 ? tokenizer.nextToken() : "GET";
            String fileRequested = tokenizer.countTokens() > 0 ? tokenizer.nextToken() : "/index.html";

            // --- VIRTUAL HOST LOGIC ---
            String hostHeader = "";
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Host: ")) {
                    hostHeader = line.substring(6).trim();
                }
            }

            String currentWebRoot = WEB_ROOT;
            if (hostHeader.contains("site1.local")) {
                currentWebRoot = "www1";
                System.out.println("VirtualHost: Switching to www1 root for host " + hostHeader);
            }

            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            }

            File file = new File(currentWebRoot, fileRequested.substring(1));
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

    private static synchronized void logRequest(String ip, String request) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println("[" + new Date() + "] IP: " + ip + " - Request: " + request);
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
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
