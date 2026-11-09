package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for horse-related operations, ensuring that all horse data meets
 * the required constraints.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // Dao for the horse, so we can check for parents sex and age
  private final HorseDao horseDao;

  /**
   * Creates a new {@code HorseValidator} with the required DAO.
   *
   * @param horseDao the DAO used to look up parent horses during validation
   */
  public HorseValidator(HorseDao horseDao) {
    this.horseDao = horseDao;
  }

  /**
   * Validates a horse before creation, ensuring all fields meet constraints and
   * checking for conflicts.
   *
   * @param horse the {@link HorseCreateDto} to validate
   * @throws ValidationException if validation fails
   * @throws ConflictException   if conflicts with existing data are detected
   */
  public void validateForCreate(
      HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    checkHorseData(horse.name(), horse.description(), horse.dateOfBirth(), horse.sex(), validationErrors);

    checkParentData(null, horse.motherId(), horse.fatherId(), horse.dateOfBirth(), validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

  }

  /**
   * Validates a horse before update, ensuring all fields meet constraints and
   * checking for conflicts.
   *
   * @param id    the ID of the horse being updated
   * @param horse the {@link HorseUpdateDto} to validate
   * @throws ValidationException if validation fails
   * @throws ConflictException   if conflicts with existing data are detected
   */
  public void validateForUpdate(
      long id,
      HorseUpdateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    checkHorseData(horse.name(), horse.description(), horse.dateOfBirth(), horse.sex(), validationErrors);

    checkParentData(id, horse.motherId(), horse.fatherId(), horse.dateOfBirth(), validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  /**
   * Checks the basic data of a horse, adding any validation errors to the
   * provided list.
   *
   * @param horseName        the name of the horse
   * @param description      the description of the horse
   * @param dateOfBirth      the date of birth of the horse
   * @param horseSex         the sex of the horse
   * @param validationErrors the list to which validation errors will be added
   */
  private void checkHorseData(String horseName, String description, LocalDate dateOfBirth, Sex horseSex,
      List<String> validationErrors) {
    if (description != null) {
      if (description.isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (description.length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horseName == null || horseName.isBlank()) {
      validationErrors.add("Horse name is required and cannot be blank");
    } else if (horseName.length() > 255) {
      validationErrors.add("Horse name too long: longer than 255 characters");
    }

    if (dateOfBirth == null) {
      validationErrors.add("Horse date of birth is required");
    } else if (dateOfBirth.isAfter(LocalDate.now())) {
      validationErrors.add("Horse date of birth cannot be in the future");
    }

    if (horseSex == null) {
      validationErrors.add("Horse sex must not be empty");
    }
  }

  /**
   * Checks the parent data of a horse, adding any validation or conflict errors
   * to the provided list.
   * 
   * @param horseId          the ID of the horse being validated
   * @param motherId         the ID of the mother horse
   * @param fatherId         the ID of the father horse
   * @param childDoB         the date of birth of the horse being validated
   * @param validationErrors the list to which validation errors will be added
   * @throws ConflictException if the sex of the parents is incorrect
   */
  private void checkParentData(Long horseId, Long motherId, Long fatherId, LocalDate childDoB,
      List<String> validationErrors) throws ConflictException {
    List<String> conflictErrors = new ArrayList<>();
    if (motherId != null) {
      if (horseId != null && horseId.equals(motherId)) {
        validationErrors.add("Horse cannot be its own mother");
      } else {
        try {
          Horse mother = horseDao.getById(motherId);
          if (mother.sex() != Sex.FEMALE) {
            conflictErrors.add("The selected mother should be female.");
          }
          if (childDoB != null && mother.dateOfBirth() != null && !mother.dateOfBirth().isBefore(childDoB)) {
            validationErrors.add("The date of birth of the mother should be before the date of birth of the child.");
          }
        } catch (NotFoundException e) {
          conflictErrors.add("No horse with ID %d found for mother".formatted(motherId));
        }
      }
    }
    if (fatherId != null) {
      if (horseId != null && horseId.equals(fatherId)) {
        validationErrors.add("Horse cannot be its own father");
      } else {
        try {
          Horse father = horseDao.getById(fatherId);
          if (father.sex() != Sex.MALE) {
            conflictErrors.add("The selected father should be male.");
          }
          if (childDoB != null && father.dateOfBirth() != null && !father.dateOfBirth().isBefore(childDoB)) {
            validationErrors.add("The date of birth of the father should be before the date of birth of the child.");
          }
        } catch (NotFoundException e) {
          conflictErrors.add("No horse with ID %d found for father".formatted(fatherId));
        }
      }
    }
    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Conflict in parent data", conflictErrors);
    }
  }
}
