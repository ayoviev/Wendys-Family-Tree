package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Represents a Data Transfer Object (DTO) for creating a new owner.
 * This record encapsulates the required fields for registering an owner in the system.
 *
 * @param firstName the first name of the owner
 * @param lastName the last name of the owner
 * @param email the optional email address of the owner
 */
public record OwnerCreateDto(
    String firstName,
    String lastName,
    String email
) {
}
