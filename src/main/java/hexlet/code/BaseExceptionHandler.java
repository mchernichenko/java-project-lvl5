package hexlet.code;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class BaseExceptionHandler {
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String generalExceptionHandler(Exception exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ObjectError> validationExceptionHandler(MethodArgumentNotValidException exception) {
        return exception.getAllErrors();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String userNotFoundException(UsernameNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String noSuchElementException(NoSuchElementException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedException(AccessDeniedException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public String validationExceptionsHandler(Exception exception) {
        return exception.getMessage();
    }
}
