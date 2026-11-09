package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();

  /**
   * Creates a horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the horse could not be created because the
   *                             data given is in itself incorrect (description
   *                             too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in
   *                             conflict the data currently in the system (owner
   *                             does not exist, …)
   */
  HorseDetailDto create(
      HorseCreateDto horse) throws ValidationException, ConflictException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in
   *                           the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Update the horse with the given ID in the persistent data store with the data
   * given in {@code horse}.
   *
   * @param id    the ID of the horse to update
   * @param horse the data to use to update the horse
   * @return the updated horse
   * @throws NotFoundException   if the Horse with the given ID does not exist in
   *                             the persistent data store
   * @throws ValidationException if the horse could not be updated because the
   *                             data given is in itself incorrect (description
   *                             too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in
   *                             conflict the data currently in the system (owner
   *                             does not exist, …)
   */
  HorseDetailDto update(long id, HorseUpdateDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Delete the horse with the given ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the Horse with the given ID does not exist in
   *                           the persistent data store
   */
  void delete(long id) throws NotFoundException;

  /**
   * Search for horses in the persistent data store matching the criteria given in
   * {@code searchParams}.
   *
   * @param searchParams the criteria to search for
   * @return a list of horses matching the search criteria
   */
  Stream<HorseListDto> search(HorseSearchDto searchParams);

  /**
   * Get the family tree of the horse with the given ID, up to the given maximum number of generations.
   * The family tree includes the horse itself, its parents, its grandparents, and so on,
   * until the maximum number of generations is reached.
   *
   * @param id the ID of the horse to get the family tree for
   * @param generations the maximum number of generations to retrieve
   *     (0 means only the horse itself, 1 includes parents, 2 includes grandparents, etc.)
   * @return the family tree of the horse with the given ID, up to the given maximum number of generations
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseTreeDto getHorseTree(long id, int generations) throws NotFoundException;
}
