package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents an owner in the persistent data store.
 *
 * @param id the unique identifier of the owner
 * @param firstName the first name of the owner
 * @param lastName the last name of the owner
 * @param email the optional email address of the owner
 */
public record Owner(
    Long id,
    String firstName,
    String lastName,
    String email
) {
}
