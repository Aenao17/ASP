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
        try{
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
        }
    }

    private void handleGet(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        try{
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
        }catch (Exception e) {
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

}
