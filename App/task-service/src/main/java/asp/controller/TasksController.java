package asp.controller;


import asp.database.DAO;
import asp.model.Task;
import asp.service.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class TasksController implements HttpHandler {

    private final Service service = new Service();
    private final ObjectMapper mapper = new ObjectMapper();

    public TasksController() {}

    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                handleGet(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equals("POST")) {
            try {
                handlePost(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equals("PUT")) {
            handlePut(exchange);
        } else if (method.equals("DELETE")) {
            //handleDelete(exchange);
        }
    }

    private void handleGet(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        try {
            List<Task> tasks = service.getAllTasks();
            String json = mapper.writeValueAsString(tasks);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            // Write the JSON response
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    private void handlePost(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        try {
            Task t = mapper.readValue(is, Task.class);
            service.addTask(t);
            String response = "{Task added successfully}";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    private void handlePut(com.sun.net.httpserver.HttpExchange exchange) {
        InputStream is = exchange.getRequestBody();
        try{
            String requestBody = new String(is.readAllBytes());
            service.assignUserToTask(requestBody);
            String response = "{Task updated successfully}";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
