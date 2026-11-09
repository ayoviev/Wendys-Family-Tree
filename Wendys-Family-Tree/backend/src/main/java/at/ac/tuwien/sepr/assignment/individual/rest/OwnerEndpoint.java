package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing owner-related operations.
 * Provides endpoints for searching and creating owners.
 */
@RestController
@RequestMapping(OwnerEndpoint.BASE_PATH)
public class OwnerEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/owners";

  private final OwnerService service;

  /**
   * Creates a new {@code OwnerEndpoint} with the required service.
   *
   * @param service the owner service to delegate operations to
   */
  public OwnerEndpoint(OwnerService service) {
    this.service = service;
  }

  /**
   * Searches for owners based on the given search parameters.
   *
   * @param searchParameters the parameters to filter the owner search
   * @return a stream of {@link OwnerDto} matching the search criteria
   */
  @GetMapping
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH + " query parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Returns all owners stored in the system.
   *
   * @return a stream of all {@link OwnerDto} objects
   */
  @GetMapping("/all")
  public Stream<OwnerDto> getAllOwners() {
    LOG.info("GET " + BASE_PATH + "/all");
    return service.getAll();
  }

  /**
   * Creates a new owner based on the provided data.
   *
   * @param newOwner the details of the owner to create
   * @return the created {@link OwnerDto}
   * @throws ValidationException if validation fails
   */
  @PostMapping
  public OwnerDto create(
      @RequestBody OwnerCreateDto newOwner) throws ValidationException {

    LOG.info("POST " + BASE_PATH);
    LOG.debug("request body: {}", newOwner);
    return service.create(
        newOwner);
  }


}
