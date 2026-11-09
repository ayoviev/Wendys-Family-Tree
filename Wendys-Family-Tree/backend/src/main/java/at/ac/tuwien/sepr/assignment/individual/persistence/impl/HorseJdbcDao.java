package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementation of {@link HorseDao} for interacting with the database.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";

  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME
      + " WHERE ID = :id";

  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME
      + " (name, description, date_of_birth, sex, image, owner_id, mother_id, father_id)"
      + " VALUES (:name, :description, :date_of_birth, :sex, :image, :owner_id, :mother_id, :father_id)";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = :name, description = :description, date_of_birth = :date_of_birth,"
      + " sex = :sex, image = :image, owner_id = :owner_id, mother_id = :mother_id, father_id = :father_id"
      + " WHERE id = :id";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME
      + " WHERE id = :id";

  private static final String SQL_TREE_BUILD = " WITH RECURSIVE ancestors AS ( "
      + " SELECT h.*, 0 AS generation FROM " + TABLE_NAME + " h WHERE h.id = :id"
      + " UNION ALL "
      + " SELECT h.*, a.generation + 1 AS generation FROM " + TABLE_NAME + " h"
      + " JOIN ancestors a ON h.id = a.mother_id OR h.id = a.father_id"
      + " WHERE a.generation < :generations)"
      + " SELECT * FROM ancestors";

  private final JdbcClient jdbcClient;

  /**
   * Creates a new {@code HorseJdbcDao} with the required JDBC client.
   *
   * @param jdbcClient the JDBC client used for database operations
   */
  @Autowired
  public HorseJdbcDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcClient
        .sql(SQL_SELECT_ALL)
        .query(this::mapRow)
        .list();
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses = jdbcClient
        .sql(SQL_SELECT_BY_ID)
        // We replace id with the acutal value
        .param("id", id)
        .query(this::mapRow)
        .list();

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();
  }

  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);
    LOG.info("Image in DAO: {}", horse.image() != null ? "YES length=" + horse.image().length() : "NO");

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    int created = jdbcClient
        .sql(SQL_INSERT)
        .param("name", horse.name())
        .param("description", horse.description())
        .param("date_of_birth", horse.dateOfBirth())
        .param("sex", horse.sex().toString())
        .param("image", horse.image())
        .param("owner_id", horse.ownerId())
        .param("mother_id", horse.motherId())
        .param("father_id", horse.fatherId())
        .update(keyHolder);

    // Checks if exactly one row was inserted
    if (created != 1) {
      throw new FatalException("%d horses inserted, expected exactly 1".formatted(created));
    }

    Number key = keyHolder.getKey();

    if (key == null) {
      throw new FatalException("No generated key returned for inserted horse.");
    }

    long generatedId = key.longValue();
    return new Horse(
        generatedId,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        horse.ownerId(),
        horse.image(),
        horse.motherId(),
        horse.fatherId());
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse(
        result.getLong("id"),
        result.getString("name"),
        result.getString("description"),
        result.getDate("date_of_birth").toLocalDate(),
        Sex.valueOf(result.getString("sex")),
        result.getObject("owner_id", Long.class),
        result.getString("image"),
        result.getObject("mother_id", Long.class),
        result.getObject("father_id", Long.class));
  }

  /**
   * Updates an existing horse in the persistent data store.
   *
   * @param id    the unique identifier of the horse to be updated
   * @param horse the data transfer object containing the new values for the horse
   * @return the completely updated {@link Horse} entity as it is now saved in the
   *         database
   * @throws NotFoundException if no horse with the given ID exists in the data
   *                           store
   */
  @Override
  public Horse update(long id, HorseUpdateDto horse) throws NotFoundException {
    LOG.trace("update({}, {})", id, horse);

    int updated = jdbcClient
        .sql(SQL_UPDATE)
        .param("name", horse.name())
        .param("description", horse.description())
        .param("date_of_birth", horse.dateOfBirth())
        .param("sex", horse.sex().toString())
        .param("image", horse.image())
        .param("owner_id", horse.ownerId())
        .param("mother_id", horse.motherId())
        .param("father_id", horse.fatherId())
        .param("id", id)
        .update();

    if (updated == 0) {
      throw new NotFoundException("No horse with ID %d found to update".formatted(id));
    }
    return getById(id);
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);

    int deleted = jdbcClient
        .sql(SQL_DELETE)
        .param("id", id)
        .update();

    if (deleted == 0) {
      throw new NotFoundException("No horse with ID %d found to delete".formatted(id));
    }
  }

  @Override
  public List<Horse> search(HorseSearchDto searchParams) {
    LOG.trace("search({})", searchParams);

    // To include even the horse, without owners.
    StringBuilder sqlQuery = new StringBuilder(
        "SELECT h.* FROM horse h "
            + "LEFT JOIN owner o ON h.owner_id = o.id "
            + "WHERE 1=1");

    MapSqlParameterSource params = new MapSqlParameterSource();

    if (searchParams.name() != null && !searchParams.name().isBlank()) {
      sqlQuery.append(" AND LOWER(h.name) LIKE LOWER(:name ||'%')");
      params.addValue("name", searchParams.name());
    }

    if (searchParams.description() != null && !searchParams.description().isBlank()) {
      sqlQuery.append(" AND LOWER(h.description) LIKE LOWER('%'|| :description ||'%')");
      params.addValue("description", searchParams.description());
    }

    if (searchParams.bornBefore() != null) {
      sqlQuery.append(" AND h.date_of_birth < :bornBefore");
      params.addValue("bornBefore", searchParams.bornBefore());
    }

    if (searchParams.sex() != null) {
      sqlQuery.append(" AND h.sex = :sex");
      params.addValue("sex", searchParams.sex().toString());
    }

    if (searchParams.ownerName() != null && !searchParams.ownerName().isBlank()) {
      sqlQuery.append(" AND LOWER(o.first_name || ' ' || o.last_name) LIKE LOWER('%'|| :ownerName ||'%')");
      params.addValue("ownerName", searchParams.ownerName());
    }

    if (searchParams.limit() != null) {
      sqlQuery.append(" LIMIT :limit");
      params.addValue("limit", searchParams.limit());
    }
    return jdbcClient
        .sql(sqlQuery.toString())
        .paramSource(params)
        .query(this::mapRow)
        .list();
  }

  @Override
  public List<Horse> getFamilyTree(long id, int generations) throws NotFoundException {
    LOG.trace("getFamilyTree({}, {})", id, generations);

    // Veifying if a horse exists
    Horse horse = getById(id);
    if (horse == null) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    return jdbcClient
        .sql(SQL_TREE_BUILD)
        .param("id", id)
        .param("generations", generations)
        .query(this::mapRow)
        .list();
  }
}
