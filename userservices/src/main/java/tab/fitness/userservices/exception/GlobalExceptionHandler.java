package tab.fitness.userservices.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tab.fitness.userservices.util.CommonUtil;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

    @Slf4j
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleException(Exception e){
            log.error("GlobalExceptionhandler :: Exception :: ",e.getMessage());
            return CommonUtil.errorResponseMessage(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
            //return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
            return CommonUtil.errorResponseMessage(e.getMessage(),HttpStatus.FORBIDDEN);
        }


        @ExceptionHandler(NullPointerException.class)
        public ResponseEntity<?> handleNullPointerException(Exception e){
            return CommonUtil.errorResponseMessage(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
            //return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(SuccessException.class)
        public ResponseEntity<?> handleSuccessException(SuccessException e){
            return CommonUtil.createResponseMessage(e.getMessage(),HttpStatus.OK);
            //return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleResourceNotFoundException(Exception e){
            log.error("GlobalExceptionhandler :: ResourceNotFoundException :: ",e.getMessage());
            return CommonUtil.errorResponseMessage(e.getMessage(),HttpStatus.NOT_FOUND);
            //return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        @ExceptionHandler(ResourceExistsException.class)
        public ResponseEntity<?> handleResourceExistsException(Exception e){
            log.error("GlobalExceptionhandler :: ResourceExistsException :: ",e.getMessage());
            return CommonUtil.errorResponseMessage(e.getMessage(),HttpStatus.CONFLICT);
            //  return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<?> handleValidationException(ValidationException e){
            return CommonUtil.errorResponse(e.getError(),HttpStatus.BAD_REQUEST);
            //return new ResponseEntity<>(e.getError(),HttpStatus.BAD_REQUEST);
        }
        @ExceptionHandler(FileNotFoundException.class)
        public ResponseEntity<?> handleFileNotFoundException(FileNotFoundException e){
            return CommonUtil.errorResponse(e.getMessage(),HttpStatus.NOT_FOUND);
            //return new ResponseEntity<>(e.getError(),HttpStatus.BAD_REQUEST);
        }
}
