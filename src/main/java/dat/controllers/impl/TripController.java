package dat.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.TripDAO;
import dat.dtos.GuideDTO;
import dat.dtos.TripDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TripController implements IController<IController> {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);
    private final TripDAO tripDAO;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public TripController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.tripDAO = TripDAO.getInstance(emf);
    }

    public void getAll(Context ctx) {
        try {
            ctx.json(tripDAO.getAll());
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

    @Override
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO tripDTO = tripDAO.getById(id);
            ctx.res().setStatus(200);
            ctx.json(tripDTO, TripDTO.class);
        } catch (NumberFormatException e) {
            logger.error("Invalid Trip ID format: {}", ctx.pathParam("id"), e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid trip ID format.",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Status 404: Not found: Trip with ID: {}", ctx.pathParam("id"), e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Trip with ID: " + ctx.pathParam("id") + " not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("An unknown error occures while retrieving trip with ID: {}", ctx.pathParam("id"), e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }
    // Her oprettes en appointment i databasen og tilføjes til en doctor hvis doctorId findes i Context.
    // den dækker altså over 2 endpoints, et til at oprette en appointment og et til at tilføje den til en doctor.
    public void create(Context ctx) {
        try {
            int guideId = Integer.parseInt(ctx.pathParam("id"));
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            if (tripDTO == null) {
                throw new HttpResponseException(400, "Invalid trip data provided.");
            }
            TripDTO newTripDTO = tripDAO.addTripToGuide(guideId, tripDTO);
            GuideDTO guideDTO = tripDAO.create(guideId, tripDTO.getId());
            ctx.status(201).json(newTripDTO);
        } catch (HttpResponseException e) {
            logger.error("Invalid trip Data", e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid trip data provided.",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
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
}
