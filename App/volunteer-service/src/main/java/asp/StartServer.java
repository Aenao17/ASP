package asp;

import asp.controller.VolunteersController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class StartServer{
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        server.createContext("/api/volunteers", new VolunteersController());
        server.setExecutor(null); // creates a default executor
        try {
            server.start();
            System.out.println("Server started on port 8082");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}