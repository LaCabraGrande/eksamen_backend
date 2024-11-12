package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.GuideDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

import static dat.config.Populate.populateDatabase;


public class GuideController implements IController<GuideDTO> {

    private final GuideDAO guideDAO;
    private static final Logger logger = LoggerFactory.getLogger(GuideController.class);


    public GuideController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.guideDAO = GuideDAO.getInstance(emf);
    }

    @Override
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            GuideDTO guideDTO = guideDAO.getById(id);
            if (guideDTO != null) {
                ctx.res().setStatus(200);
                ctx.json(guideDTO);
            } else {
                throw new ApiException(404, "Guide with ID "+ id + " not found");
            }
        } catch (Exception e) {
            logger.error("Invalid guide ID", e);
            throw new ApiException(400, "An error occurred while trying to get guide with ID: " + ctx.pathParam("id"));
        }
    }

    @Override
    public void getAll(Context ctx) {
        try {
            List<GuideDTO> guideDTOS = guideDAO.getAll();
            if(guideDTOS.isEmpty()){
                throw new ApiException(404, "No guides found");
            } else {
                ctx.status(200).json(guideDTOS);
            }
        } catch (Exception e) {
            logger.error("Unknown error occurred", e);
            throw new ApiException(500, "Internal server error");
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);
            GuideDTO savedGuideDTO = guideDAO.create(guideDTO);
            ctx.status(201).json(savedGuideDTO);
        } catch (Exception e) {
            logger.error("Invalid guide data", e);
            throw new ApiException(400, "An error occurred while trying to create a guide");
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);
            GuideDTO updatedGuideDTO = guideDAO.update(id, guideDTO);
            if (updatedGuideDTO != null) {
                ctx.status(200).json(updatedGuideDTO);
            } else {
                throw new ApiException(404, "Guide with ID " + id + " not found");
            }
        } catch (Exception e) {
            throw new ApiException(400, "An error occured while trying to update guide with ID: " + ctx.pathParam("id"));
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            GuideDTO deletedGuideDTO = guideDAO.getById(id);
            if (deletedGuideDTO != null) {
                guideDAO.delete(id);
                ctx.status(200).json("{ \"message\": \"Guide with ID " + id + " has been deleted\" }");
            } else {
                throw new ApiException(404, "Guide not found");
            }
        } catch (Exception e) {
            throw new ApiException(400, "An error occured while trying to delete guide with ID: " + ctx.pathParam("id"));
        }
    }

    public void populate(Context ctx) {
        try {
            logger.info("Checking if guides already exist in the database.");

            if (guideDAO.hasGuides()) {
                logger.warn("Attempted to populate database, but it is already populated.");
                throw new ApiException(400, "The Database is already populated");
            } else {
                logger.info("Database is empty. Proceeding with population.");
                populateDatabase();
                ctx.status(200).json(Map.of("message", "The Database has been populated"));
            }
        } catch (ApiException e) {
            logger.warn("Caught ApiException: {}", e.getMessage());
            throw e;  // Lad ApiException boble op for at sikre korrekt statuskode
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            throw new ApiException(500, "Internal server error");
        }
    }
}
