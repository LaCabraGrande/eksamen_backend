package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.impl.GuideDAO;
import dat.daos.impl.TripDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import io.javalin.Javalin;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripRouteIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(TripRouteIntegrationTest.class);
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final TripDAO tripDAO = TripDAO.getInstance(emf);
    private static final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private static final String BASE_URL = "http://localhost:7070/api";
    private static final TripPopulator tripPopulator = new TripPopulator(tripDAO, emf);
    private static final GuidePopulator guidePopulator = new GuidePopulator(guideDAO, emf);
    private Javalin app;
    private TripDTO t1, t2, t3;
    private GuideDTO g1, g2, g3;
    private List<TripDTO> tripDTOS;
    private List<GuideDTO> guideDTOS;
    private String jwtToken;

//    @BeforeAll
//    void init() {
//        ApplicationConfig.startServer();
//        HibernateConfig.setTest(true);
//
//        // Her registreres jeg en ny bruger
//        given()
//                .contentType(ContentType.JSON)
//                .body("{\"username\": \"newuser\", \"password\": \"test123\"}")
//                .when()
//                .post(BASE_URL + "/auth/register/")
//                .then()
//                .statusCode(201);
//
//        // Her logger jeg brugeren ind for at få et JWT-token
//        jwtToken = given()
//                .contentType(ContentType.JSON)
//                .body("{\"username\": \"newuser\", \"password\": \"test123\"}")
//                .when()
//                .post(BASE_URL + "/auth/login/")
//                .then()
//                .statusCode(200)
//                .extract()
//                .path("token");
//
//        // Hej tilføjer jeg admin-rollen til brugeren så jeg kan teste de endpoints der kræver admin-rettigheder
//        given()
//                .header("Authorization", "Bearer " + jwtToken)
//                .contentType(ContentType.JSON)
//                .body("{\"role\": \"admin\"}")
//                .when()
//                .post(BASE_URL + "/auth/user/addrole/")
//                .then()
//                .statusCode(200);
//
//        // Hej logger jeg brugeren ind igen for at få et nyt JWT-token med admin-rettigheder
//        jwtToken = given()
//                .contentType(ContentType.JSON)
//                .body("{\"username\": \"newuser\", \"password\": \"test123\"}")
//                .when()
//                .post(BASE_URL + "/auth/login/")
//                .then()
//                .statusCode(200)
//                .extract()
//                .path("token");
//    }
//
//    @BeforeEach
//    void setUp() {
//        tripDTOS = tripPopulator.populateTrips();
//
//        t1 = tripDTOS.get(0);
//        t2 = tripDTOS.get(1);
//        t3 = tripDTOS.get(2);
//    }
//
//    @AfterEach
//    void tearDown() {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            int deletedTrips = em.createQuery("DELETE FROM Trip").executeUpdate();
//            int deletedGuides = em.createQuery("DELETE FROM Guide").executeUpdate();
//            int deletedUsers = em.createQuery("DELETE FROM User").executeUpdate();
//            em.getTransaction().commit();
//            logger.info("Deleted {} trips, {} guides and {} users from the database.", deletedTrips, deletedGuides, deletedUsers);
//        } catch (Exception e) {
//            logger.error("Error clearing database", e);
//            throw new RuntimeException("Error clearing database", e);
//        }
//    }
//
//    @AfterAll
//    void closeDown() {
//        ApplicationConfig.stopServer(app);
//    }
//
//    @Test
//    void testGetAllTrips() {
//        TripDTO[] tripDTOS =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .when()
//                        .get(BASE_URL + "/trips/")
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(TripDTO[].class);
//        assertThat(tripDTOS, arrayContainingInAnyOrder(t1, t2, t3));
//    }
//
//    @Test
//    void testGetTripById() {
//        TripDTO tripDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .when()
//                        .get(BASE_URL + "/trips/" + t1.getId())
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(TripDTO.class);
//        assertThat(tripDTO, equalTo(t1));
//    }
//
//    @Test
//    void testAddTrip() {
//        TripDTO newTrip = new TripDTO(LocalTime.of(9, 0), LocalTime.of(17, 0), "12.345", "23.456", "Trip2", 200, Trip.CategoryType.FOREST);
//
//        TripDTO tripDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .body(newTrip)
//                        .when()
//                        .post(BASE_URL + "/trips/")
//                        .then()
//                        .log().all()
//                        .statusCode(201)
//                        .extract()
//                        .as(TripDTO.class);
//
//        // Check if the added trip is in the database
//        List<NewTripDTO> allTrips = tripDAO.getAll();
//        assertThat(allTrips, hasItem(new NewTripDTO(tripDTO)));
//    }
//
//
//    @Test
//    void testUpdateTrip() {
//        TripDTO updatedTrip = new TripDTO(LocalTime.of(9, 0), LocalTime.of(17, 0), "12.345", "23.456", "Trip2", 200, Trip.CategoryType.FOREST);
//
//        TripDTO tripDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .body(updatedTrip)
//                        .when()
//                        .put(BASE_URL + "/trips/" + t1.getId())
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(TripDTO.class);
//        assertThat(tripDTO.getName(), equalTo(updatedTrip.getName()));
//    }
//
//    @Test
//    void testDeleteTrip() {
//        given()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + jwtToken)
//                .when()
//                .delete(BASE_URL + "/trips/" + t1.getId())
//                .then()
//                .log().all()
//                .statusCode(200);
//
//        List<NewTripDTO> remainingTrips = tripDAO.getAll();
//        assertEquals(2, remainingTrips.size());
//    }
//
//    @Test
//    void testGetTripsByCategory() {
//        TripDTO[] tripDTOS =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .when()
//                        .get(BASE_URL + "/trips/category/" + Trip.CategoryType.FOREST)
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(TripDTO[].class);
//        assertThat(tripDTOS, arrayContainingInAnyOrder(t1, t2));
//    }
}
