package Part3;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * NginxSimulator: Simulates Nginx's Event-loop / Non-blocking architecture.
 * Uses a single thread to handle multiple connections with Java NIO.
 */
public class NginxSimulator {
    private static final int PORT = 8082;
    private static final String RESPONSE = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHi from Nginx-style simulator (Event loop)!";

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Nginx Simulator running on port " + PORT + " (Single-threaded Event loop)");

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        register(selector, serverChannel);
                    }

                    if (key.isReadable()) {
                        handleRead(key);
                    }

                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Nginx: Accepted connection from " + client.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int read = client.read(buffer);

        if (read == -1) {
            client.close();
            return;
        }

        String request = new String(buffer.array()).trim();
        System.out.println("Nginx: Processing request: " + (request.length() > 0 ? request.split("\n")[0] : "empty"));

        // In a real Nginx simulator, we wouldn't block with Thread.sleep.
        // We handle everything asynchronously.
        ByteBuffer outBuffer = ByteBuffer.wrap(RESPONSE.getBytes(StandardCharsets.UTF_8));
        client.write(outBuffer);
        client.close();
        System.out.println("Nginx: Request finished.");
    }
}
