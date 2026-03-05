import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Arrancando Servidor de Chat básico en el puerto " + PORT + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor a la espera de un cliente (telnet o ChatClient)...");
            
            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("¡Cliente conectado desde " + clientSocket.getInetAddress() + "!");
            
            // Set up input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner consoleScanner = new Scanner(System.in);
            
            // Output greeting
            out.println("Bienvenido al Chat Server. Escribe 'EXIT' para cerrar la conexión.");
            
            // Thread for reading messages from the client
            Thread readThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = in.readLine()) != null) {
                        System.out.println("Cliente: " + clientMessage);
                        if (clientMessage.equalsIgnoreCase("EXIT")) {
                            System.out.println("El cliente ha solicitado cerrar la conexión (cierre ordenado).");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Conexión con el cliente interrumpida.");
                } finally {
                    try {
                        clientSocket.close();
                        System.out.println("Socket de cliente cerrado.");
                        System.exit(0); // Optional: close server after client disconnects for a short demo
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();
            
            // Main thread for sending messages to the client
            while (true) {
                if(consoleScanner.hasNextLine()) {
                    String serverMessage = consoleScanner.nextLine();
                    out.println("Servidor: " + serverMessage);
                    if (serverMessage.equalsIgnoreCase("EXIT")) {
                        System.out.println("Cerrando el servidor de chat...");
                        clientSocket.close();
                        break;
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
