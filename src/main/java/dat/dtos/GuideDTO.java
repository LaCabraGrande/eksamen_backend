package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Set<Trip> trips = new HashSet<>();

    // Constructor med alle felter
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
            guide.getTrips().forEach(trip -> trips.add(new TripDTO(trip).toEntity()));
        }
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof GuideDTO guideDto)) return false;

        return getId() == guideDto.getId();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getId());
    }

}

