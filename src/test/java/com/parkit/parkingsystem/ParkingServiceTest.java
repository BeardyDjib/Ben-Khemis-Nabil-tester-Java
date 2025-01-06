package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private ParkingService parkingService; // Non static

    @Mock
    private InputReaderUtil inputReaderUtil; // Non static
    @Mock
    private ParkingSpotDAO parkingSpotDAO; // Non static
    @Mock
    private TicketDAO ticketDAO; // Non static

    @BeforeEach
    public void setUpPerTest() {
        try {
            // Simulate reading a vehicle registration number (used in all tests)
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            // Initialize the ParkingService with the mocks
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
        // GIVEN: Prepare the context for this specific test
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 hour ago
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        // Simulate methods for this specific test
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2); // Mocking getNbTicket()
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        // Simulate reading the vehicle registration number
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            fail("Failed to stub readVehicleRegistrationNumber: " + e.getMessage());
        }

        // WHEN: Execute the action to test
        try {
            parkingService.processExitingVehicle();
        } catch (Exception e) {
            // THEN: Fail the test if an exception is thrown
            fail("processExitingVehicle should not throw an exception: " + e.getMessage());
        }

        // THEN: Verify the result
        verify(ticketDAO).getTicket("ABCDEF"); // Verify that getTicket() was called
        verify(ticketDAO).getNbTicket("ABCDEF"); // Verify that getNbTicket() was called
        verify(ticketDAO).updateTicket(any(Ticket.class)); // Verify that updateTicket() was called
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class)); // Verify that updateParking() was called
        try {
            verify(inputReaderUtil).readVehicleRegistrationNumber(); // Verify that readVehicleRegistrationNumber() was called
        } catch (Exception e) {
            fail("Failed to verify readVehicleRegistrationNumber: " + e.getMessage());
        }
    }
}




