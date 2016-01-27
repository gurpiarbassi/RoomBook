package com.gurps.roombooking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IBookingRequestScheduler {
    
    static final String OUT_FILE_DELIM = " ";

    Map<LocalDate, List<IBookingRequest>> schedule(final IBookingRequestBatch batch);
}
