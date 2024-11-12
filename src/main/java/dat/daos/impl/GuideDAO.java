package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.dtos.TripDTO;
import dat.entities.*;
import dat.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        }
    }

    public GuideDTO create(GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = new Guide(guideDTO);
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

    public GuideDTO getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                return null;
            }
            Set<TripDTO> tripDTOs = new HashSet<>();
            for (Trip trip : guide.getTrips()) {
                tripDTOs.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType()));
            }
            return new GuideDTO(guide.getId(), guide.getFirstname(), guide.getLastname(), guide.getEmail(), guide.getPhone(), guide.getYearsOfExperience(), tripDTOs);
        }
    }

    public List<GuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);
            List<Guide> guides = query.getResultList();
            List<GuideDTO> guideDTOS = new ArrayList<>();
            for (Guide guide : guides) {
                Set<TripDTO> tripDTOs = new HashSet<>();
                for (Trip trip : guide.getTrips()) {
                    tripDTOs.add(new TripDTO(trip.getId(), trip.getStarttime(), trip.getEndtime(), trip.getLongitude(), trip.getLatitude(), trip.getName(), trip.getPrice(), trip.getCategoryType()));
                }
                guideDTOS.add(new GuideDTO(guide.getId(), guide.getFirstname(), guide.getLastname(), guide.getEmail(), guide.getPhone(), guide.getYearsOfExperience(), tripDTOs));
            }
            return guideDTOS;
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
            return mergedGuide != null ? new GuideDTO(mergedGuide) : null;
        }
    }

    @Override
    public void delete(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, id);
            if (guide != null) {
               for (Trip trip: guide.getTrips()) {
                    trip.setGuide(null);
                }
                em.remove(guide);
            } else {
                throw new ApiException(404, "Guide not found");
            }
            em.getTransaction().commit();
        }
    }
}
