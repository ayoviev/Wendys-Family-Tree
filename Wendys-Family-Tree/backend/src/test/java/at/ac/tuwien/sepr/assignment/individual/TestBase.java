package at.ac.tuwien.sepr.assignment.individual;

import at.ac.tuwien.sepr.assignment.individual.persistence.DataGeneratorBean;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for integration tests that require a populated database.
 * Sets up test data before each test and clears it afterwards.
 */
public abstract class TestBase {
  @Autowired
  DataGeneratorBean dataGenerator;

  @BeforeEach
  public void setupDb() throws SQLException {
    dataGenerator.generateData();
  }

  @AfterEach
  public void tearDownDb() throws SQLException {
    dataGenerator.clearData();
  }
}
