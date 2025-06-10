package asp;

import asp.controller.TasksController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class StartTaskService {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
        server.createContext("/api/tasks", new TasksController());
        server.setExecutor(null); // creates a default executor
        try {
            server.start();
            System.out.println("Server started on port 8083");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start server: " + e.getMessage());
        }
    };
}