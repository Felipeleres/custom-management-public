package com.felipeleres.customermanagement.handlers;

import com.felipeleres.customermanagement.dto.CustomError;
import com.felipeleres.customermanagement.dto.FieldMessage;
import com.felipeleres.customermanagement.dto.ValidationError;
import com.felipeleres.customermanagement.services.exception.DadosIncompletoException;
import com.felipeleres.customermanagement.services.exception.DataBaseException;
import com.felipeleres.customermanagement.services.exception.PagamentoException;
import com.felipeleres.customermanagement.services.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> MethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError(Instant.now(), status.value(), "Dados inválidos!", request.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            error.addFieldMessage(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler({PagamentoException.class})
    public ResponseEntity<CustomError> PagamentoException (PagamentoException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError error =  new CustomError(Instant.now(),status.value(),"Existe pendências no pagamento",request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({DataBaseException.class})
    public ResponseEntity<CustomError> PagamentoException (DataBaseException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError error =  new CustomError(Instant.now(),status.value(),e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DadosIncompletoException.class)
    public ResponseEntity<CustomError> DadosIncompletoException (DadosIncompletoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }


}