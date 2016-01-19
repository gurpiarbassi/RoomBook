package com.gurps.roombooking;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gurps.roombooking.service.MeetingSchedulerService;
import com.gurps.roombooking.service.MeetingSchedulerServiceImpl;

/**
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com 
 * This is the main class where the action starts. Two arguments need to be passed into the main method
 * arg1 = input file path 
 * arg2 = output file path
 */
public class MeetingScheduler {

    private static MeetingSchedulerService meetingSchedulerService;

    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("input path and output path should be specified.");
        } 
        
         Path inputFilePath = null;
         Path outputFilePath = null;
        	
        	try{
        		 inputFilePath = Paths.get(args[0]);
                 outputFilePath = Paths.get(args[1]);
                 System.out.println("input path : " + inputFilePath);
                 System.out.println("output path : " + outputFilePath);
        	}catch(final InvalidPathException e){
        		throw new IOException(e);
        	}
        	
            // TODO use DI to shield concrete implementation from client
            meetingSchedulerService = new MeetingSchedulerServiceImpl(inputFilePath, outputFilePath);

            meetingSchedulerService.produceSchedule();


    }
}
