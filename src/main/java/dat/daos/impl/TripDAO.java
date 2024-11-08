package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuideDTO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TripDAO implements IDAO<TripDTO> {

    private static final Logger logger = LoggerFactory.getLogger(TripDAO.class);
    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public TripDTO getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            return trip != null ? new TripDTO(trip) : null;
        } catch (Exception e) {
            logger.error("Error fetching trip", e);
            throw new JpaException("Error occured fetching trip by ID: " + id, e);
        }
    }

    @Override
    public List<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<TripDTO> query = em.createQuery("SELECT new dat.dtos.TripDTO(p) FROM Trip p", TripDTO.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching trips", e);
            throw new JpaException("Error occured fetching trips", e);
        }
    }

    public TripDTO addTripToGuide(int guideId, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, guideId);
            Trip trip = new Trip(tripDTO);
            guide.addTrip(trip);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        } catch (Exception e) {
            logger.error("Error adding trip with ID: {}", tripDTO.getId(), e);
            throw new JpaException("Error adding appointment with ID: " + tripDTO.getId(), e);
        }
    }

    public TripDTO update(int id, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip a = em.find(Trip.class, id);
            a.setStarttime(tripDTO.getStarttime());
            a.setEndtime(tripDTO.getEndtime());
            a.setLongitude(tripDTO.getLongitude());
            a.setLatitude(tripDTO.getLatitude());
            a.setName(tripDTO.getName());
            a.setPrice(tripDTO.getPrice());
            a.setCategoryType(tripDTO.getCategoryType());

            Trip mergedTrip = em.merge(a);
            em.getTransaction().commit();
            return mergedTrip != null ? new TripDTO(mergedTrip) : null;
        }
        catch (Exception e) {
            logger.error("Error updating trip", e);
            throw new JpaException("Error updating trip", e);
        }
    }

    public TripDTO delete(int id) {
        TripDTO deletedTripDTO = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip != null) {
                deletedTripDTO = new TripDTO(trip);
                // Her fjerner jeg appointment fra doctor for at undgå referencer, der forårsager konflikter
                Guide guide = trip.getGuide();
                if (guide != null) {
                    guide.getTrips().remove(trip);
                    em.merge(guide);
                }
                em.remove(trip);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error deleting trip", e);
            throw new JpaException("Error deleting trip", e);
        }
        return deletedTripDTO;
    }

    public TripDTO add(TripDTO tripDTO) {
        return null;
    }

    public GuideDTO create(int guideId, int tripId) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            Trip trip = em.find(Trip.class, tripId);
            if (guide != null && trip != null) {
                em.getTransaction().begin();
                guide.addTrip(trip);
                em.merge(guide);
                em.getTransaction().commit();
                return new GuideDTO(guide);
            }
            return null;
        }
        catch (Exception e) {
            logger.error("Error adding trip to guide", e);
            throw new JpaException("Error adding trip to guide", e);
        }
    }
}
