package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import dat.entities.Trip;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
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
    private GuideDTO guideDTO;

    public NewTripDTO(int id, LocalTime starttime, LocalTime endtime, String longitude, String latitude, String name, int price, Trip.CategoryType categoryType, GuideDTO guideDTO) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.categoryType = categoryType;
        this.guideDTO = guideDTO;
    }
}
