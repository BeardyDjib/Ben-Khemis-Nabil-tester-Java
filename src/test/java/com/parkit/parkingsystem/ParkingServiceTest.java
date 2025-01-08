package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ParkingServiceTest {

    private ParkingService parkingService;

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        // Initialize mocks manually
        inputReaderUtil = Mockito.mock(InputReaderUtil.class);
        parkingSpotDAO = Mockito.mock(ParkingSpotDAO.class);
        ticketDAO = Mockito.mock(TicketDAO.class);

        // Simulate reading a vehicle registration number
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            fail("Failed to stub readVehicleRegistrationNumber: " + e.getMessage());
        }

        // Initialize the ParkingService with the mocks
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
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

    @Test
    void testProcessIncomingVehicle() {
        // GIVEN: Prepare the context for this specific test
        int parkingSpotId = 1; // ID of the available parking spot
        ParkingType parkingType = ParkingType.CAR; // Type of vehicle

        // Simulate that a parking spot is available
        when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(parkingSpotId);

        // Create a ParkingSpot object using the ID
        ParkingSpot parkingSpot = new ParkingSpot(parkingSpotId, parkingType, true); // Available parking spot

        // Simulate reading the vehicle registration number
        String vehicleRegNumber = "ABCDEF";
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        } catch (Exception e) {
            fail("Failed to stub readVehicleRegistrationNumber: " + e.getMessage());
        }

        // Simulate selecting the vehicle type (CAR)
        when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR

        // Simulate that the vehicle has no previous tickets (new customer)
        when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(0);

        // WHEN: Execute the method under test
        parkingService.processIncomingVehicle();

        // THEN: Verify the interactions and results
        verify(parkingSpotDAO).updateParking(parkingSpot); // Verify that the spot is marked as occupied
        verify(ticketDAO).saveTicket(any(Ticket.class)); // Verify that a ticket is saved
        verify(ticketDAO).getNbTicket(vehicleRegNumber); // Verify that the number of tickets is checked
    }

    @Test
    void processExitingVehicleTestUnableUpdate() {
        // GIVEN: Prepare the context for this specific test
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); // Occupied parking spot
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 hour ago
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        // Simulate that getTicket() returns this ticket
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);

        // Simulate that updateTicket() returns false (update failed)
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        // Simulate reading the vehicle registration number
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            fail("Failed to stub readVehicleRegistrationNumber: " + e.getMessage());
        }

        // WHEN: Execute the method under test
        parkingService.processExitingVehicle();

        // THEN: Verify the interactions and results
        verify(ticketDAO).getTicket("ABCDEF"); // Verify that getTicket() was called
        verify(ticketDAO).updateTicket(any(Ticket.class)); // Verify that updateTicket() was called
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class)); // Verify that updateParking() was never called
        try {
            verify(inputReaderUtil).readVehicleRegistrationNumber(); // Verify that readVehicleRegistrationNumber() was called
        } catch (Exception e) {
            fail("Failed to verify readVehicleRegistrationNumber: " + e.getMessage());
        }
    }

    @Test
    void testGetNextParkingNumberIfAvailable() {
        // GIVEN
        int parkingNumber = 1;
        when(inputReaderUtil.readSelection()).thenReturn(1); // Simulate user selecting CAR
        ParkingType parkingType = ParkingType.CAR;
        when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(parkingNumber); // Simulate a spot being available

        // WHEN
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable(); // Single call to the method

        // THEN
        assertNotNull(result); // Verify that the result is not null
        assertThat(result.getId()).isEqualTo(1); // Verify that the ID is 1
        assertThat(result.getParkingType()).isEqualTo(ParkingType.CAR); // Verify that the type is CAR
        assertThat(result.isAvailable()).isTrue(); // Verify that the spot is available

        verify(parkingSpotDAO).getNextAvailableSlot(parkingType); // Verify that the method was called once
        verifyNoMoreInteractions(parkingSpotDAO); // Verify that no other interactions occurred
    }

    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        // GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1); // Simulate user selecting CAR
        ParkingType parkingType = ParkingType.CAR;
        when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(0); // Simulate no spot available

        // WHEN
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable(); // Call the method

        // THEN
        assertNull(result); // Verify that the result is null
        verify(parkingSpotDAO).getNextAvailableSlot(parkingType); // Verify that the method was called once
        verifyNoMoreInteractions(parkingSpotDAO); // Verify that no other interactions occurred
    }

    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(3);

        // WHEN
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable(); // Call the method

        // THEN
        assertNull(result, "method should return null");
        verifyNoMoreInteractions(parkingSpotDAO);


    }

}