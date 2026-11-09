package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store
 * regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();

  /**
   * Create a horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the data to use to create the horse
   * @return the created horse
   */
  Horse create(HorseCreateDto horse);

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in
   *                           the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Update the horse with the given ID in the persistent data store with the data
   * given in {@code horse}.
   *
   * @param id    the ID of the horse to update
   * @param horse the data to use to update the horse
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in
   *                           the persistent data store
   */
  Horse update(long id, HorseUpdateDto horse) throws NotFoundException;

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
  List<Horse> search(HorseSearchDto searchParams);

  /**
   * Get the family tree of the horse with the given ID, up to the given maximum depth.
   * The family tree includes the horse itself, its parents, its grandparents, and so on, until the max depth is reached.
   * 
   * @param id the ID of the horse to get the family tree for
   * @param generations the maximum number of generations to retrieve (0 means only the horse itself, 1 includes parents, 2 includes grandparents, etc.)
   * @return the family tree of the horse with the given ID, up to the given maximum number of generations
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  List<Horse> getFamilyTree(long id, int generations) throws NotFoundException;
}
