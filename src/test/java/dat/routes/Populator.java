package dat.routes;

import dat.daos.impl.GuideDAO;
import dat.dtos.GuideDTO;
import dat.entities.Guide;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Populator {

    private GuideDAO guideDAO;
    private EntityManagerFactory emf;

    public Populator(GuideDAO guideDAO, EntityManagerFactory emf) {
        this.guideDAO = guideDAO;
        this.emf = emf;
    }

    public List<GuideDTO> populateguides() {

        GuideDTO g1, g2, g3;

        g1 = new GuideDTO("Ole", "Birk", "olebirk@gmail.com", 12345678, 5);
        g2 = new GuideDTO("Lars", "Larsen", "larslarsen@gmail.com", 87654321, 10);
        g3 = new GuideDTO("Hans", "Hansen", "hanshansen@gmail.com", 23456789, 15);

        g1 = guideDAO.create(g1);
        g2 = guideDAO.create(g2);
        g3 = guideDAO.create(g3);
        return new ArrayList<>(List.of(g1, g2, g3));
    }
}
