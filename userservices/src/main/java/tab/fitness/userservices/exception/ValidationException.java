package tab.fitness.userservices.exception;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{


    private static final long serialVersionUID = 1L;

    private transient Map<String, Object> error = new LinkedHashMap<>();

    public ValidationException(Map<String, Object> error) {
        super("validation failed");
        this.error = error;
    }

    public Map<String, Object> getError() {
        return error;
    }
}