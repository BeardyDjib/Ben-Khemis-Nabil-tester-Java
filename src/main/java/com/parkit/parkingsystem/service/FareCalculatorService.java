package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();

        //fixed in millisecondes
        long duration = (outTime - inTime);

        //Free if less than 30 minutes parking
        if (duration <= 30 * 60 * 1000) {
            ticket.setPrice(0);
            return;
        }
        double hours = (double) duration / (60 * 60 * 1000);


        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(hours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(hours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
        if (discount) {
            ticket.setPrice(ticket.getPrice() * 0.95); // Apply 5% discount
        }
    }

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }
}