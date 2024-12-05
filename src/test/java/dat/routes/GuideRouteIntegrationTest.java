//package dat.routes;
//
//import dat.config.ApplicationConfig;
//import dat.config.HibernateConfig;
//import dat.daos.impl.GuideDAO;
//import dat.dtos.GuideDTO;
//import dat.dtos.NewGuideDTO;
//import io.javalin.Javalin;
//import io.restassured.http.ContentType;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import org.junit.jupiter.api.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class GuideRouteIntegrationTest {
//    private static final Logger logger = LoggerFactory.getLogger(GuideRouteIntegrationTest.class);
//    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
//    private static final GuideDAO guideDAO = GuideDAO.getInstance(emf);
//    private static final String BASE_URL = "http://localhost:7070/api";
//    private static final GuidePopulator guidePopulator = new GuidePopulator(guideDAO, emf);
//    private Javalin app;
//    private GuideDTO g1, g2, g3;
//    private NewGuideDTO g4, g5, g6;
//    private List<GuideDTO> guideDTOS;
//    private List<NewGuideDTO> newGuideDTOS;
//    private String jwtToken;
//
//    @BeforeAll
//    void init() {
//        ApplicationConfig.startServer();
//        HibernateConfig.setTest(true);
//
//        // Her registrerer jeg en bruger
//        given()
//                .contentType(ContentType.JSON)
//                .body("{\"username\": \"newuser\", \"password\": \"test123\"}")
//                .when()
//                .post(BASE_URL + "/auth/register/")
//                .then()
//                .statusCode(201);  // Forventet 201 Created
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
//        // Her tilføjer jeg rollen admin til brugeren så han har adgang til at lave CUD operationer
//        given()
//                .header("Authorization", "Bearer " + jwtToken)
//                .contentType(ContentType.JSON)
//                .body("{\"role\": \"admin\"}")  // her giver vi useren admin rollen så han kan lave CUD operationer
//                .when()
//                .post(BASE_URL + "/auth/user/addrole/")
//                .then()
//                .statusCode(200);
//
//        // Her logger jeg brugeren ind igen for at aktivere hans nye rolle som admin og få et nyt JWT-token
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
//        guideDTOS = guidePopulator.populateGuides();
//        newGuideDTOS = guidePopulator.populateNewGuides();
//
//        g1 = guideDTOS.get(0);
//        g2 = guideDTOS.get(1);
//        g3 = guideDTOS.get(2);
//        g4 = newGuideDTOS.get(0);
//        g5 = newGuideDTOS.get(1);
//        g6 = newGuideDTOS.get(2);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            em.createQuery("DELETE FROM Guide").executeUpdate();
//            em.createQuery("DELETE FROM Trip").executeUpdate();
//            em.getTransaction().commit();
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
//    void testGetAllGuides() {
//        NewGuideDTO[] guideDTOS =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .when()
//                        .get(BASE_URL + "/guides/")
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(NewGuideDTO[].class);
//
//        assertThat(guideDTOS, arrayContainingInAnyOrder(g4, g5, g6));  }
//
//
//    @Test
//    void testGetGuideById() {
//        NewGuideDTO guideDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .when()
//                        .get(BASE_URL + "/guides/" + g4.getId())
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(NewGuideDTO.class);
//
//        assertThat(guideDTO.getFirstname(), equalTo(g4.getFirstname()));
//    }
//
//
//
//    @Test
//    void testAddGuide() {
//        GuideDTO g4 = new GuideDTO("Ole", "Birk", "olebirk@gmail.com", 75849323, 5);
//
//        GuideDTO guideDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .body(g4)
//                        .when()
//                        .post(BASE_URL + "/guides/")
//                        .then()
//                        .log().all()
//                        .statusCode(201)
//                        .extract()
//                        .as(GuideDTO.class);
//        assertThat(guideDTO.getLastname(), equalTo(g4.getLastname()));
//
//    }
//
//    @Test
//    void testUpdateGuide() {
//
//        GuideDTO g3 = new GuideDTO("Ole", "Birk", "olebirk@gmail.com", 12345678, 5);
//        GuideDTO guideDTO =
//                given()
//                        .contentType(ContentType.JSON)
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .body(g3)
//                        .when()
//                        .put(BASE_URL + "/guides/" + g1.getId())
//                        .then()
//                        .log().all()
//                        .statusCode(200)
//                        .extract()
//                        .as(GuideDTO.class);
//        assertThat(guideDTO.getFirstname(), equalTo(g3.getFirstname()));
//
//    }
//
//    @Test
//    void testDeleteGuide() {
//        given()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + jwtToken)
//                .when()
//                .delete(BASE_URL + "/guides/" + g1.getId())
//                .then()
//                .log().all()
//                .statusCode(200);
//
//        List<NewGuideDTO> guides = guideDAO.getAll();
//        assertEquals(5, guides.size());
//    }
//}