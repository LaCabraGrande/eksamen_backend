package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dat.entities.Trip;
import lombok.*;
import java.time.LocalTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDTO tripDTO = (TripDTO) o;
        return Objects.equals(id, tripDTO.id) &&
                Objects.equals(name, tripDTO.name) &&
                Objects.equals(price, tripDTO.price) &&
                Objects.equals(starttime, tripDTO.starttime) &&
                Objects.equals(endtime, tripDTO.endtime) &&
                Objects.equals(longitude, tripDTO.longitude) &&
                Objects.equals(latitude, tripDTO.latitude) &&
                Objects.equals(getCategoryType(), tripDTO.getCategoryType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, starttime, endtime, longitude, latitude, categoryType);
    }
}
