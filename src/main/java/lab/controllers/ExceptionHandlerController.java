package lab.controllers;

import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lab.dto.ErrorMessageDto;
import lab.exceptions.ResourceIsAlreadyExistsException;
import lab.exceptions.ResourceIsNotValidException;
import lab.exceptions.ResourceNotFoundException;
import lab.exceptions.TokenRefreshException;
import lab.exceptions.WrongFormatUserRequestException;

@RestControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessageDto resourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("There are some troubles with finding resource: {}", message);
        return message;
    }

    @ExceptionHandler(ResourceIsNotValidException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessageDto resourceIsNotValid(ResourceIsNotValidException e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("This resource is not valid: {}", message);
        return message;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessageDto methodArgumentIsNotValid(MethodArgumentNotValidException e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(e.getMessage())
        );
        LOGGER.warn("This method argument is not valid: {}", message);
        return message;
    }

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessageDto refreshTokenException(TokenRefreshException e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("Refresh token time out: {}", message);
        return message;
    }

    @ExceptionHandler(ResourceIsAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessageDto resourceIsAlreadyExistsException(ResourceIsAlreadyExistsException e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("This resource is already exists: {}", message);
        return message;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessageDto accessDenied(Exception e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("Your token is expired or you need to authorize: {}", message);
        return message;
    }

    @ExceptionHandler(WrongFormatUserRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessageDto wrongFormat(Exception e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("Wrong format of user request: {}", message);
        return message;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto globalExceptionHandler(Exception e, WebRequest request) {
        ErrorMessageDto message = new ErrorMessageDto(
            new Date(),
            e.getMessage()
        );
        LOGGER.warn("Exception type: {}", e.getClass());
        LOGGER.warn("Server sent error: {}", message);
        return message;
    }
}
