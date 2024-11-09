package dat.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class NewGuideDTO {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private int phone;
    private int yearsOfExperience;
    private Set<TripDTO> tripDTOS;

    public NewGuideDTO(int id, String firstname, String lastname, String email, int phone, int yearsOfExperience, Set<TripDTO> tripDTOS) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
        this.tripDTOS = tripDTOS;
    }
}

