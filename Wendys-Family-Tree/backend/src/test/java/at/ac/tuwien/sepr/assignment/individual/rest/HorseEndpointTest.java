package at.ac.tuwien.sepr.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.json.JsonMapper;

/**
 * Integration tests for the Horse REST API endpoint.
 */
@ActiveProfiles({"test", "datagen"})
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  private JsonMapper jsonMapper;

  /**
   * Sets up the MockMvc instance before each test.
   */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  /**
   * Tests retrieving all horses from the endpoint.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = jsonMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult)
        .isNotNull()
        .hasSizeGreaterThanOrEqualTo(1)
        .extracting(HorseListDto::id, HorseListDto::name)
        .contains(tuple(-1L, "Wendy"));
  }

  /**
   * Tests that accessing a nonexistent URL returns a 404 status.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  /**
   * Tests that retrieving a horse by its ID returns the correct horse with all expected fields.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void getByIdReturnsCorrectHorse() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-1")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    HorseDetailDto horse = jsonMapper.readValue(body, HorseDetailDto.class);

    assertThat(horse).isNotNull();
    assertThat(horse.id()).isEqualTo(-1L);
    assertThat(horse.name()).isEqualTo("Wendy");
    assertThat(horse.sex()).isEqualTo(Sex.FEMALE);
    assertThat(horse.dateOfBirth()).isEqualTo(LocalDate.of(2012, 12, 12));
  }

  /**
   * Tests that requesting a non-existing horse by ID returns a 404 status.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void getByIdOfNonExistentHorseReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-99")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
  }

  /**
   * Tests that creating a valid horse via POST returns 201 with the correct horse data.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void createValidHorseReturns201WithCorrectData() throws Exception {
    var toCreate = new HorseCreateDto(
        "Rocky",
        "A strong stallion",
        LocalDate.of(2015, 3, 20),
        Sex.MALE,
        null, null, null, null);

    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(toCreate))
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsByteArray();

    HorseDetailDto created = jsonMapper.readValue(body, HorseDetailDto.class);

    assertThat(created).isNotNull();
    assertThat(created.id()).isNotNull();
    assertThat(created.name()).isEqualTo("Rocky");
    assertThat(created.sex()).isEqualTo(Sex.MALE);
    assertThat(created.dateOfBirth()).isEqualTo(LocalDate.of(2015, 3, 20));
  }

  /**
   * Tests that creating a horse with a null name returns a 422 Unprocessable Entity status.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void createHorseWithNullNameReturns422() throws Exception {
    var toCreate = new HorseCreateDto(
        null,
        "Some description",
        LocalDate.of(2020, 1, 1),
        Sex.FEMALE,
        null, null, null, null);

    mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(toCreate))
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
  }
}
