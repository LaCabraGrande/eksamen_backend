package dat.controllers.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.TripDAO;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import dat.exceptions.ApiException;
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

    @Override
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO tripDTO = tripDAO.getById(id);
            if (tripDTO != null) {
                ctx.res().setStatus(200);
                ctx.json(tripDTO, NewTripDTO.class);
            } else {
                throw new ApiException(404, "Trip not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            throw new ApiException(400, "Invalid ID format. Please provide a numeric ID.");
        }
    }

    @Override
    public void getAll(Context ctx) {
        try {
            List<TripDTO> tripDTOS = tripDAO.getAll();
            if(tripDTOS.isEmpty()){
                throw new ApiException(404, "No trips found");
            } else {
                ctx.status(200).json(tripDTOS);
            }
        } catch (Exception e) {
            logger.error("An error occurred while retrieving all trips", e);
            throw new ApiException(500, "Internal server error");
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            TripDTO newTripDTO = tripDAO.create(tripDTO);
            ctx.status(201).json(newTripDTO);
        } catch (BadRequestResponse e) {
            logger.error("Invalid request body", e);
            throw new ApiException(400, "Invalid request format");
        } catch (Exception e) {
            logger.error("An error occurred while creating a new trip", e);
            throw new ApiException(500, "Internal server error");
        }
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
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            throw new ApiException(400, "Invalid ID format. Please provide a numeric ID.");
        } catch (ValidationException e) {
            logger.error("Invalid request body", e);
            throw new ApiException(400, "Invalid request format");
        } catch (Exception e) {
            logger.error("An error occurred while updating trip", e);
            throw new ApiException(500, "Internal server error");
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO tripDTO = tripDAO.getById(id);
            if (tripDTO != null) {
                tripDAO.delete(id);
                ctx.status(200).json(Map.of("message", "Trip with ID " + id + " deleted"));
            } else {
                throw new ApiException(404, "Trip with ID " + id + " not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            throw new ApiException(400, "Invalid ID format. Please provide a numeric ID.");
        } catch (Exception e) {
            logger.error("An error occurred while deleting trip", e);
            throw new ApiException(500, "Internal server error");
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(200).json("Guide with ID: " + guideId + " added to trip with ID: " + tripId);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format", e);
            throw new ApiException(400, "Invalid ID format. Please provide a numeric ID.");
        } catch (Exception e) {
            logger.error("An error occurred while adding guide to trip", e);
            throw new ApiException(500, "Internal server error");
        }
    }

    public void getTripsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        Trip.CategoryType categoryType;
        try {
            categoryType = Trip.CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
           logger.error("Invalid category provided: {}", category);
            throw new ApiException(400, "Invalid category provided");
        }
        try {
            List<TripDTO> trips = tripDAO.getTripsByCategory(categoryType);
            if (trips.isEmpty()) {
               logger.error("No trips found for category: {}", category);
                throw new ApiException(404, "No trips found for category: " + category);
            } else {
                ctx.status(200).json(trips);
            }
        } catch (Exception e) {
            logger.error("An unexpected error occurred for category: {}", category, e);
            throw new ApiException(500, "Internal server error");
        }
    }

    public void getTripsByGuide(Context ctx) {
        try {
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            Set<TripDTO> trips = tripDAO.getTripsByGuide(guideId);
            if (trips.isEmpty()) {
                logger.error("No trips found for guide with ID: {}", guideId);
                throw new ApiException(404, "No trips found for guide with ID: " + guideId);
            }
            ctx.json(trips);
        } catch (NumberFormatException e) {
            logger.error("Invalid Guide ID format: {}", ctx.pathParam("guideId"), e);
            throw new ApiException(400, "Invalid ID format. Please provide a numeric ID.");
        }
    }

    public void getPackingListItemsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        try {
            Trip.CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid category provided: {}", category, e);
            throw new ApiException(400, "Invalid category provided.");
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
                logger.error("Error fetching packing items. Status code: {}", response.statusCode());
                throw new ApiException(response.statusCode(), "Error fetching packing items.");
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching packing list items", e);
            throw new ApiException(500, "Internal server error while fetching packing list items.");
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
                return OBJECT_MAPPER.readTree(response.body());
            } else if (response.statusCode() == 404) {
                return OBJECT_MAPPER.createObjectNode().put("message", "No packing list items found for category: " + category);
            } else {
                throw new ApiException(response.statusCode(), "Error fetching packing list items. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching packing list items for category: {}", category, e);
            throw new ApiException(500, "Internal server error while fetching packing list items.");
        }
    }
}
