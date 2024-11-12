package dat.controllers.impl;

import dat.exceptions.Message;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class ExceptionController {

    private final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    public void apiExceptionHandler(ApiException e, Context ctx) {
        log.error("STATUS: {}, MESSAGE: {}", e.getStatusCode(), e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.json(new Message(e.getStatusCode(), e.getMessage(), e.getTimeStamp()));

    }

    public void exceptionHandler(Exception e, Context ctx) {
        log.error("STATUS: {}, MESSAGE: {}, TYPE: {}", ctx.res().getStatus(), e.getMessage(), e.getClass());
        ctx.status(500);
        String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        ctx.json(new Message(500, "An unexpected error occurred. Please try again later.", timeStamp));
    }
}