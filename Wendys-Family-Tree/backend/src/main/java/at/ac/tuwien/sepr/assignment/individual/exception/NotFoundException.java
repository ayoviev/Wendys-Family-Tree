package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception that signals, that whatever resource,
 * that has been tried to access,
 * was not found.
 */
public class NotFoundException extends Exception {
  /**
   * Creates a new {@code NotFoundException} with the given message.
   *
   * @param message a description of what was not found
   */
  public NotFoundException(String message) {
    super(message);
  }

  /**
   * Creates a new {@code NotFoundException} wrapping the given cause.
   *
   * @param cause the underlying cause
   */
  public NotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new {@code NotFoundException} with the given message and cause.
   *
   * @param message a description of what was not found
   * @param cause   the underlying cause
   */
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
