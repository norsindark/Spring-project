package com.medimarket.api.advice;

import com.medimarket.api.exceptions.CustomerTokenException;
import com.medimarket.api.exceptions.UserLoginException;
import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.exceptions.UserRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandle {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String ,String > handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String , String > errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserRegistrationException.class)
    public Map<String ,String > handleBusinessException(UserRegistrationException ex) {
        Map<String ,String > errorMap = new HashMap<>();
        errorMap.put("errorMessage", ex.getMessage());
        return  errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserLoginException.class)
    public Map<String ,String > handleBusinessException(UserLoginException e) {
        Map<String ,String > errorMap = new HashMap<>();
        errorMap.put("error", e.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String ,String > handleBusinessException(UserNotFoundException e) {
        Map<String ,String > errorMap = new HashMap<>();
        errorMap.put("error", e.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomerTokenException.class)
    public Map<String ,String > handleBusinessException(CustomerTokenException e) {
        Map<String ,String > errorMap = new HashMap<>();
        errorMap.put("error", e.getMessage());
        return errorMap;
    }
}
