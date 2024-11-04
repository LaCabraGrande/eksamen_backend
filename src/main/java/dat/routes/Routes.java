package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;


public class Routes {

    private final GuideRoute guideRoute = new GuideRoute();
    private final TripRoute tripRoute = new TripRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/guides", guideRoute.getRoutes());
            path("/trips", tripRoute.getRoutes());

        };
    }
}
