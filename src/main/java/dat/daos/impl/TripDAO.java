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

    public TripDTO getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if(trip == null) {
                return null;
            }
            NewGuideDTO newGuideDTO = null;
            if (trip.getGuide() != null) {
                newGuideDTO = new NewGuideDTO(trip.getGuide().getId(), trip.getGuide().getFirstname(), trip.getGuide().getLastname(), trip.getGuide().getEmail(), trip.getGuide().getPhone(), trip.getGuide().getYearsOfExperience());
            }
            return new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType(), newGuideDTO);
        }
    }

    public List<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            List<Trip> trips = query.getResultList();
            List<TripDTO> tripDTOS = new ArrayList<>();
            for (Trip trip : trips) {
                NewGuideDTO newGuideDTO = null;
                if (trip.getGuide() != null) {
                    newGuideDTO = new NewGuideDTO(trip.getGuide().getId(), trip.getGuide().getFirstname(), trip.getGuide().getLastname(), trip.getGuide().getEmail(), trip.getGuide().getPhone(), trip.getGuide().getYearsOfExperience());
                    tripDTOS.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType(), newGuideDTO));
                } else {
                    tripDTOS.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType()));
                }

            }
            return tripDTOS;
        }
    }

    public NewTripDTO create(NewTripDTO newTripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip(newTripDTO);
            em.persist(trip);
            em.getTransaction().commit();
            return new NewTripDTO(trip);
        } catch (Exception e) {
            logger.error("Error creating trip", e);
            throw new JpaException("Error creating trip", e);
        }
    }

    public NewTripDTO update(int id, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip t = em.find(Trip.class, id);
            if(t != null) {
                t.setStarttime(tripDTO.getStarttime());
                t.setEndtime(tripDTO.getEndtime());
                t.setLongitude(tripDTO.getLongitude());
                t.setLatitude(tripDTO.getLatitude());
                t.setName(tripDTO.getName());
                t.setPrice(tripDTO.getPrice());
                t.setCategoryType(tripDTO.getCategoryType());
                em.merge(t);
            }
            em.getTransaction().commit();
            return t != null ? new NewTripDTO(t) : null;
        }
    }

    public void delete(int id) {
       try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip != null) {
                Guide guide = trip.getGuide();
                if (guide != null) {
                    guide.getTrips().remove(trip);
                    em.merge(guide);
                }
                em.remove(trip);
            }
            em.getTransaction().commit();
        }
    }

    public TripDTO add(TripDTO tripDTO) {
        return null;
    }



    public void addGuideToTrip(int tripId, int guideId) {
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
            } else {
                em.getTransaction().rollback();
                throw new ApiException(404, "Trip or Guide not found");
            }
        }
    }

    public List<TripDTO> getTripsByCategory(Trip.CategoryType categoryType) {
        try (EntityManager em = emf.createEntityManager()) {

            TypedQuery<TripDTO> query = em.createQuery(
                    "SELECT new dat.dtos.TripDTO(p) FROM Trip p WHERE p.categoryType = :category",
                    TripDTO.class
            );
            query.setParameter("category", categoryType);
            return query.getResultList();
        }
    }

    @Override
    public Set<NewTripDTO> getTripsByGuide(int guideId) {
        try(EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            if (guide != null) {
                return guide.getTrips().stream()
                        .map(NewTripDTO::new)
                        .collect(Collectors.toSet());
            }
            return Set.of();
        }
    }
}
