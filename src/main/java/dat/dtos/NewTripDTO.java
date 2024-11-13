package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dat.entities.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class NewTripDTO {

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
    //private GuideDTO guideDTO;

    public NewTripDTO(int id, LocalTime starttime, LocalTime endtime, String longitude, String latitude, String name, int price, Trip.CategoryType categoryType) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.categoryType = categoryType;
    }

    public NewTripDTO(TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.starttime = tripDTO.getStarttime();
        this.endtime = tripDTO.getEndtime();
        this.longitude = tripDTO.getLongitude();
        this.latitude = tripDTO.getLatitude();
        this.name = tripDTO.getName();
        this.price = tripDTO.getPrice();
        this.categoryType = tripDTO.getCategoryType();
    }
    public NewTripDTO(Trip trip) {
        this.id = trip.getId();
        this.starttime = trip.getStarttime();
        this.endtime = trip.getEndtime();
        this.longitude = trip.getLongitude();
        this.latitude = trip.getLatitude();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.categoryType = trip.getCategoryType();
    }

}
