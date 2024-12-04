package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripDAOTest {

    private static EntityManagerFactory emf;
    private static TripDAO tripDAO;

    private TripDTO tripDTO1, tripDTO2;

//    @BeforeAll
//    public static void setup(){
//        emf = HibernateConfig.getEntityManagerFactoryForTest();
//        tripDAO = TripDAO.getInstance(emf);
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        if (emf != null) {
//            emf.close();
//        }
//    }
//
//    @BeforeEach
//    public void init() {
//       tripDTO1 = new TripDTO( LocalTime.of(10, 30), LocalTime.of(11, 30), "10.123456", "20.123456", "Skovtur", 400,Trip.CategoryType.BEACH);
//       tripDTO2 = new TripDTO( LocalTime.of(11, 30), LocalTime.of(12, 30), "20.123456", "30.123456", "Bytur", 500,Trip.CategoryType.CITY);
//
//       tripDTO2 = tripDAO.create(tripDTO2);
//       tripDTO1 = tripDAO.create(tripDTO1);
//
//
//    }
//
//    @AfterEach
//    public void cleanup(){
//        // Ryd op i databasen efter hver test
//        List<NewTripDTO> allTrips = tripDAO.getAll();
//        for (NewTripDTO tripDTO : allTrips) {
//            try {
//                tripDAO.delete(tripDTO.getId());
//            } catch (Exception e) {
//                System.err.println("Could not delete tripDTO with ID: " + tripDTO.getId() + ": " + e.getMessage());
//            }
//        }
//    }
//
//    @Test
//    void create() {
//        TripDTO newTripDTO = tripDAO.create(tripDTO2);
//        Assertions.assertNotNull(newTripDTO);
//        Assertions.assertEquals(tripDTO2.getName(), newTripDTO.getName());
//    }
//
//    @Test
//    void getById() {
//        TripDTO fetchedTripDTO = tripDAO.getById(Math.toIntExact(tripDTO1.getId()));
//        Assertions.assertNotNull(fetchedTripDTO);
//        Assertions.assertEquals(tripDTO1.getName(), fetchedTripDTO.getName());
//    }
//
//    @Test
//    void getAll() {
//        List<NewTripDTO> tripDTOS = tripDAO.getAll();
//        Assertions.assertNotNull(tripDTOS);
//        assertEquals(2, tripDTOS.size());  // tripDTO1 blev oprettet i init(), så vi forventer mindst 1 tur.
//    }
//
//    @Test
//    void updateTrip() {
//        TripDTO updatedTripDTO = new TripDTO();
//        updatedTripDTO.setId(Math.toIntExact(tripDTO1.getId()));
//        updatedTripDTO.setName("Updated Trip");
//        updatedTripDTO.setPrice(150);
//        updatedTripDTO.setCategoryType(Trip.CategoryType.FOREST);
//
//        TripDTO result = tripDAO.update(updatedTripDTO.getId(), updatedTripDTO);
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(updatedTripDTO.getName(), result.getName());
//        Assertions.assertEquals(updatedTripDTO.getPrice(), result.getPrice());
//    }
//
//    @Test
//    void deleteTrip() { // Testen fejler, da vi ikke kan slette en tur, da den er i brug.
//        tripDAO.delete(Math.toIntExact(tripDTO1.getId()));
//        List<NewTripDTO> tripDTOS = tripDAO.getAll();
//        assertEquals(1, tripDTOS.size());
//
//    }


}
