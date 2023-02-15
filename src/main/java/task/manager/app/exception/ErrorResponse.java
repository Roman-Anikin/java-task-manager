package task.manager.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class ErrorResponse {

    private final List<String> errors;
    private final String message;
    private final String reason;
    private final HttpStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(Exception e, String message, String reason, HttpStatus status) {
        this.errors = List.of(String.valueOf(e.getClass()));
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
