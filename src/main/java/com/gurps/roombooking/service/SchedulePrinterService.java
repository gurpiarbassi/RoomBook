package com.gurps.roombooking.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import com.gurps.roombooking.domain.IBookingRequest;

public interface SchedulePrinterService {

    void print(final Map<LocalDate, SortedSet<IBookingRequest>> output) throws IOException;
}
