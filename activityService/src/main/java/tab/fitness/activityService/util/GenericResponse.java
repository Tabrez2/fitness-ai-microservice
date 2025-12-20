package tab.fitness.activityService.util;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class GenericResponse {

    String status;
    String message;
    Object data;
    HttpStatus responseStatus;

    public ResponseEntity<?> create(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("status",status);
        map.put("message",message);

        if(!ObjectUtils.isEmpty(data)){
            map.put("data",data);
        }
        return new ResponseEntity<>(map,responseStatus);
    }
}