package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dat.entities.Trip;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TripDTO {

    private int id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime starttime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endtime;
    private String longitude;
    private String latitude;
    private String name;
    private int price;
    private Trip.CategoryType categoryType;


    public TripDTO(int id, LocalTime starttime, LocalTime endtime, String longitude, String latitude, String name, int price, Trip.CategoryType categoryType) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.categoryType = categoryType;
    }

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.starttime = trip.getStarttime();
        this.endtime = trip.getEndtime();
        this.longitude = trip.getLongitude();
        this.latitude = trip.getLatitude();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.categoryType = trip.getCategoryType();
    }

    public Trip toEntity() {
        return new Trip(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TripDTO tripDTO = (TripDTO) o;
        return getId() == (tripDTO.getId()) && getStarttime().equals(tripDTO.getStarttime()) && getEndtime().equals(tripDTO.getEndtime()) && getLongitude().equals(tripDTO.getLongitude()) && getLatitude().equals(tripDTO.getLatitude()) && getName().equals(tripDTO.getName()) && getPrice() == tripDTO.getPrice() && getCategoryType().equals(tripDTO.getCategoryType());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
