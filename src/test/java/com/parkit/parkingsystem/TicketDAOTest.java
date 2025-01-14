import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

public class TicketDAOTest {

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig;
    }

    @Test
    void testSaveTicket_Success() throws Exception {
        // GIVEN: Simulate a successful save
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(1.5);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);

        // WHEN: Save the ticket
        boolean result = ticketDAO.saveTicket(ticket);

        // THEN: Verify that the save was successful
        assertTrue(result, "The ticket should be saved successfully");
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void testSaveTicket_ConnectionError() throws Exception {
        // GIVEN: Simulate a connection error
        Ticket ticket = new Ticket();
        when(dataBaseConfig.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // WHEN: Save the ticket
        boolean result = ticketDAO.saveTicket(ticket);

        // THEN: Verify that the save failed
        assertFalse(result, "The save should fail in case of error");
    }
    @Test
    void testUpdateTicket_Success() throws Exception {
        // GIVEN: Create a ticket and simulate a successful update
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(2.0);
        ticket.setOutTime(new Date());

        // Simulate the connection and prepare the statement
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true); // Simulate a successful execution

        // WHEN: Call the updateTicket method
        boolean result = ticketDAO.updateTicket(ticket);

        // THEN: Verify that the ticket was updated successfully
        assertTrue(result, "The ticket should be updated successfully");
        verify(preparedStatement, times(1)).execute(); // Verify that execute() was called
    }
    @Test
    void testUpdateTicket_ConnectionError() throws Exception {
        // GIVEN: Simulate a connection error
        Ticket ticket = new Ticket();
        when(dataBaseConfig.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // WHEN: Call the updateTicket method
        boolean result = ticketDAO.updateTicket(ticket);

        // THEN: Verify that the update failed
        assertFalse(result, "The update should fail in case of connection error");
    }
    @Test
    void testSaveTicket_InvalidInput() throws Exception {
        // GIVEN: A null ticket
        Ticket ticket = null;

        // WHEN: Try to save the null ticket
        boolean result = ticketDAO.saveTicket(ticket);

        // THEN: Verify that the save fails
        assertFalse(result, "The save should fail for a null ticket");
    }
    @Test
    void testUpdateTicket_NullOutTime() throws Exception {
        // GIVEN: A ticket with no out time
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(2.0);
        ticket.setOutTime(null);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

        // WHEN: Call updateTicket
        boolean result = ticketDAO.updateTicket(ticket);

        // THEN: Verify that the update fails
        assertFalse(result, "The update should fail for a ticket with null out time");
    }



}