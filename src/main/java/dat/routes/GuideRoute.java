package dat.routes;

import dat.controllers.impl.GuideController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class GuideRoute {

    private final GuideController guideController = new GuideController();
    protected EndpointGroup getRoutes() {

        return () -> {
            get("/populate", guideController::populate, Role.ANYONE);
            get("/", guideController::getAll, Role.ANYONE);
            get("/{id}", guideController::getById, Role.ANYONE);
            post("/", guideController::create, Role.ADMIN);
            put("/{id}", guideController::update, Role.ADMIN);
            delete("/{id}", guideController::delete, Role.ADMIN);
        };
    }
}

