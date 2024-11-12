package dat.config;

import dat.controllers.impl.ExceptionController;
import dat.exceptions.ApiException;
import dat.routes.Routes;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import dat.security.routes.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import lombok.NoArgsConstructor;
import dat.utils.ApiProps;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApplicationConfig {

    private static final Routes routes = new Routes();
    private static final ExceptionController exceptionController = new ExceptionController();
    private static final AccessController accessController = new AccessController();

    public static void configuration(JavalinConfig config) {
        config.router.contextPath = ApiProps.API_CONTEXT;
        config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE);
        config.bundledPlugins.enableDevLogging();
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    private static void exceptionContext(Javalin app) {
        app.exception(ApiException.class, (e, ctx) -> exceptionController.apiExceptionHandler(e, ctx));
        app.exception(Exception.class, (e, ctx) -> exceptionController.exceptionHandler(e, ctx));
    }

    public static void startServer() {
        Javalin app = Javalin.create(ApplicationConfig::configuration);
        exceptionContext(app);
        app.beforeMatched(accessController::accessHandler);
        app.start(ApiProps.PORT);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
