package dat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dat.dtos.GuideDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guide")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id", nullable = false, unique = true)
    private int id;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private int phone;

    @Column(name = "yearsOfExperience", nullable = false)
    private int yearsOfExperience;

    @OneToMany(mappedBy = "guide", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Trip> trips = new HashSet<>();

    public Guide(String firstname, String lastname, String email, int phone, int yearsOfExperience) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Guide(String firstname, String lastname, String email, int phone, int yearsOfExperience, Set<Trip> trips) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
        this.trips = trips;
    }

    public Guide(GuideDTO guideDTO) {
        this.id = guideDTO.getId();
        this.firstname = guideDTO.getFirstname();
        this.lastname = guideDTO.getLastname();
        this.email = guideDTO.getEmail();
        this.phone = guideDTO.getPhone();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
        if (guideDTO.getTrips() != null) {
            guideDTO.getTrips().forEach(tripDTO -> trips.add(new Trip(tripDTO.toDTO())));
        }
    }

    public void setTrips(Set<Trip> trips) {
        if(trips != null) {
            this.trips = trips;
            for (Trip trip: trips) {
                trip.setGuide(this);
            }
        }
    }

    public void addTrip(Trip trip) {
        if (trip != null) {
            this.trips.add(trip);
            trip.setGuide(this);
        }
    }


}
