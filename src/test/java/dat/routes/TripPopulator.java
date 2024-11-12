package dat.routes;

import dat.daos.impl.TripDAO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TripPopulator {

    private TripDAO tripDAO;
    private EntityManagerFactory emf;

    public TripPopulator(TripDAO tripDAO, EntityManagerFactory emf) {
        this.tripDAO = tripDAO;
        this.emf = emf;
    }

    public List<TripDTO> populateTrips() {

        TripDTO t1 = new TripDTO(LocalTime.of(9, 0), LocalTime.of(17, 0), "12.345", "23.456", "Trip2", 200, Trip.CategoryType.FOREST);
        TripDTO t2 = new TripDTO(LocalTime.of(10, 0), LocalTime.of(18, 0), "12.345", "23.456", "Trip3", 300, Trip.CategoryType.FOREST);
        TripDTO t3 = new TripDTO(LocalTime.of(11, 0), LocalTime.of(19, 0), "12.345", "23.456", "Trip4", 400, Trip.CategoryType.CITY);


        t1 = tripDAO.create(t1);
        t2 = tripDAO.create(t2);
        t3 = tripDAO.create(t3);

        return new ArrayList<>(List.of(t1, t2, t3));
    }
}
