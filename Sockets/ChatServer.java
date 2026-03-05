import java.io.*;
import java.net.*;

public class ChatServer {
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        System.out.println("Servidor de Chat esperando 2 clientes en el puerto " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Esperar al primer cliente
            Socket client1 = serverSocket.accept();
            System.out.println("Cliente 1 conectado: " + client1.getInetAddress());

            PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
            out1.println("[Servidor] Conectado. Esperando al segundo cliente...");

            // Esperar al segundo cliente
            Socket client2 = serverSocket.accept();
            System.out.println("Cliente 2 conectado: " + client2.getInetAddress());

            PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);

            // Avisar a ambos
            out1.println("[Servidor] ¡Chat iniciado! Ya puedes escribir.");
            out2.println("[Servidor] ¡Conectado! El chat ya está activo.");

            System.out.println("Ambos clientes conectados. Retransmitiendo mensajes...");

            // Hilo: recibe de cliente1, envía a cliente2
            Thread t1 = new Thread(() -> relay(client1, out2, "Cliente 1"));
            // Hilo: recibe de cliente2, envía a cliente1
            Thread t2 = new Thread(() -> relay(client2, out1, "Cliente 2"));

            t1.start();
            t2.start();

            t1.join();
            t2.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Ambos clientes desconectados. Servidor cerrado.");
    }

    private static void relay(Socket source, PrintWriter destination, String label) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.equalsIgnoreCase("EXIT")) {
                    destination.println("[" + label + " se ha desconectado]");
                    System.out.println(label + " se desconectó.");
                    break;
                }
                System.out.println(label + ": " + msg);
                destination.println(label + ": " + msg);
            }
        } catch (IOException e) {
            System.out.println("Conexión con " + label + " interrumpida.");
            destination.println("[" + label + " se ha desconectado inesperadamente]");
        } finally {
            try { source.close(); } catch (IOException ignored) {}
        }
    }
}
