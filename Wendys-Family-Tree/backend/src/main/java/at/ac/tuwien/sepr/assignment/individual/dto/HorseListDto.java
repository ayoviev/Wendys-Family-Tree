package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for loading a list of horses.
 * This record encapsulates essential horse attributes required for listing.
 *
 * @param id the unique identifier of the horse
 * @param name the name of the horse
 * @param description an optional description of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param sex the sex of the horse
 * @param owner the owner of the horse, or {@code null} if unowned
 */
public record HorseListDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner
) {
}
