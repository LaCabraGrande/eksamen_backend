package dat.dtos;

import dat.entities.Guide;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GuideDTO {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private int phone;
    private int yearsOfExperience;

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

    }

    public GuideDTO(String firstname, String lastname, String email, int phone, int yearsOfExperience) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Guide toEntity() {
        return new Guide(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuideDTO guideDTO = (GuideDTO) o;
        return getId() == guideDTO.getId() &&
                getPhone() == guideDTO.getPhone() &&
                getYearsOfExperience() == guideDTO.getYearsOfExperience() &&
                Objects.equals(getFirstname(), guideDTO.getFirstname()) &&
                Objects.equals(getLastname(), guideDTO.getLastname()) &&
                Objects.equals(getEmail(), guideDTO.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstname(), getLastname(), getEmail(), getPhone(), getYearsOfExperience());
    }
}

