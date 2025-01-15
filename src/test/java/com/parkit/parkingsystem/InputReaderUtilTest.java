package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InputReaderUtilTest {

    private InputStream originalSystemIn; // To save the original System.in

    @BeforeEach
    void setUp() {
        // Save the original System.in
        originalSystemIn = System.in;
    }

    @AfterEach
    void tearDown() {
        // Restore the original System.in after each test
        System.setIn(originalSystemIn);
    }

    @Test
    void testReadSelection_ValidInput() {
        // GIVEN: Simulate a valid user input (e.g., "1")
        String input = "1\n"; // Add a newline to simulate pressing "Enter"
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readSelection method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil
        int result = inputReaderUtil.readSelection();

        // THEN: Verify that the method returns 1
        assertEquals(1, result, "The selection should be 1");
    }

    @Test
    void testReadSelection_InvalidInput() {
        // GIVEN: Simulate an invalid user input (e.g., "abc")
        String input = "abc\n"; // Add a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readSelection method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil
        int result = inputReaderUtil.readSelection();

        // THEN: Verify that the method returns -1
        assertEquals(-1, result, "The selection should be -1 for invalid input");
    }

    @Test
    void testReadSelection_EmptyInput() {
        // GIVEN: Simulate an empty user input
        String input = "\n"; // Simulate an empty input with a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readSelection method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil
        int result = inputReaderUtil.readSelection();

        // THEN: Verify that the method returns -1
        assertEquals(-1, result, "The selection should be -1 for empty input");
    }

    @Test
    void testReadVehicleRegistrationNumber_ValidInput() throws Exception {
        // GIVEN: Simulate a valid user input (e.g., "ABCDEF")
        String input = "ABCDEF\n"; // Add a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readVehicleRegistrationNumber method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil
        String result = inputReaderUtil.readVehicleRegistrationNumber();

        // THEN: Verify that the method returns "ABCDEF"
        assertEquals("ABCDEF", result, "The registration number should be ABCDEF");
    }

    @Test
    void testReadVehicleRegistrationNumber_EmptyInput() {
        // GIVEN: Simulate an empty user input
        String input = "\n"; // Simulate an empty input with a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readVehicleRegistrationNumber method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil

        // THEN: Verify that the method throws an IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            inputReaderUtil.readVehicleRegistrationNumber();
        });

        assertEquals("Invalid input provided", exception.getMessage(), "The exception message should match");
    }

    @Test
    void testReadVehicleRegistrationNumber_NullInput() {
        // GIVEN: Simulate an empty user input (since null is not supported)
        String input = "\n"; // Simulate an empty input with a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // WHEN: Call the readVehicleRegistrationNumber method
        Scanner scanner = new Scanner(System.in); // Create a Scanner for System.in
        InputReaderUtil inputReaderUtil = new InputReaderUtil(scanner); // Pass the Scanner to InputReaderUtil

        // THEN: Verify that the method throws an IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            inputReaderUtil.readVehicleRegistrationNumber();
        });

        assertEquals("Invalid input provided", exception.getMessage(), "The exception message should match");
    }
}