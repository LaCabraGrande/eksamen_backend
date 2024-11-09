package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.dtos.TripDTO;
import dat.entities.*;
import dat.exceptions.ApiException;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuideDAO implements IDAO<GuideDTO> {

    private static final Logger logger = LoggerFactory.getLogger(GuideDAO.class);
    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }

    public boolean hasGuides() {
        try (EntityManager em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(d) FROM Guide d", Long.class).getSingleResult();
            return count > 0;
        } catch (Exception e) {
            logger.error("Error checking for existing guides", e);
            throw new JpaException("An error occurred while checking for existing Guides", e);
        }
    }

    public GuideDTO create(GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = new Guide(guideDTO);
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        } catch (Exception e) {
            logger.error("Error adding guide", e);
            throw new JpaException("Error adding guide", e);
        }
    }

    public NewGuideDTO getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                return null;
            }

            Set<TripDTO> tripDTOs = new HashSet<>();
            for (Trip trip : guide.getTrips()) {
                tripDTOs.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType()));
            }

            return new NewGuideDTO(guide.getId(), guide.getFirstname(), guide.getLastname(), guide.getEmail(), guide.getPhone(), guide.getYearsOfExperience(), tripDTOs);
        } catch (Exception e) {
            logger.error("Error fetching guide by ID: {}", id, e);
            throw new JpaException("Error occurred while fetching guide by ID: " + id, e);
        }
    }

    public List<NewGuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);
            List<Guide> guides = query.getResultList();
            List<NewGuideDTO> newGuideDTOS = new ArrayList<>();
            for (Guide guide : guides) {
                Set<TripDTO> tripDTOs = new HashSet<>();
                for (Trip trip : guide.getTrips()) {
                    tripDTOs.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType()));
                }
                newGuideDTOS.add(new NewGuideDTO(guide.getId(), guide.getFirstname(), guide.getLastname(), guide.getEmail(), guide.getPhone(), guide.getYearsOfExperience(), tripDTOs));
            }

            return newGuideDTOS;
        } catch (Exception e) {
            logger.error("Error fetching all guides", e);
            throw new JpaException("An error occured while fetching all guides", e);
        }
    }

    public Guide addTripToGuide(int guideId, int tripId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, guideId);
            Trip trip = em.find(Trip.class, tripId);

            if (guide != null && trip != null) {
                guide.addTrip(trip);
                trip.setGuide(guide);
                em.merge(guide);
                em.merge(trip);
                em.getTransaction().commit();
                return guide;
            } else {
                em.getTransaction().rollback();
                throw new ApiException(404, "Guide or trip not found");
            }
        } catch (Exception e) {
            logger.error("Error adding trip to guide with ID: {}", guideId, e);
            throw new JpaException("An error occured while adding trip to guide with ID: " + guideId, e);
        }
    }

    @Override
    public GuideDTO update(int id, GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new dat.security.exceptions.ApiException(404, "Doctor not found");
            }

            if (guideDTO.getFirstname() != null) {
                guide.setFirstname(guideDTO.getFirstname());
            }
            if (guideDTO.getLastname() != null) {
                guide.setLastname(guideDTO.getLastname());
            }
            if (guideDTO.getEmail() != null) {
                guide.setEmail(guideDTO.getEmail());
            }
            if (guideDTO.getPhone() != 0) {
                guide.setPhone(guideDTO.getPhone());
            }
            if (guideDTO.getYearsOfExperience() != 0) {
                guide.setYearsOfExperience(guideDTO.getYearsOfExperience());
            }

            Guide mergedGuide = em.merge(guide);
            em.getTransaction().commit();
            return new GuideDTO(mergedGuide);
        } catch (Exception e) {
            logger.error("Error updating guide with ID: {}", id, e);
            throw new JpaException("An error occured while updating the guide with ID: " + id, e);
        }
    }

    public GuideDTO delete(int id) {
        GuideDTO deletedGuide;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, id);
            if (guide != null) {
                deletedGuide = new GuideDTO(guide);
                for (Trip trip: guide.getTrips()) {
                    em.remove(trip);
                }
                em.remove(guide);
            } else {
                throw new ApiException(404, "Guide not found");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting Guide with ID: {}", id, e);
            throw new JpaException("An error occured while deleting the guide with ID: " + id, e);
        }
        logger.info("Deleted doctor: {}", deletedGuide);
        return deletedGuide;
    }
}
