package dat.controllers.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.TripDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import dat.exceptions.JpaException;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TripController implements IController<TripDTO> {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);
    private final TripDAO tripDAO;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String BASE_URL_FETCHING_PACKING_LIST = "https://packingapi.cphbusinessapps.dk/packinglist/";

    public TripController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.tripDAO = TripDAO.getInstance(emf);
    }

    public void getAll(Context ctx) {
        try {
            List<NewTripDTO> newTripDTOS = tripDAO.getAll();
            ctx.json(newTripDTOS);
        }  catch (NotFoundResponse e) {
            logger.error("Trip not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Trip not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("Unknown error occurred", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            NewTripDTO newTripDTO = tripDAO.getById(id);
            if (newTripDTO != null) {
                ctx.res().setStatus(200);
                ctx.json(newTripDTO, NewTripDTO.class);
            } else {
                throw new NotFoundResponse("Trip not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid trip ID format.",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Trip with ID: {}", ctx.pathParam("id") + " not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Trip with ID: " + ctx.pathParam("id") + " not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("An unknown error occurred while retrieving trip with ID: {}", ctx.pathParam("id"), e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void create(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            TripDTO newTripDTO = tripDAO.create(tripDTO);
            ctx.status(201).json(newTripDTO);
        }  catch (ValidationException e) {
            logger.error("Validation error: {}", e.getErrors(), e);
            ctx.status(400).json(createErrorResponse(400, "Validation failed for the provided trip data."));
        } catch (BadRequestResponse e) {
            logger.error("Invalid request: {}", e.getMessage());
            ctx.status(400).json(createErrorResponse(400, "The provided request is invalid. " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    private Map<String, Object> createErrorResponse(int httpstatus, String message) {
        return Map.of(
                "status", httpstatus,
                "message", message,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @Override
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            TripDTO updatedTripDTO = tripDAO.update(id, tripDTO);
            if (updatedTripDTO != null) {
                ctx.res().setStatus(200);
                ctx.json(updatedTripDTO);
            } else {
                throw new NotFoundResponse("Trip not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid trip ID format.",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Trip with ID: {}", ctx.pathParam("id") + " not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Trip with ID: " + ctx.pathParam("id") + " not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("An unknown error occures while updating trip with ID: {}", ctx.pathParam("id"), e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    // Her slettes en appointment fra databasen
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO deletedTripDTO = tripDAO.delete(id);
            if (deletedTripDTO != null) {
                String jsonResponse = String.format("{\"Message\": \"Trip deleted\", \"deletedTripDTO\": %s}",
                        OBJECT_MAPPER.writeValueAsString(deletedTripDTO));
                ctx.status(200).json(jsonResponse);
            } else {
                throw new NotFoundResponse("Trip not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid trip ID format.",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Trip with ID: {}", ctx.pathParam("id") + " not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Trip with ID: " + ctx.pathParam("id") + " not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Throwable e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));

            NewTripDTO newTripDTO = tripDAO.addGuideToTrip(tripId, guideId);

            if (newTripDTO == null) {
                ctx.status(404).json(createErrorResponse(404, "Either Trip or Guide not found with the given IDs."));
                return;
            }

            ctx.status(200).json(newTripDTO);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format", e);
            ctx.status(400).json(createErrorResponse(400, "Invalid ID format."));
        } catch (Exception e) {
            logger.error("An unknown error occurred while adding guide to trip", e);
            ctx.status(500).json(createErrorResponse(500, "Internal server error"));
        }
    }

    public void getTripsByGuide(Context ctx) {
        try {
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            Set<TripDTO> tripDTOS = tripDAO.getTripsByGuide(guideId);

            if (tripDTOS.isEmpty()) {
                ctx.status(404).json(createErrorResponse(404, "No trips found for guide with ID: " + guideId));
            } else {
                ctx.json(tripDTOS);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Guide ID format: {}", ctx.pathParam("guideId"), e);
            ctx.status(400).json(createErrorResponse(400, "Invalid guide ID format."));
        } catch (Exception e) {
            logger.error("An error occurred while retrieving trips by guide: {}", ctx.pathParam("guideId"), e);
            ctx.status(500).json(createErrorResponse(500, "Internal server error"));
        }
    }

    public void getTripsByCategory(Context ctx) {
        String category = ctx.pathParam("category");

        // Her validerer jeg først om kategorien findes i min CategoryType enum
        Trip.CategoryType categoryType;
        try {
            categoryType = Trip.CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(createErrorResponse(400, "Invalid category provided"));
            return;
        }

        try {
            List<TripDTO> trips = tripDAO.getTripsByCategory(categoryType);

            if (trips.isEmpty()) {
                ctx.status(404).json(createErrorResponse(404, "No trips found for category: " + category));
            } else {
                ctx.json(trips);
            }
        } catch (JpaException e) {
            logger.error("Database error for category: {}", category, e);
            ctx.status(500).json(createErrorResponse(500, "Database error"));
        } catch (Exception e) {
            logger.error("An unexpected error occurred for category: {}", category, e);
            ctx.status(500).json(createErrorResponse(500, "Internal server error"));
        }
    }


    public void getPackingListByCategory(Context ctx) {
        String category = ctx.pathParam("category").toUpperCase();
        Trip.CategoryType categoryType;
        try {
            categoryType = Trip.CategoryType.valueOf(category);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid category provided");
            return;
        }

        String url = BASE_URL_FETCHING_PACKING_LIST + category.toLowerCase();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ctx.json(OBJECT_MAPPER.readTree(response.body()));
            } else {
                ctx.status(response.statusCode()).result("Error fetching packing items. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error fetching packing list items");
        }
    }

    public JsonNode getPackingList(String category) {
        String url = BASE_URL_FETCHING_PACKING_LIST + category.toLowerCase();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return OBJECT_MAPPER.readTree(response.body()); // Her returnerer jeg JSON som en JsonNode
            } else {
                // Hvis der ikke er nogen pakkeliste, returnerer jeg en  JsonNode med besked om at der ikke er nogen ting at medbringe på den tur
                return OBJECT_MAPPER.createObjectNode().put("message", "No packing list items found for category: " + category);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
