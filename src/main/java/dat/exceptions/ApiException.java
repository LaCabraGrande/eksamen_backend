package dat.exceptions;

import lombok.Getter;
import java.sql.Timestamp;

@Getter
public class ApiException extends RuntimeException {

    private final int statusCode;
    private final String timeStamp;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
    }
}