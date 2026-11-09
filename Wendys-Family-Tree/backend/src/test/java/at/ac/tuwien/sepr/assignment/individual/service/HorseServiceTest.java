package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseService}.
 */
@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class HorseServiceTest extends TestBase {

  @Autowired
  HorseService horseService;

  /**
   * Tests whether retrieving all stored horses returns the expected number and specific entries.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses().toList();

    assertThat(horses)
        .hasSizeGreaterThanOrEqualTo(1)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  /**
   * Tests that retrieving a horse by its ID returns the correct horse with all expected fields.
   */
  @Test
  public void getByIdReturnsCorrectHorse() throws NotFoundException {
    HorseDetailDto horse = horseService.getById(-1L);

    assertThat(horse).isNotNull();
    assertThat(horse.id()).isEqualTo(-1L);
    assertThat(horse.name()).isEqualTo("Wendy");
    assertThat(horse.sex()).isEqualTo(Sex.FEMALE);
    assertThat(horse.dateOfBirth()).isEqualTo(LocalDate.of(2012, 12, 12));
  }

  /**
   * Tests that retrieving a non-existing horse by ID throws a {@link NotFoundException}.
   */
  @Test
  public void getByIdOfNonExistentHorseThrowsNotFoundException() {
    assertThatThrownBy(() -> horseService.getById(-99L))
        .isInstanceOf(NotFoundException.class);
  }

  /**
   * Tests that creating a horse with valid data returns the created horse with correct fields.
   */
  @Test
  public void createHorseWithValidDataReturnsCreatedHorse() throws ValidationException, ConflictException {
    var toCreate = new HorseCreateDto(
        "Bella",
        "A beautiful mare",
        LocalDate.of(2018, 5, 15),
        Sex.FEMALE,
        null, null, null, null);

    HorseDetailDto created = horseService.create(toCreate);

    assertThat(created).isNotNull();
    assertThat(created.id()).isNotNull();
    assertThat(created.name()).isEqualTo("Bella");
    assertThat(created.description()).isEqualTo("A beautiful mare");
    assertThat(created.sex()).isEqualTo(Sex.FEMALE);
    assertThat(created.dateOfBirth()).isEqualTo(LocalDate.of(2018, 5, 15));
  }

  /**
   * Tests that creating a horse with a null name throws a {@link ValidationException}.
   */
  @Test
  public void createHorseWithNullNameThrowsValidationException() {
    var toCreate = new HorseCreateDto(
        null,
        "Some description",
        LocalDate.of(2020, 1, 1),
        Sex.MALE,
        null, null, null, null);

    assertThatThrownBy(() -> horseService.create(toCreate))
        .isInstanceOf(ValidationException.class);
  }
}
