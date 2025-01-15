package com.parkit.parkingsystem.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // To use Mockito with JUnit 5
public class DataBaseConfigTest {

    @Mock
    private Connection mockConnection; // Mock for Connection

    @Mock
    private PreparedStatement mockPreparedStatement; // Mock for PreparedStatement

    @Mock
    private ResultSet mockResultSet; // Mock for ResultSet

    //@Test
    //public void testGetConnection() throws Exception {
        // GIVEN: An instance of DataBaseConfig
        //DataBaseConfig dataBaseConfig = new DataBaseConfig();

        // WHEN: Calling getConnection()
        //Connection connection = dataBaseConfig.getConnection();

        // THEN: The connection should not be null
        //assertNotNull(connection); // Test to ensure a valid connection is returned
    //}

    @Test
    public void testGetConnectionThrowsException() {
        // GIVEN: An instance of DataBaseConfig with invalid connection parameters
        DataBaseConfig dataBaseConfig = new DataBaseConfig();

        // WHEN/THEN: Expect an exception to be thrown
        assertThrows(SQLException.class, () -> {
            dataBaseConfig.getConnection(); // Test to ensure an exception is thrown when connection fails
        });
    }

    @Test
    public void testCloseConnection() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked connection
        DataBaseConfig dataBaseConfig = new DataBaseConfig();

        // WHEN: Calling closeConnection() with the mocked connection
        dataBaseConfig.closeConnection(mockConnection);

        // THEN: The close() method of the connection should be called once
        verify(mockConnection, times(1)).close(); // Test to ensure the connection is properly closed
    }

    @Test
    public void testCloseConnectionWithException() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked connection that throws an exception
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        doThrow(new SQLException("Error closing connection")).when(mockConnection).close();

        // WHEN: Calling closeConnection() with the mocked connection
        dataBaseConfig.closeConnection(mockConnection);

        // THEN: The close() method of the connection should be called once
        verify(mockConnection, times(1)).close(); // Test to ensure the close method is called even if an exception occurs
    }

    @Test
    public void testClosePreparedStatement() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked PreparedStatement
        DataBaseConfig dataBaseConfig = new DataBaseConfig();

        // WHEN: Calling closePreparedStatement() with the mocked PreparedStatement
        dataBaseConfig.closePreparedStatement(mockPreparedStatement);

        // THEN: The close() method of the PreparedStatement should be called once
        verify(mockPreparedStatement, times(1)).close(); // Test to ensure the PreparedStatement is properly closed
    }

    @Test
    public void testClosePreparedStatementWithException() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked PreparedStatement that throws an exception
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        doThrow(new SQLException("Error closing prepared statement")).when(mockPreparedStatement).close();

        // WHEN: Calling closePreparedStatement() with the mocked PreparedStatement
        dataBaseConfig.closePreparedStatement(mockPreparedStatement);

        // THEN: The close() method of the PreparedStatement should be called once
        verify(mockPreparedStatement, times(1)).close(); // Test to ensure the close method is called even if an exception occurs
    }

    @Test
    public void testCloseResultSet() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked ResultSet
        DataBaseConfig dataBaseConfig = new DataBaseConfig();

        // WHEN: Calling closeResultSet() with the mocked ResultSet
        dataBaseConfig.closeResultSet(mockResultSet);

        // THEN: The close() method of the ResultSet should be called once
        verify(mockResultSet, times(1)).close(); // Test to ensure the ResultSet is properly closed
    }

    @Test
    public void testCloseResultSetWithException() throws SQLException {
        // GIVEN: An instance of DataBaseConfig and a mocked ResultSet that throws an exception
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        doThrow(new SQLException("Error closing result set")).when(mockResultSet).close();

        // WHEN: Calling closeResultSet() with the mocked ResultSet
        dataBaseConfig.closeResultSet(mockResultSet);

        // THEN: The close() method of the ResultSet should be called once
        verify(mockResultSet, times(1)).close(); // Test to ensure the close method is called even if an exception occurs
    }
}