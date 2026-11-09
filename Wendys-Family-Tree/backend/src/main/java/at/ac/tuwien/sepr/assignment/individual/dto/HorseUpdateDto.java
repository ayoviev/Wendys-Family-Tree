package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

/**
 * Represents a Data Transfer Object (DTO) for updating a horse entry.
 * This record encapsulates all necessary details for updating a horse.
 *
 * @param name the updated name of the horse
 * @param description the updated description, or {@code null} to remove it
 * @param dateOfBirth the updated date of birth
 * @param sex the updated sex of the horse
 * @param image the updated image as a Base64 data URL, or {@code null}
 * @param ownerId the updated owner ID, or {@code null} to remove the owner
 * @param motherId the updated mother ID, or {@code null}
 * @param fatherId the updated father ID, or {@code null}
 */
public record HorseUpdateDto(
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    String image,
    Long ownerId,
    Long motherId,
    Long fatherId
) {  
}
