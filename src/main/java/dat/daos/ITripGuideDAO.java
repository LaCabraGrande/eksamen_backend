package dat.daos;

import dat.dtos.NewTripDTO;
import dat.dtos.TripDTO;

import java.util.Set;

public interface ITripGuideDAO<T> {

    NewTripDTO addGuideToTrip(int tripId, int guideId);
    Set<TripDTO> getTripsByGuide(int guideId);

}
