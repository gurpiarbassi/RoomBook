package com.gurps.roombooking.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import com.gurps.roombooking.domain.BookingRequest;

/**
 * File specific output service outputs to a given file path
 * @author gurps bassi gurpiar.bassi@gmail.com
 *
 */
public class FilePrinterServiceImpl implements SchedulePrinterService{

    private String outputFilePath;
    
    private static final String OUT_FILE_DELIM = " ";
    
    public FilePrinterServiceImpl(String outputFilePath){
     this.outputFilePath = outputFilePath;
    }
    
    @Override
    /**
     * @Param bookings All the successful booking requests
     * Formats the bookings and prints to a file. 
     */
    public void print(Map<LocalDate, SortedSet<BookingRequest>> bookings) throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        Set<Entry<LocalDate, SortedSet<BookingRequest>>> entries = bookings.entrySet();
        Iterator<Entry<LocalDate, SortedSet<BookingRequest>>> meetingDaysIterator = entries.iterator();
        while(meetingDaysIterator.hasNext()){
            Entry<LocalDate, SortedSet<BookingRequest>> entry = meetingDaysIterator.next();
            LocalDate meetingDate = entry.getKey();
            outputBuilder.append(meetingDate.toString() + System.getProperty("line.separator"));
            
            Iterator<BookingRequest> it = entry.getValue().iterator();
            while(it.hasNext()){
                BookingRequest bookingRequest = it.next();
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
        Files.createDirectories(Paths.get(outputFilePath).getParent()); //create directory structure if not already present
        Files.write(Paths.get(outputFilePath), outputBuilder.toString().getBytes());
    }

}
