package dat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import dat.dtos.NewTripDTO;
import jakarta.persistence.*;
import lombok.*;
import dat.dtos.TripDTO;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @Column(name = "trip_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "starttime", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime starttime;

    @Column(name = "endtime", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endtime;

    @Column(name = "longitude", nullable = true)
    private String longitude;

    @Column(name = "latitude", nullable = true)
    private String latitude;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "price", nullable = true)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CategoryType categoryType;

    @ManyToOne(optional = true)
    @JoinColumn(name = "guide_id", nullable = true)
    private Guide guide;

    public Trip(int id, LocalTime starttime, LocalTime endtime, String longitude, String latitude, String name, int price, CategoryType categoryType) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.categoryType = categoryType;
    }

    public Trip(LocalTime starttime, LocalTime endtime, String longitude, String latitude, String name, int price, CategoryType categoryType) {
        this.starttime = starttime;
        this.endtime = endtime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.price = price;
        this.categoryType = categoryType;
    }

    public Trip(TripDTO tripDTO) {
        this.starttime = tripDTO.getStarttime();
        this.endtime = tripDTO.getEndtime();
        this.longitude = tripDTO.getLongitude();
        this.latitude = tripDTO.getLatitude();
        this.name = tripDTO.getName();
        this.price = tripDTO.getPrice();
        this.categoryType = tripDTO.getCategoryType();
    }

    public Trip(NewTripDTO newTripDTO) {
        this.starttime = newTripDTO.getStarttime();
        this.endtime = newTripDTO.getEndtime();
        this.longitude = newTripDTO.getLongitude();
        this.latitude = newTripDTO.getLatitude();
        this.name = newTripDTO.getName();
        this.price = newTripDTO.getPrice();
        this.categoryType = newTripDTO.getCategoryType();
    }



    public enum CategoryType {
        BEACH, CITY, FOREST, LAKE, SEA, SNOW
    }
}
