package at.ac.tuwien.sepr.assignment.individual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the SEPR Individual Assignment application.
 * This class bootstraps the Spring Boot application.
 */
@SpringBootApplication
public class SeprIndividualAssignmentApplication {

  /** Creates a new {@code SeprIndividualAssignmentApplication}. */
  public SeprIndividualAssignmentApplication() {
  }

  /**
   * Starts the Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(SeprIndividualAssignmentApplication.class, args);
  }

}
