package com.gurps.roombooking.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.IBookingRequest;

public class TestFilePrinterService {

	@Rule
	public final TemporaryFolder tmpFolder = new TemporaryFolder();
	
	private Path outputFilePath;
	
	@Before
	public void setup() throws IOException{
		outputFilePath = tmpFolder.newFolder().toPath().resolve("output.txt");
	}
   
    @Test
     public void testPrintOfEmptyDataSet(){
        final Map<LocalDate, SortedSet<IBookingRequest>> output = new TreeMap<>();
        final SchedulePrinterService printer = new FilePrinterServiceImpl(outputFilePath);
        try{
            printer.print(output);
            
            //check file is empty
            assertEquals(0, Files.size(outputFilePath));
        }catch(final IOException e){
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
        final Map<LocalDate, SortedSet<IBookingRequest>> output = new TreeMap<>();
        final SortedSet<IBookingRequest> set1 = new TreeSet<>();
        final SortedSet<IBookingRequest> set2 = new TreeSet<>();
        
       
        final IBookingRequest bookingRequest1 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(12, 34, 56),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP002");
        
        set1.add(bookingRequest1);
        
        final IBookingRequest bookingRequest2 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(9, 28, 23),
                LocalDate.of(2011, 3, 22), LocalTime.of(14, 0), 2,"EMP003");
        
        final IBookingRequest bookingRequest3 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(11, 23, 45),
                LocalDate.of(2011, 3, 22), LocalTime.of(16, 0), 1,"EMP004");
        
        set2.add(bookingRequest2);
        set2.add(bookingRequest3);
        
        output.put(LocalDate.of(2011, 3, 21), set1);
        output.put(LocalDate.of(2011, 3, 22), set2);
        
        final SchedulePrinterService printer = new FilePrinterServiceImpl(outputFilePath);
        try{
            printer.print(output);            
            //check file is populated
            assertNotEquals(0, Files.size(outputFilePath));
            
            
            //read contents of file and compare with expected output;
            final List<String> allLines = Files.readAllLines(outputFilePath, Charset.defaultCharset());
            assertEquals(5, allLines.size());
            assertEquals("2011-03-21", allLines.get(0));
            assertEquals("09:00 11:00 EMP002", allLines.get(1));
            assertEquals("2011-03-22", allLines.get(2));
            assertEquals("14:00 16:00 EMP003", allLines.get(3));
            assertEquals("16:00 17:00 EMP004", allLines.get(4));
            
        }catch(final IOException e){
            e.printStackTrace();
            fail("Exception caught. Test failed!");
        }
        
    }
    
    //TOO Refactor to utility method since can be used by more than one Test class
    private BookingRequest makeBookingRequest(final LocalDate requestDate, final LocalTime requestTime, final LocalDate meetingDate,
            final LocalTime meetingStartTime, final int duration, final String employeeNumber){
        return  BookingRequest.BookingRequestBuilder.aBookingRequest(requestDate, requestTime).withMeetingDate(meetingDate)
																						       .withMeetingStartTime(meetingStartTime)
																						       .withMeetingDuration(duration)
																						       .withEmployee(employeeNumber)
																						       .build();
    }
}
