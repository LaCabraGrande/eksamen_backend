package dat.daos.impl;

import dat.daos.IDAO;
import dat.daos.ITripGuideDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.exceptions.JpaException;
import dat.security.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TripDAO implements IDAO<TripDTO>, ITripGuideDAO<TripDTO> {

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

    // Get Trip by ID (henter en trip baseret på ID)
    public NewTripDTO getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);

            if (trip == null) {
                return null;
            }

            // Konverter Trip til TripDTOWithGuide og tilføj den tilknyttede guide
            GuideDTO guideDTO = null;
            if (trip.getGuide() != null) {
                guideDTO = new GuideDTO(trip.getGuide().getId(), trip.getGuide().getFirstname(), trip.getGuide().getLastname(), trip.getGuide().getEmail(), trip.getGuide().getPhone(), trip.getGuide().getYearsOfExperience());
            }

            return new NewTripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType(), guideDTO);
        } catch (Exception e) {
            logger.error("Error fetching trip", e);
            throw new JpaException("Error occured fetching trip by ID: " + id, e);
        }
    }

    public List<NewTripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            List<Trip> trips = query.getResultList();
            List<NewTripDTO> newTripDTOS = new ArrayList<>();
            for (Trip trip : trips) {
                GuideDTO guideDTO = null;
                if (trip.getGuide() != null) {
                    guideDTO = new GuideDTO(trip.getGuide().getId(), trip.getGuide().getFirstname(), trip.getGuide().getLastname(), trip.getGuide().getEmail(), trip.getGuide().getPhone(), trip.getGuide().getYearsOfExperience());
                }
                newTripDTOS.add(new NewTripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType(), guideDTO));
            }
            return newTripDTOS;
        } catch (Exception e) {
            logger.error("Error fetching trips", e);
            throw new JpaException("Error occured fetching trips", e);
        }
    }

    public NewTripDTO addGuideToTrip(int tripId, int guideId) {
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
                return new NewTripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType(), new GuideDTO(guide));
            } else {
                em.getTransaction().rollback();
                throw new ApiException(404, "Trip or Guide not found");
            }
        } catch (Exception e) {
            logger.error("Error adding guide with ID: {}", guideId, e);
            throw new JpaException("Error adding guide with ID: " + guideId, e);
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

    public TripDTO create(TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip(tripDTO);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        } catch (Exception e) {
            logger.error("Error creating trip", e);
            throw new JpaException("Error creating trip", e);
        }
    }

    public List<TripDTO> getTripsByCategory(Trip.CategoryType categoryType) {
        try (EntityManager em = emf.createEntityManager()) {

            TypedQuery<TripDTO> query = em.createQuery(
                    "SELECT new dat.dtos.TripDTO(p) FROM Trip p WHERE p.categoryType = :category",
                    TripDTO.class
            );

            query.setParameter("category", categoryType); // Brug `categoryType` som enum
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid category provided: {}", categoryType, e);
            throw new JpaException("Invalid category provided: " + categoryType, e);
        } catch (Exception e) {
            logger.error("Error fetching trips by category", e);
            throw new JpaException("Error occurred fetching trips by category", e);
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) {
        try(EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            if (guide != null) {
                return guide.getTrips().stream()
                        .map(TripDTO::new)
                        .collect(Collectors.toSet());
            }
            return Set.of();
        } catch (Exception e) {
            logger.error("Error fetching trips by guide", e);
            throw new JpaException("Error occurred fetching trips by guide", e);
        }
    }



}
