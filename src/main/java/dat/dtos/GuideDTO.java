package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dat.entities.Guide;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuideDTO {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private int phone;
    private int yearsOfExperience;
    private Set<NewTripDTO> trips = new HashSet<>();

    public GuideDTO(int id, String firstname, String lastname, String email, int phone, int yearsOfExperience) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.firstname = guide.getFirstname();
        this.lastname = guide.getLastname();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        if (guide.getTrips() != null) {
            guide.getTrips().forEach(trip -> this.trips.add(new NewTripDTO(trip)));
        }
    }


}



