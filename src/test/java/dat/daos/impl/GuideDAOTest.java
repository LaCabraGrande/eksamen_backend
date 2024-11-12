package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.*;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.Assert.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuideDAOTest {

    private static EntityManagerFactory emf;
    private static GuideDAO guideDAO;
    private static TripDAO tripDAO;
    private Guide guide1;
    private Guide guide2;
    private Trip trip1;
    private Trip trip2;
    private Trip trip3;
    private Trip trip4;

    private GuideDTO guideDTO1;
    private GuideDTO guideDTO2;
    private TripDTO tripDTO1;
    private TripDTO tripDTO2;
    private TripDTO tripDTO3;
    private TripDTO tripDTO4;

    @BeforeAll
    public static void setup(){
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        guideDAO = GuideDAO.getInstance(emf);
        tripDAO = TripDAO.getInstance(emf);
    }

    @AfterAll
    public static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void init() {
        guide1 = new Guide();
        guide2 = new Guide();
        trip1 = new Trip();
        trip2 = new Trip();
        trip3 = new Trip();
        trip4 = new Trip();

        guide1.setFirstname("Ole");
        guide1.setLastname("Birk");
        guide1.setEmail("olebirk@gmail.com");
        guide1.setPhone(12345678);
        guide1.setYearsOfExperience(10);

        guide2.setFirstname("Hans");
        guide2.setLastname("Jensen");
        guide2.setEmail("hansjensen@gmail.com");
        guide2.setPhone(87654321);
        guide2.setYearsOfExperience(5);

        trip1.setStarttime(LocalTime.of(10, 30));
        trip1.setEndtime(LocalTime.of(11, 30));
        trip1.setLongitude("10.123456");
        trip1.setLatitude("20.123456");
        trip1.setName("Trip1");
        trip1.setPrice(100);
        trip1.setCategoryType(Trip.CategoryType.BEACH);

        trip2.setStarttime(LocalTime.of(11, 30));
        trip2.setEndtime(LocalTime.of(12, 30));
        trip2.setLongitude("20.123456");
        trip2.setLatitude("30.123456");
        trip2.setName("Trip2");
        trip2.setPrice(200);
        trip2.setCategoryType(Trip.CategoryType.CITY);

        trip3.setStarttime(LocalTime.of(10, 30));
        trip3.setEndtime(LocalTime.of(11, 30));
        trip3.setLongitude("10.123456");
        trip3.setLatitude("20.123456");
        trip3.setName("Trip1");
        trip3.setPrice(100);
        trip3.setCategoryType(Trip.CategoryType.BEACH);

        trip4.setStarttime(LocalTime.of(11, 30));
        trip4.setEndtime(LocalTime.of(12, 30));
        trip4.setLongitude("20.123456");
        trip4.setLatitude("30.123456");
        trip4.setName("Trip2");
        trip4.setPrice(200);
        trip4.setCategoryType(Trip.CategoryType.CITY);

        guideDTO1 = guideDAO.create(new GuideDTO(guide1));
        guideDTO2 = guideDAO.create(new GuideDTO(guide2));

        tripDTO1 = new TripDTO(trip1);
        tripDTO2 = new TripDTO(trip2);
        tripDTO3 = new TripDTO(trip3);
        tripDTO4 = new TripDTO(trip4);
    }

    @AfterEach
    public void cleanup(){
        List<NewGuideDTO> allGuides = guideDAO.getAll();
        for (NewGuideDTO guideDTO : allGuides) {
            try {
                guideDAO.delete(guideDTO.getId());
            } catch (Exception e) {
                System.err.println("Could not delete guideDTO with ID "+ guideDTO.getId() + ": " + e.getMessage());
            }
        }
        List<NewTripDTO> allTrips = tripDAO.getAll();
        for (NewTripDTO tripDTO : allTrips) {
            try {
                tripDAO.delete(Math.toIntExact(tripDTO.getId()));
            } catch (Exception e) {
                System.err.println("Could not delete tripDTO with ID: "+tripDTO.getId() + ": " + e.getMessage());
            }
        }
    }


    @Test
    void add() {
        Guide guide = new Guide();
        guide.setFirstname("Ole");
        guide.setLastname("Birk");
        guide.setEmail("olebirk@gmail.com");
        guide.setPhone(12345678);
        guide.setYearsOfExperience(10);

        GuideDTO newGuideDTO = new GuideDTO(guide);

        GuideDTO addedGuideDTO = guideDAO.create(newGuideDTO);
        Assertions.assertNotNull(addedGuideDTO);
        Assertions.assertEquals(newGuideDTO.getFirstname(), addedGuideDTO.getFirstname());
    }

    @Test
    void getById() {
        NewGuideDTO newGuideDTO = guideDAO.getById(Math.toIntExact(guideDTO1.getId()));
        Assertions.assertNotNull(newGuideDTO);
        Assertions.assertEquals(guideDTO1.getLastname(), newGuideDTO.getLastname());
    }

    @Test
    void getAll() {
        List<NewGuideDTO> guideDTOS = guideDAO.getAll();
        Assertions.assertNotNull(guideDTOS);
        assertEquals(2, guideDTOS.size());
    }

    @Test
    void update() {
        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setId(Math.toIntExact(guideDTO1.getId()));
        guideDTO.setFirstname("Hans");
        guideDTO.setLastname("Jensen");
        guideDTO.setEmail("hansjensen@gmail.com");
        guideDTO.setPhone(87654321);
        guideDTO.setYearsOfExperience(5);

        GuideDTO updatedGuideDTO = guideDAO.update(Math.toIntExact(guideDTO.getId()), guideDTO);
        Assertions.assertNotNull(updatedGuideDTO);
        Assertions.assertEquals(guideDTO.getLastname(), updatedGuideDTO.getLastname());
    }

    @Test
    void delete() {
        guideDAO.delete(Math.toIntExact(guideDTO1.getId()));
        NewGuideDTO newGuideDTO = guideDAO.getById(Math.toIntExact(guideDTO1.getId()));
        Assertions.assertNull(newGuideDTO);
    }
}