package dat.routes;

import dat.controllers.impl.TripController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoute {

    private final TripController tripController = new TripController();
    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", tripController::getAll, Role.ANYONE);
            get("/{id}", tripController::getById, Role.ANYONE);
            get("/category/{category}", tripController::getTripsByCategory, Role.ANYONE);
            get("/guide/{guideId}", tripController::getTripsByGuide, Role.ANYONE);
            get("/packing-list/{category}", tripController::getPackingListByCategory, Role.ANYONE);
            post("/", tripController::create, Role.ADMIN);
            put("/{id}", tripController::update, Role.ADMIN);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, Role.ADMIN);
            delete("/{id}", tripController::delete, Role.ADMIN);
        };
    }
}
