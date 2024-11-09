package dat.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.GuideDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.entities.Guide;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import static dat.config.Populate.populateDatabase;

public class GuideController implements IController<GuideDTO> {

    private final GuideDAO guideDAO;
    private static final Logger logger = LoggerFactory.getLogger(GuideController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public GuideController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.guideDAO = GuideDAO.getInstance(emf);
    }

    public void create(Context ctx) {
        try {
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);
            GuideDTO savedGuideDTO = guideDAO.create(guideDTO);
            ctx.status(201).json(savedGuideDTO);
        } catch (Exception e) {
            logger.error("Invalid guide data", e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid guide data",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Throwable e) {
            logger.error("Internal server error", e);
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
            NewGuideDTO newGuideDTO = guideDAO.getById(id);
            if (newGuideDTO != null) {
                ctx.res().setStatus(200);
                ctx.json(newGuideDTO);  // Sends the bicycleDTO as JSON
            } else {
                throw new NotFoundResponse("Guide with ID "+ id + " not found");  // Exception is thrown
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid Guide ID format: {}", ctx.pathParam("id"), e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid Guide ID, wrong format",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Status 404: Not found: Guide with ID: {}", ctx.pathParam("id"), e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Guide with ID " + ctx.pathParam("id") + " not found",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
        catch (Exception e) {
            logger.error("An unknown error occures while retrieving guide with ID: {}", ctx.pathParam("id"), e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "An unknown error occurred. Please try again later",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void getAll(Context ctx) {
        try {
            List<NewGuideDTO> newGuideDTOS = guideDAO.getAll();
            ctx.json(newGuideDTOS);
        } catch (NotFoundResponse e) {
            logger.error("Guide not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "No guides not found",
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


    public void addTripToGuide(Context ctx) {
        try {
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            Guide guide = guideDAO.addTripToGuide(guideId, tripId);
            ctx.json(guide);
        } catch (NumberFormatException e) {
            logger.error("Invalid guide or trip ID", e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid guide or trip ID",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            logger.error("Failed to add trip to guide: {}", e.getMessage(), e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", "Failed to add trip to guide: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Throwable e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);
            GuideDTO updatedGuideDTO = guideDAO.update(id, guideDTO);
            if (updatedGuideDTO != null) {
                ctx.json(updatedGuideDTO);
            } else {
                throw new NotFoundResponse("Guide not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid guide ID", e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid guide ID",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Doctor not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Throwable e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            GuideDTO deletedGuideDTO = guideDAO.delete(id);
            if (deletedGuideDTO != null) {
                String jsonResponse = String.format("{\"Message\": \"Guide deleted\", \"deletedGuideDTO\": %s}",
                        OBJECT_MAPPER.writeValueAsString(deletedGuideDTO));
                ctx.status(200).json(jsonResponse);
            } else {
                throw new NotFoundResponse("Guide not found");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid guide ID", e);
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "message", "Invalid guide ID",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (NotFoundResponse e) {
            logger.error("Guide not found", e);
            ctx.status(404).json(Map.of(
                    "status", 404,
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Throwable e) {
            logger.error("Internal server error", e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    public void populate(Context ctx) {
        try {
            if(!guideDAO.getAll().isEmpty()) {
                ctx.status(400).json(Map.of(
                        "status", 400,
                        "message", "The Database is already populated",
                        "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ));
            } else {
                populateDatabase();
                ctx.res().setStatus(200);
                ctx.json("{ \"Message\": \"The Database has been populated\" }");
            }
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of(
                    "status", 500,
                    "message", "Internal server error: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }
}
