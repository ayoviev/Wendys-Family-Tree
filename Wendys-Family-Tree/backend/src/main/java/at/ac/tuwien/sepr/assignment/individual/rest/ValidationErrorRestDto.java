package at.ac.tuwien.sepr.assignment.individual.rest;

import java.util.List;

/**
 * Represents a Data Transfer Object (DTO) for validation errors returned in REST responses.
 * This record encapsulates a summary message along with a list of specific validation errors.
 *
 * @param message a brief summary of the validation failure
 * @param errors  the list of individual validation error messages
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
