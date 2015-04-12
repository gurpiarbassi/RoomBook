package com.gurps.roombooking.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import com.gurps.roombooking.domain.BookingRequest;

public interface SchedulePrinterService {

    void print(Map<LocalDate, SortedSet<BookingRequest>> output) throws IOException;
}
