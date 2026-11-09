package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception used to signal unexpected and unrecoverable errors.
 */
public class FatalException extends RuntimeException {
  /**
   * Creates a new {@code FatalException} with the given message.
   *
   * @param message a description of the unrecoverable error
   */
  public FatalException(String message) {
    super(message);
  }

  /**
   * Creates a new {@code FatalException} wrapping the given cause.
   *
   * @param cause the underlying cause of the error
   */
  public FatalException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new {@code FatalException} with the given message and cause.
   *
   * @param message a description of the unrecoverable error
   * @param cause   the underlying cause of the error
   */
  public FatalException(String message, Throwable cause) {
    super(message, cause);
  }
}
