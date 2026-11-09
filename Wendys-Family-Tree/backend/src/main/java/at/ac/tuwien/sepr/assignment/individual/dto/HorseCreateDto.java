package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for creating a new horse entry.
 * This record encapsulates all necessary details for registering a horse.
 *
 * @param name the name of the horse
 * @param description an optional description of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param sex the sex of the horse
 * @param ownerId the ID of the owner, or {@code null} if unowned
 * @param image the image as a Base64 data URL, or {@code null}
 * @param motherId the ID of the mother horse, or {@code null}
 * @param fatherId the ID of the father horse, or {@code null}
 */
public record HorseCreateDto(
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    Long ownerId,
    String image,
    Long motherId,
    Long fatherId
) {
}
