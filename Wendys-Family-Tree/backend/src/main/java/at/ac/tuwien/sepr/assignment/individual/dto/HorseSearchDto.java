package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name optional name prefix to filter by
 * @param description optional description substring to filter by
 * @param bornBefore optional date; only horses born before this date are returned
 * @param sex optional sex to filter by
 * @param ownerName optional owner name substring to filter by
 * @param limit optional maximum number of results to return
 */
public record HorseSearchDto(
    String name,
    String description,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornBefore,
    Sex sex,
    String ownerName,
    Integer limit
) {
}
