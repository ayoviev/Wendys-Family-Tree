package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for detailed horse information.
 * This record provides all necessary details about a horse.
 *
 * @param id the unique identifier of the horse
 * @param name the name of the horse
 * @param description an optional description of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param sex the sex of the horse
 * @param image the image as a Base64 data URL, or {@code null}
 * @param owner the owner of the horse, or {@code null} if unowned
 * @param mother the mother of the horse as a list entry, or {@code null}
 * @param father the father of the horse as a list entry, or {@code null}
 */
public record HorseDetailDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    String image,
    OwnerDto owner,
    HorseListDto mother,
    HorseListDto father
) {
}
