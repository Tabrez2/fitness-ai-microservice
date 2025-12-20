package tab.fitness.activityService.util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
public class CommonUtil {

    public static ResponseEntity<?> createResponse(Object data, HttpStatus responseStatus){

        GenericResponse response = GenericResponse.builder()
                .status("success")
                .message("success")
                .responseStatus(responseStatus)
                .data(data)
                .build();

        return  response.create();

    }

    public static ResponseEntity<?> createResponseMessage(String message, HttpStatus responseStatus){

        GenericResponse response = GenericResponse.builder()
                .status("success")
                .message(message)
                .responseStatus(responseStatus)
                .build();

        return  response.create();

    }

    public static ResponseEntity<?> errorResponse(Object data, HttpStatus responseStatus){

        GenericResponse response = GenericResponse.builder()
                .status("failed")
                .message("failed")
                .data(data)
                .responseStatus(responseStatus)
                .build();

        return  response.create();

    }

    public static ResponseEntity<?> errorResponseMessage(String message, HttpStatus responseStatus){

        GenericResponse response = GenericResponse.builder()
                .status("failed")
                .message(message)
                .responseStatus(responseStatus)
                .build();

        return  response.create();

    }

}
