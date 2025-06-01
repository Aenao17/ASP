package asp.controller;


import asp.database.DAO;
import asp.dtos.VolunteerDTO;
import asp.model.Departament;
import asp.model.Volunteer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class VolunteersController implements HttpHandler {

    private final DAO Dao = new DAO();
    private final ObjectMapper mapper = new ObjectMapper();

    public VolunteersController() {
        try {
            this.Dao.createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            try {
                handlePut(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equals("DELETE")) {
            try {
                handleDelete(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGet(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        try {
            List<Volunteer> volunteers = Dao.getAllVolunteers();
            List<VolunteerDTO> volunteerDTOs = volunteers.stream()
                    .map(v -> new VolunteerDTO(v.getUsernameLinked(), v.getPoints().toString(), v.getBirthday(), v.getDepartament()))
                    .toList();
            String json = mapper.writeValueAsString(volunteerDTOs);

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
            VolunteerDTO v = mapper.readValue(is, VolunteerDTO.class);
            Dao.addVolunteer(v.getUsernameLinked(), v.getPoints(), v.getBirthday(), Departament.valueOf(v.getDepartament()));
            String response = "Volunteer added successfully";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    private void handlePut(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        try {
            VolunteerDTO v = mapper.readValue(is, VolunteerDTO.class);
            Dao.updateVolunteer(
                    v.getUsernameLinked(),
                    v.getPoints(),
                    v.getBirthday(),
                    Departament.valueOf(v.getDepartament())
            );
            String response = "Volunteer updated successfully";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    private void handleDelete(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        if (segments.length < 3) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        String username = segments[segments.length - 1]; // expecting /api/volunteers/{username}

        try {
            Dao.deleteVolunteer(username);
            String response = "Volunteer deleted successfully";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }


}
