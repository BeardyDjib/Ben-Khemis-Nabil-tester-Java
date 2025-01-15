import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAOTest {

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    }

    @Test
    void testGetNextAvailableSlot_Success() throws Exception {
        // GIVEN: Simulate a successful database query
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // WHEN: Get the next available slot
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN: Verify that the result is correct
        assertEquals(1, result, "The next available slot should be 1");
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetNextAvailableSlot_ConnectionError() throws Exception {
        // GIVEN: Simulate a connection error
        when(dataBaseConfig.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // WHEN: Get the next available slot
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN: Verify that the result is -1 (error case)
        assertEquals(-1, result, "The result should be -1 in case of error");
    }
    @Test
    void testGetNextAvailableSlot_NoAvailableSlot() throws Exception {
        // GIVEN: Simulate a query with no results
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows returned

        // WHEN: Get the next available slot
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN: Verify that the result is -1 (no available slot)
        assertEquals(-1, result, "The result should be -1 if no slot is available");
        verify(preparedStatement, times(1)).executeQuery();
    }
    @Test
    void testUpdateParking_Success() throws Exception {
        // GIVEN: Simulate a successful update
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); // Simulate a parking spot

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate 1 row updated

        // WHEN: Update the parking spot
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN: Verify that the update was successful
        assertTrue(result, "The parking spot should be updated successfully");
        verify(preparedStatement, times(1)).executeUpdate();
    }
    @Test
    void testUpdateParking_ConnectionError() throws Exception {
        // GIVEN: Simulate a connection error
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); // Simulate a parking spot

        when(dataBaseConfig.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // WHEN: Update the parking spot
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN: Verify that the update failed
        assertFalse(result, "The update should fail in case of connection error");
    }
    @Test
    void testUpdateParking_NoUpdate() throws Exception {
        // GIVEN: Simulate an update with no rows affected
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); // Simulate a parking spot

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0); // Simulate 0 rows updated

        // WHEN: Update the parking spot
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN: Verify that the update failed
        assertFalse(result, "The update should fail if no rows are updated");
        verify(preparedStatement, times(1)).executeUpdate();
    }
    @Test
    void testUpdateParking_Parameters() throws Exception {
        // GIVEN: Simulate a successful update
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // WHEN: Update the parking spot
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN: Verify that the parameters are set correctly
        assertTrue(result, "The parking spot should be updated successfully");
        verify(preparedStatement).setBoolean(1, parkingSpot.isAvailable());
        verify(preparedStatement).setInt(2, parkingSpot.getId());
        verify(preparedStatement, times(1)).executeUpdate();
    }

}