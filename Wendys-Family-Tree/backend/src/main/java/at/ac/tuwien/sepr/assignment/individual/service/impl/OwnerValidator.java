package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for owner-related operations, ensuring that all owner data meets
 * the required constraints.
 */
@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** Creates a new {@code OwnerValidator}. */
  public OwnerValidator() {
  }

  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";


  /**
   * Validates an owner before creation, ensuring all fields meet constrains and
   * checking for conflicts.
   * 
   * @param owner The owner to validate.
   * @throws ValidationException If validation fails.
   * 
   */
  public void validateForCreate(
      OwnerCreateDto owner) throws ValidationException {
    LOG.trace("validateForCreate({})", owner);
    List<String> validationErrors = new ArrayList<>();
    if ((owner.firstName() == null || owner.firstName().isBlank())
        && (owner.lastName() == null || owner.lastName().isBlank())
        && (owner.email() == null || owner.email().isBlank())) {
      validationErrors.add("At least first and last name should be provided");
    }
    if (owner.firstName() == null || owner.firstName().isBlank()) {
      validationErrors.add("First name must not be null or blank");
    } else if (owner.firstName().length() > 255) {
      validationErrors.add("First name must not exceed 255 characters");
    }
    if (owner.lastName() == null || owner.lastName().isBlank()) {
      validationErrors.add("Last name must not be null or blank");
    } else if (owner.lastName().length() > 255) {
      validationErrors.add("Last name must not exceed 255 characters");
    }

    if (owner.email() != null && !owner.email().isBlank()) {
      if (!owner.email().matches(EMAIL_REGEX)) {
        validationErrors.add("Email must be a valid email address");
      } else if (owner.email().length() > 255) {
        validationErrors.add("Email must not exceed 255 characters");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner for create failed", validationErrors);
    }
  }
}
