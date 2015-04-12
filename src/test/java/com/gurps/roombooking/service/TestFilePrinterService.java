package com.gurps.roombooking.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.service.FilePrinterServiceImpl;
import com.gurps.roombooking.service.SchedulePrinterService;

public class TestFilePrinterService {

    private String getHomeDir(){
        return System.getProperty("user.home");
    }
   
    @Test
     public void testPrintOfEmptyDataSet(){
        Map<LocalDate, SortedSet<BookingRequest>> output = new TreeMap<>();
        SchedulePrinterService printer = new FilePrinterServiceImpl(getHomeDir() + File.separator + " output.txt");
        try{
            printer.print(output);
            
            //check file is empty
            assertEquals(0, Files.size(Paths.get(getHomeDir() + File.separator + " output.txt")));
        }catch(IOException e){
            e.printStackTrace();
            fail("Exception caught. Test failed!");
        }
     }
    
    @Test
    /**
     * This is the scenario given in the exercise
     * 2011-03-21
       09:00 11:00 EMP002
       2011-03-22
       14:00 16:00 EMP003
       16:00 17:00 EMP004
     */
    public void testPrintSampleData(){
        Map<LocalDate, SortedSet<BookingRequest>> output = new TreeMap<>();
        SortedSet<BookingRequest> set1 = new TreeSet<>();
        SortedSet<BookingRequest> set2 = new TreeSet<>();
        
       
        BookingRequest bookingRequest1 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(12, 34, 56),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP002");
        
        set1.add(bookingRequest1);
        
        BookingRequest bookingRequest2 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(9, 28, 23),
                LocalDate.of(2011, 3, 22), LocalTime.of(14, 0), 2,"EMP003");
        
        BookingRequest bookingRequest3 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(11, 23, 45),
                LocalDate.of(2011, 3, 22), LocalTime.of(16, 0), 1,"EMP004");
        
        set2.add(bookingRequest2);
        set2.add(bookingRequest3);
        
        output.put(LocalDate.of(2011, 3, 21), set1);
        output.put(LocalDate.of(2011, 3, 22), set2);
        
        SchedulePrinterService printer = new FilePrinterServiceImpl(getHomeDir() + File.separator + " output.txt");
        try{
            printer.print(output);            
            //check file is populated
            assertNotEquals(0, Files.size(Paths.get(getHomeDir() + File.separator + " output.txt")));
            
            
            //read contents of file and compare with expected output;
            List<String> allLines = Files.readAllLines(Paths.get(getHomeDir() + File.separator + " output.txt"), Charset.defaultCharset());
            assertEquals(5, allLines.size());
            assertEquals("2011-03-21", allLines.get(0));
            assertEquals("09:00 11:00 EMP002", allLines.get(1));
            assertEquals("2011-03-22", allLines.get(2));
            assertEquals("14:00 16:00 EMP003", allLines.get(3));
            assertEquals("16:00 17:00 EMP004", allLines.get(4));
            
        }catch(IOException e){
            e.printStackTrace();
            fail("Exception caught. Test failed!");
        }
        
    }
    
    //TOO Refactor to utility method since can be used by more than one Test class
    private BookingRequest makeBookingRequest(final LocalDate requestDate, final LocalTime requestTime, final LocalDate meetingDate,
            final LocalTime meetingStartTime, final int duration, final String employeeNumber){
        return new BookingRequest.BookingRequestBuilder(requestDate, requestTime)
        .meetingDate(meetingDate)
        .meetingStart(meetingStartTime)
        .duration(duration)
        .employee(employeeNumber)
        .build();
    }
}
