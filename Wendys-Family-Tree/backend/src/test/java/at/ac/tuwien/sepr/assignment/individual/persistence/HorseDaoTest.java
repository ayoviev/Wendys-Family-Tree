package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseDao}, ensuring database operations function correctly.
 */
@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class HorseDaoTest extends TestBase {

  @Autowired
  HorseDao horseDao;

  /**
   * Tests that retrieving all stored horses returns at least one entry
   * and verifies that a specific horse exists in the test dataset.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses)
        .hasSizeGreaterThanOrEqualTo(1)
        .extracting(Horse::id, Horse::name)
        .contains(tuple(-1L, "Wendy"));
  }

  /**
   * Tests that retrieving a horse by its ID returns the correct horse with all expected fields.
   */
  @Test
  public void getByIdReturnsCorrectHorse() throws NotFoundException {
    Horse horse = horseDao.getById(-1L);
    assertThat(horse).isNotNull();
    assertThat(horse.id()).isEqualTo(-1L);
    assertThat(horse.name()).isEqualTo("Wendy");
    assertThat(horse.sex()).isEqualTo(Sex.FEMALE);
    assertThat(horse.dateOfBirth()).isEqualTo(LocalDate.of(2012, 12, 12));
    assertThat(horse.description()).isEqualTo("The famous one!");
  }

  /**
   * Tests that retrieving a horse by a non-existing ID throws a {@link NotFoundException}.
   */
  @Test
  public void getByIdOfNonExistentHorseThrowsNotFoundException() {
    assertThatThrownBy(() -> horseDao.getById(-99L))
        .isInstanceOf(NotFoundException.class);
  }

  /**
   * Tests that creating a horse persists all fields correctly and returns the created horse.
   */
  @Test
  public void createHorsePersistsAllFields() {
    var toCreate = new HorseCreateDto(
        "TestHorse",
        "A test horse",
        LocalDate.of(2020, 1, 1),
        Sex.MALE,
        null, null, null, null);

    Horse created = horseDao.create(toCreate);

    assertThat(created).isNotNull();
    assertThat(created.id()).isPositive();
    assertThat(created.name()).isEqualTo("TestHorse");
    assertThat(created.description()).isEqualTo("A test horse");
    assertThat(created.sex()).isEqualTo(Sex.MALE);
    assertThat(created.dateOfBirth()).isEqualTo(LocalDate.of(2020, 1, 1));
  }

  /**
   * Tests that deleting an existing horse removes it from the database.
   */
  @Test
  public void deleteExistingHorseRemovesItFromDatabase() {
    assertThatNoException().isThrownBy(() -> horseDao.delete(-1L));
    assertThatThrownBy(() -> horseDao.getById(-1L))
        .isInstanceOf(NotFoundException.class);
  }
}
