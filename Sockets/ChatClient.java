import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando al servidor " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("¡Conectado! Esperando al otro cliente...");

            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter   out  = new PrintWriter(socket.getOutputStream(), true);
            Scanner       kbd  = new Scanner(System.in);

            // Hilo daemon: imprime todo lo que manda el servidor
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("[Conexión cerrada]");
                } finally {
                    System.exit(0);
                }
            });
            reader.setDaemon(true);
            reader.start();

            // Hilo principal: lee teclado y envía al servidor
            while (kbd.hasNextLine()) {
                String msg = kbd.nextLine();
                out.println(msg);
                if (msg.equalsIgnoreCase("EXIT")) {
                    System.out.println("Desconectando...");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("No se pudo conectar: " + e.getMessage());
        }
    }
}
