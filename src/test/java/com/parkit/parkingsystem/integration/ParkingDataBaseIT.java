package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    @Mock
    private InputReaderUtil inputReaderUtil;

    @Mock
    private ParkingSpotDAO parkingSpotDAO;

    @Mock
    private TicketDAO ticketDAO;

    @InjectMocks
    private ParkingService parkingService;

    @BeforeEach
    public void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    public void testParkingACar() throws Exception {
        // GIVEN: Simulate user input and an available parking spot
        when(inputReaderUtil.readSelection()).thenReturn(1); // CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot availableSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // WHEN: The vehicle is registered
        parkingService.processIncomingVehicle();

        // THEN: Verify ticket and parking spot updates
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testParkingLotExit() throws Exception {
        // GIVEN: Simulate an existing ticket
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 hour ago
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        ticket.setVehicleRegNumber("ABCDEF");

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        // WHEN: The vehicle exits the parking
        parkingService.processExitingVehicle();

        // THEN: Verify ticket updates and fare calculation
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        assertNotNull(ticket.getOutTime(), "Out time should be populated");
        assertTrue(ticket.getPrice() > 0, "Fare should be calculated and greater than 0");
    }

    @Test
    void testParkingLotExitRecurringUser() throws Exception {
        // GIVEN: Simulate a recurring user with previous tickets
        String recurringVehicleRegNumber = "ABCDEF";

        // Simulate user input
        when(inputReaderUtil.readSelection()).thenReturn(1); // Simulate selection of "CAR"
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(recurringVehicleRegNumber);

        // Simulate an available parking spot
        ParkingSpot availableSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // Simulate a ticket for the recurring user
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000))); // 2 hours ago
        ticket.setParkingSpot(availableSpot);
        ticket.setVehicleRegNumber(recurringVehicleRegNumber);
        ticket.setPrice(0);

        // Simulate the recurring user having 2 previous tickets
        when(ticketDAO.getNbTicket(recurringVehicleRegNumber)).thenReturn(2);
        when(ticketDAO.getTicket(recurringVehicleRegNumber)).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        // WHEN: The vehicle enters and exits the parking
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        // THEN: Verify that the price is reduced by 5% for the recurring user
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        assertNotNull(ticket.getOutTime(), "Out time should be populated");

        // Expected price: 1.5 * 2 = 3, then 3 * 0.95 = 2.85
        double expectedPrice = 2.85;
        double actualPrice = ticket.getPrice();
        assertEquals(expectedPrice, actualPrice, 0.01, "The price should be reduced by 5% for recurring users");
    }
}