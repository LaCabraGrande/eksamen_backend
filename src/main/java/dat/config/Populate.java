package dat.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import dat.daos.impl.GuideDAO;
import dat.entities.*;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Populate {
    public static Logger logger = LoggerFactory.getLogger(Populate.class);
    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    static GuideDAO guideDAO = GuideDAO.getInstance(emf);

    public static void main(String[] args) {

        if (!guideDAO.hasGuides()) {
            populateDatabase();
        } else {
            logger.info("Database already populated, skipping population.");
        }
    }

    // Metode til at populere databasen med doctors og appointments
    public static void populateDatabase() {

        Trip trip1 = new Trip(LocalTime.of(8, 0), LocalTime.of(16, 0), "55.6761", "12.5683", "Morning Forest Hike", 12, Trip.CategoryType.FOREST);
        Trip trip2 = new Trip(LocalTime.of(9, 0), LocalTime.of(17, 0), "55.6761", "12.5683", "Extended Forest Exploration", 18, Trip.CategoryType.FOREST);
        Trip trip3 = new Trip(LocalTime.of(10, 0), LocalTime.of(18, 0), "57.0488", "9.9217", "Beach Relaxation at Skagen", 25, Trip.CategoryType.BEACH);
        Trip trip4 = new Trip(LocalTime.of(11, 0), LocalTime.of(19, 0), "55.4038", "10.4024", "City Tour in Odense", 10, Trip.CategoryType.CITY);
        Trip trip5 = new Trip(LocalTime.of(12, 0), LocalTime.of(20, 0), "56.2639", "9.5018", "Midday Forest Adventure", 15, Trip.CategoryType.FOREST);
        Trip trip6 = new Trip(LocalTime.of(13, 0), LocalTime.of(21, 0), "55.6759", "12.5655", "Afternoon Copenhagen Sightseeing", 20, Trip.CategoryType.CITY);
        Trip trip7 = new Trip(LocalTime.of(14, 0), LocalTime.of(22, 0), "55.4732", "8.4412", "Evening Walk at Esbjerg Beach", 8, Trip.CategoryType.BEACH);
        Trip trip8 = new Trip(LocalTime.of(15, 0), LocalTime.of(23, 0), "55.6761", "12.5683", "Nightlife in Copenhagen", 5, Trip.CategoryType.CITY);
        Trip trip9 = new Trip(LocalTime.of(16, 0), LocalTime.of(0, 0), "56.1325", "9.2286", "Twilight Forest Stroll", 10, Trip.CategoryType.FOREST);
        Trip trip10 = new Trip(LocalTime.of(17, 0), LocalTime.of(1, 0), "55.6905", "12.4858", "Midnight City Lights", 6, Trip.CategoryType.CITY);
        Trip trip11 = new Trip(LocalTime.of(18, 0), LocalTime.of(2, 0), "55.5269", "8.4051", "Beach Bonfire at West Coast", 7, Trip.CategoryType.BEACH);
        Trip trip12 = new Trip(LocalTime.of(19, 0), LocalTime.of(3, 0), "55.6761", "12.5683", "Late-Night City Excursion", 4, Trip.CategoryType.CITY);



        // Opretter appointments og tilføjer dem til en set
        Set<Trip> trips1 = new HashSet<>();
        trips1.add(trip1);
        trips1.add(trip2);
        trips1.add(trip3);

        Set<Trip> trips2 = new HashSet<>();
        trips2.add(trip4);
        trips2.add(trip5);
        trips2.add(trip6);

        Set<Trip> trips3 = new HashSet<>();
        trips3.add(trip7);
        trips3.add(trip8);
        trips3.add(trip9);

        Set<Trip> trips4 = new HashSet<>();
        trips4.add(trip10);
        trips4.add(trip11);
        trips4.add(trip12);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Opretter guides og tilføjer trips
            Guide guide1 = new Guide("Hans", "Jensen", "hansjensen@gmail.com", 82445678, 10);
            Guide guide2 = new Guide("Niels", "Nielsen", "nielsnielsen@gmail.com", 82445679, 20);
            Guide guide3 = new Guide("Peter", "Petersen", "peterpetersen@gmail.com", 82445680, 30);
            Guide guide4 = new Guide("Lars", "Larsen", "larslarsen@gmail.com", 82445681, 40);

            guide1.setTrips(trips1);
            guide2.setTrips(trips2);
            guide3.setTrips(trips3);
            guide4.setTrips(trips4);

            em.persist(guide1);
            em.persist(guide2);
            em.persist(guide3);
            em.persist(guide4);

            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);
            em.persist(trip4);
            em.persist(trip5);
            em.persist(trip6);
            em.persist(trip7);
            em.persist(trip8);
            em.persist(trip9);
            em.persist(trip10);
            em.persist(trip11);
            em.persist(trip12);

            em.getTransaction().commit();
            logger.info("Database populated successfully");
        } catch (PersistenceException e) {
            logger.error("Error populating database", e);
            throw new RuntimeException("Error populating database", e);
        }
    }
}
