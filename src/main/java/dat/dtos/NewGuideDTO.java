package dat.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewGuideDTO {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private int phone;
    private int yearsOfExperience;

    public NewGuideDTO(int id, String firstname, String lastname, String email, int phone, int yearsOfExperience, Set<TripDTO> tripDTOS) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public NewGuideDTO(String firstname, String lastname, String email, int phone, int yearsOfExperience) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public NewGuideDTO(GuideDTO guideDTO) {
        this.id = guideDTO.getId();
        this.firstname = guideDTO.getFirstname();
        this.lastname = guideDTO.getLastname();
        this.email = guideDTO.getEmail();
        this.phone = guideDTO.getPhone();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
    }

}

