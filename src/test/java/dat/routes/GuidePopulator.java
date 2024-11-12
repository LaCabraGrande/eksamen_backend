package dat.routes;

import dat.daos.impl.GuideDAO;
import dat.dtos.GuideDTO;
import dat.dtos.NewGuideDTO;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class GuidePopulator {

    private GuideDAO guideDAO;
    private EntityManagerFactory emf;

    public GuidePopulator(GuideDAO guideDAO, EntityManagerFactory emf) {
        this.guideDAO = guideDAO;
        this.emf = emf;
    }

    public List<GuideDTO> populateGuides() {

        GuideDTO g1, g2, g3;

        g1 = new GuideDTO("Ole", "Birk", "olebirk@gmail.com", 12345678, 5);
        g2 = new GuideDTO("Lars", "Larsen", "larslarsen@gmail.com", 87654321, 10);
        g3 = new GuideDTO("Hans", "Hansen", "hanshansen@gmail.com", 23456789, 15);

        g1 = guideDAO.create(g1);
        g2 = guideDAO.create(g2);
        g3 = guideDAO.create(g3);
        return new ArrayList<>(List.of(g1, g2, g3));
    }

    public List<NewGuideDTO> populateNewGuides() {

        // Opretter NewGuideDTO-objekter
        NewGuideDTO newG4 = new NewGuideDTO("Mette", "Madsen", "mettemadsen@gmail.com", 11223344, 8);
        NewGuideDTO newG5 = new NewGuideDTO("Søren", "Sørensen", "sorensorensen@gmail.com", 22334455, 12);
        NewGuideDTO newG6 = new NewGuideDTO("Pia", "Poulsen", "piapoulsen@gmail.com", 33445566, 7);

        // Konverterer NewGuideDTO-objekter til GuideDTO-objekter
        GuideDTO g4 = new GuideDTO(newG4.getFirstname(), newG4.getLastname(), newG4.getEmail(), newG4.getPhone(), newG4.getYearsOfExperience());
        GuideDTO g5 = new GuideDTO(newG5.getFirstname(), newG5.getLastname(), newG5.getEmail(), newG5.getPhone(), newG5.getYearsOfExperience());
        GuideDTO g6 = new GuideDTO(newG6.getFirstname(), newG6.getLastname(), newG6.getEmail(), newG6.getPhone(), newG6.getYearsOfExperience());

        // Opretter guider i databasen ved at bruge GuideDAO
        g4 = guideDAO.create(g4);
        g5 = guideDAO.create(g5);
        g6 = guideDAO.create(g6);

        // Returnerer listen af oprindelige NewGuideDTO-objekter
        return new ArrayList<>(List.of(newG4, newG5, newG6));
    }



}
