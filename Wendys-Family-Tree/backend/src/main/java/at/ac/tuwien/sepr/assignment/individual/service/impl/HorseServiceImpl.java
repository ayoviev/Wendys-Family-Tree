package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HorseService} for handling image storage and
 * retrieval.
 */
@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  /**
   * Creates a new {@link HorseServiceImpl} with the required dependencies.
   *
   * @param dao the data access object for horse persistence
   * @param mapper the mapper for converting between horse entities and DTOs
   * @param validator the validator for horse data
   * @param ownerService the service for owner-related operations
   */
  @Autowired
  public HorseServiceImpl(HorseDao dao,
      HorseMapper mapper,
      HorseValidator validator,
      OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    // We get all the horses
    var horses = dao.getAll();
    // We get all the owners of the horses in one go, to avoid doing it for each horse separately
    var ownerIds = horses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }

    // Converts entities to DTO
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  @Override
  public HorseDetailDto create(
      HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    var newHorse = dao.create(
        horse);
    var ownerMap = ownerMapForSingleId(newHorse.ownerId());
    var parentMap = parentMapForSingleId(newHorse);
    return mapper.entityToDetailDto(
        newHorse,
        ownerMap,
        parentMap);
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.ownerId()),
        parentMapForSingleId(horse));
  }

  private Map<Long, HorseListDto> parentMapForSingleId(Horse horse) {
    Map<Long, HorseListDto> parentMap = new java.util.HashMap<>();
    try {
      if (horse.motherId() != null) {
        Horse mother = dao.getById(horse.motherId());
        parentMap.put(horse.motherId(), new HorseListDto(
            mother.id(),
            mother.name(),
            mother.description(),
            mother.dateOfBirth(),
            mother.sex(),
            // We don't need to know the owner, to find the parent.
            null));
      }
      if (horse.fatherId() != null) {
        Horse father = dao.getById(horse.fatherId());
        parentMap.put(horse.fatherId(), new HorseListDto(
            father.id(),
            father.name(),
            father.description(),
            father.dateOfBirth(),
            father.sex(),
            null));
      }
    } catch (NotFoundException e) {
      throw new FatalException("Parent horse not found", e);
    }
    return parentMap;
  }

  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? Collections.emptyMap() // had problems, when it was null
          // fetches the owner from the database by ID
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId), e);
    }
  }

  @Override
  public HorseDetailDto update(
      long id, HorseUpdateDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({}, {})", id, horse);
    validator.validateForUpdate(id, horse);
    var updatedHorse = dao.update(id, horse);
    var ownerMap = ownerMapForSingleId(updatedHorse.ownerId());
    var parentMap = parentMapForSingleId(updatedHorse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMap,
        parentMap);
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    dao.delete(id);
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    var horses = dao.search(searchParams);
    var ownerIds = horses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Owner(s) referenced by horse(s) not found", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  @Override
  public HorseTreeDto getHorseTree(long id, int generations) throws NotFoundException {
    LOG.trace("getHorseTree({}, {})", id, generations);
    Horse horse = dao.getById(id);
    HorseTreeDto father = null;
    HorseTreeDto mother = null;

    if (generations > 0) {
      if (horse.motherId() != null) {
        mother = getHorseTree(horse.motherId(), generations - 1);
      }
      if (horse.fatherId() != null) {
        father = getHorseTree(horse.fatherId(), generations - 1);
      }
    }
    return new HorseTreeDto(
        horse.id(),
        horse.name(),
        horse.dateOfBirth(),
        father,
        mother);
  }
}
