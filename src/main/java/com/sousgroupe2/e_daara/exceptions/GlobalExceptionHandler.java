package com.sousgroupe2.e_daara.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, ErrorDetail> ERROR_MAP = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    static {
        ERROR_MAP.put(BadCredentialsException.class, new ErrorDetail(401, "The username or password is incorrect"));
        ERROR_MAP.put(AccountStatusException.class, new ErrorDetail(403, "The account is locked"));
        ERROR_MAP.put(AccessDeniedException.class, new ErrorDetail(403, "You are not authorized to access this resource"));
        ERROR_MAP.put(SignatureException.class, new ErrorDetail(403, "The JWT signature is invalid"));
        ERROR_MAP.put(ExpiredJwtException.class, new ErrorDetail(403, "The JWT token has expired"));
    }

    // Pour gérer les exceptions génériques
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleSecurityException(Exception exception) {
        // Optional: Send stack trace to an observability tool
        exception.printStackTrace();

        ErrorDetail errorDetail = ERROR_MAP.getOrDefault(exception.getClass(), new ErrorDetail(500, "Unknown internal server error."));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(errorDetail.getStatusCode()), exception.getMessage());

        problemDetail.setProperty("description", errorDetail.getDescription());
        problemDetail.setProperty("errorCode", exception.getClass().getSimpleName());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(errorDetail.getStatusCode()).body(problemDetail);
    }

    // Pour gérer les ResourceNotFoundException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResponseStatusException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                exception.getStatusCode(),
                exception.getReason()
        );
        problemDetail.setProperty("description", "Ressource non trouvée");
        problemDetail.setProperty("errorCode", "RESOURCE_NOT_FOUND");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(exception.getStatusCode()).body(problemDetail);
    }

    // Pour gérer les NoSuchElementException
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoSuchElementException() {
        // Pas besoin de body pour une 404
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<String> handleAsyncRequestException(AsyncRequestNotUsableException ex) {
        // Utilisation de logger.error() au lieu de log.error()
        logger.error("Erreur de connexion asynchrone", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur de connexion");
    }

    // Classe interne pour définir les détails d'erreur
    private static class ErrorDetail {
        private final int statusCode;
        private final String description;

        public ErrorDetail(int statusCode, String description) {
            this.statusCode = statusCode;
            this.description = description;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getDescription() {
            return description;
        }
    }
}