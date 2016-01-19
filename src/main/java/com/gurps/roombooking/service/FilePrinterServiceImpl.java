package com.gurps.roombooking.service;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import com.gurps.roombooking.domain.IBookingRequest;

/**
 * File specific output service outputs to a given file path
 * @author gurps bassi gurpiar.bassi@gmail.com
 *
 */
public class FilePrinterServiceImpl implements SchedulePrinterService{

    private final Path outputFilePath;
    
    private static final String OUT_FILE_DELIM = " ";
    
    public FilePrinterServiceImpl(final Path outputFilePath){
     this.outputFilePath = outputFilePath;
    }
    
    @Override
    /**
     * @Param bookings All the successful booking requests
     * Formats the bookings and prints to a file. 
     */
    public void print(final Map<LocalDate, SortedSet<IBookingRequest>> bookings) throws IOException {
        final StringBuilder outputBuilder = new StringBuilder();
        final Set<Entry<LocalDate, SortedSet<IBookingRequest>>> entries = bookings.entrySet();
        final Iterator<Entry<LocalDate, SortedSet<IBookingRequest>>> meetingDaysIterator = entries.iterator();
        while(meetingDaysIterator.hasNext()){
            final Entry<LocalDate, SortedSet<IBookingRequest>> entry = meetingDaysIterator.next();
            final LocalDate meetingDate = entry.getKey();
            outputBuilder.append(meetingDate.toString() + System.getProperty("line.separator"));
            
            final Iterator<IBookingRequest> it = entry.getValue().iterator();
            while(it.hasNext()){
                final IBookingRequest bookingRequest = it.next();
                outputBuilder.append(bookingRequest.getMeetingStartTime())
                              .append(OUT_FILE_DELIM)
                              .append(bookingRequest.getMeetingEndDateTime().toLocalTime())
                              .append(OUT_FILE_DELIM)
                              .append(bookingRequest.getEmployeeId());
                if(it.hasNext()){
                    outputBuilder.append(System.getProperty("line.separator"));
                }
            }
            if(meetingDaysIterator.hasNext()){
                outputBuilder.append(System.getProperty("line.separator"));
            }
        }
        createDirectories(outputFilePath.getParent()); //create directory structure if not already present
        write(outputFilePath, outputBuilder.toString().getBytes());
    }

}
