import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Intentando conectar al Servidor de Chat en " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("¡Conectado exitosamente al servidor!");

            // Set up input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner consoleScanner = new Scanner(System.in);

            // Thread for reading messages from the server
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                        // If server closes by sending EXIT format (Servidor: EXIT)
                        if (serverMessage.equals("Servidor: EXIT") || serverMessage.equals("EXIT")) {
                            System.out.println("El servidor ha notificado un cierre.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Conexión con el servidor interrumpida.");
                } finally {
                    try {
                        socket.close();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();

            // Main thread for sending messages to the server
            while (true) {
                if (consoleScanner.hasNextLine()) {
                    String clientMessage = consoleScanner.nextLine();
                    out.println(clientMessage);

                    if (clientMessage.equalsIgnoreCase("EXIT")) {
                        System.out.println("Cierre ordenado solicitado. Desconectando...");
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}
