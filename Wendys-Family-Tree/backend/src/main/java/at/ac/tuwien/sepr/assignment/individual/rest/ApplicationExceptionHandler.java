package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central exception handler for REST controllers, mapping exceptions to HTTP responses.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** Creates a new {@code ApplicationExceptionHandler}. */
  public ApplicationExceptionHandler() {
  }

  /**
   * Handles {@link ValidationException} by returning a 422 Unprocessable Entity response.
   *
   * @param e the validation exception
   * @return a {@link ValidationErrorRestDto} containing validation error details
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ResponseBody
  public ValidationErrorRestDto handleValidationException(ValidationException e) {
    LOG.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ValidationErrorRestDto(e.summary(), e.errors());
  }

  /**
   * Handles {@link ConflictException} by returning a 409 Conflict response.
   *
   * @param e the conflict exception
   * @return a {@link ValidationErrorRestDto} containing conflict error details
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ValidationErrorRestDto handleConflictException(ConflictException e) {
    LOG.warn("Terminating request processing with status 409 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ValidationErrorRestDto(e.summary(), e.errors());
  }

  /**
  * Handles {@link NotFoundException} by returning a 404 Not Found response.
  *
  * @param e the not found exception
  * @return a {@link ValidationErrorRestDto} containing the error message
  */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ValidationErrorRestDto handleNotFoundException(NotFoundException e) {
    LOG.warn("Terminating request processing with status 404 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ValidationErrorRestDto(e.getMessage(), List.of());
  }

  /**
   * Handles {@link FatalException} by returning a 500 Internal Server Error response.
   *
   * @param e the fatal exception
   * @return a message describing the unrecoverable error
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleFatalException(FatalException e) {
    LOG.error("Unrecoverable error: {}", e.getMessage(), e);
    return e.getMessage();
  }

}
