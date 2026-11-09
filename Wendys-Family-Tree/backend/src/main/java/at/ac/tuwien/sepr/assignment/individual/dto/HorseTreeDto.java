package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a horse and its ancestry in a family tree structure.
 * Each node contains the horse's basic data and optionally references its father and mother,
 * recursively up to a configured maximum number of generations.
 *
 * @param id the unique identifier of the horse
 * @param name the name of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param father the paternal ancestor subtree, or {@code null} if none exists or max depth is reached
 * @param mother the maternal ancestor subtree, or {@code null} if none exists or max depth is reached
 */
public record HorseTreeDto(
    Long id,
    String name,
    LocalDate dateOfBirth,
    HorseTreeDto father,
    HorseTreeDto mother
) {
}
